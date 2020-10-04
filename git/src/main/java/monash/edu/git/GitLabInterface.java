package monash.edu.git;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GitLabInterface {

    public JSONArray getRepoContributors(String repoURL) throws IOException, JSONException {
        JSONArray contributors = new JSONArray();
        String reposUrl = "https://api.github.com/repos/" + getBaseAPIURL(repoURL) + "/repository/";

        // Class that reads from a URL and returns info in JSON format
        GetJSONReader jsonReader= new GetJSONReader();
        JSONObject json = jsonReader.readJsonFromUrl(reposUrl);

        // Extracting the array from the JSON Object
        JSONArray jsonArray = json.getJSONArray("entry");

        // Calculating the total cotributions
        int totalContributions=0;
        for(int i=0;i<jsonArray.length();i++)
        {
            totalContributions=totalContributions+jsonArray.getJSONObject(i).getInt("contributions");
        }

        // Calculating the contribution percentage of each team member in the repo
        for(int i=0;i<jsonArray.length();i++)
        {
            int individualContribution=jsonArray.getJSONObject(i).getInt("contributions");
            double contributionPercent=((double)individualContribution/totalContributions)*100;
            contributionPercent=Math.round(contributionPercent*100.0)/100.0;
            String name=jsonArray.getJSONObject(i).getString("login");
            JSONObject contributor = new JSONObject();
            contributor.put("username", name);
            contributor.put("contribution", contributionPercent);
            contributors.put(contributor);
        }
        return contributors;
    }


    public JSONArray getRepoCommits(String repoURL, String accessToken) throws IOException, JSONException {
        JSONArray commits =new JSONArray();

        String commitsUrl = "https://git.infotech.monash.edu/api/v4/projects/" + getBaseAPIURL(repoURL) + "/repository/commits?" +
        "access_token="+accessToken+"";

        // Class that reads from a URL and returns info in JSON format
        GetJSONReader jsonReader= new GetJSONReader();

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T':hh:mm:ss");
        String commitDate = dateFormat.format(date);

        String extendedUrl;
        JSONObject response, previous = new JSONObject();
        JSONArray jsonArray;
        // TODO: Wrap in a try catch to handle potential errors
        boolean firstpage = true;
        do {
            // Add extra parameters to URL
            extendedUrl = commitsUrl+"&until="+commitDate;
            response = jsonReader.readJsonFromUrl(extendedUrl);
            // Extracting the array from the JSON Object
            jsonArray = response.getJSONArray("entry");
            for (int i=0;i<jsonArray.length();i++)
            {  
                if (firstpage || i != 0){
                    String name=jsonArray.getJSONObject(i).getString("author_name");
                    boolean exists = false;
                    for (int j=0; j < commits.length(); j++) {
                        JSONObject commitor = commits.getJSONObject(j);
                        if (commitor.get("name").equals(name)) {
                            commitor.put("commits", commitor.getInt("commits")+1);
                            exists = true;
                        }
                    }
                    if (!exists) {
                        JSONObject newCommitor = new JSONObject();
                        newCommitor.put("name", name);
                        newCommitor.put("commits", 1);
                        commits.put(newCommitor);
                    }
                }
            }
            firstpage = false;
            previous = jsonArray.getJSONObject(jsonArray.length()-1);
            commitDate = ((JSONObject) jsonArray.get(jsonArray.length()-1)).get("authored_date").toString();
            //constructRepoContributions(branchcommits);
        } while (jsonArray.length() == 20);
        // Loop that goes through all the commits and extracts its authors
        // Also increments commit number if author already exists in commit JSONObject
        return commits;
    }

    private String getBaseAPIURL(String baseURL) {
        String[] urlComponents = baseURL.split("/");
        String returnValue = "";
        for (int i=3; i < urlComponents.length; i++) {
            returnValue += urlComponents[i];
            if (i < urlComponents.length-1) {
                returnValue += "%2F";
            }
        }
        return returnValue;
    }
}
