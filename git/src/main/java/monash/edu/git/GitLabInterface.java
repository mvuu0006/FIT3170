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

        // Former code that uses repo URL, not repo ID
        // String commitsUrl = "https://git.infotech.monash.edu/api/v4/projects/" + getBaseAPIURL(repoURL) + "/repository/commits?" +
        // "access_token="+accessToken+"";
        String commitsUrl = "https://git.infotech.monash.edu/api/v4/projects/" + repoURL + "/repository/commits?" +
        "access_token="+accessToken+"&all=true";

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
        int page = 1;
        do {
            // Add extra parameters to URL
            extendedUrl = commitsUrl+"&page="+page;
            response = jsonReader.readJsonFromUrl(extendedUrl);
            // Extracting the array from the JSON Object
            jsonArray = response.getJSONArray("entry");
            for (int i=jsonArray.length()-1;i>0;i--)
            {  
                if (firstpage || i != 0){
                    // Add nicely-formatted commits to list
                    JSONObject commit_info = new JSONObject();
                    // Add author name to commit_info
                    commit_info.put("author", jsonArray.getJSONObject(i).getString("author_name"));
                    // Add commit message to commit_info
                    commit_info.put("message", jsonArray.getJSONObject(i).getString("message"));
                    // Add commit date to commit_info
                    commit_info.put("date", jsonArray.getJSONObject(i).getString("authored_date"));
                    // Add author email to commit_info
                    commit_info.put("email", jsonArray.getJSONObject(i).getString("author_email"));
                    // Add commit to array
                    commits.put(commit_info);
                }
            }
            firstpage = false;
            page += 1;
            previous = jsonArray.getJSONObject(jsonArray.length()-1);
            commitDate = ((JSONObject) jsonArray.get(jsonArray.length()-1)).get("authored_date").toString();
            //constructRepoContributions(branchcommits);
        } while (jsonArray.length() == 20);
        // Loop that goes through all the commits and extracts its authors
        // Also increments commit number if author already exists in commit JSONObject
        return commits;
    }

    public JSONObject getLastContributions(String repoURL, String accessToken) throws IOException, JSONException {
        JSONArray contributors = new JSONArray();
        contributors = this.getRepoCommits(repoURL, accessToken);
        JSONObject lastchanged = new JSONObject();
        for (int i = 0; i < contributors.length(); i++){
            if (!lastchanged.has(contributors.getJSONObject(i).getString("email"))){
                lastchanged.put(contributors.getJSONObject(i).getString("email"), contributors.getJSONObject(i).getString("date"));
            }
        }
        return lastchanged;
    }

    public JSONObject getRepoInfo(String repoURL, String accessToken) throws IOException, JSONException {
        // Creating the URL
        String infoUrl = "https://git.infotech.monash.edu/api/v4/projects/"+repoURL+"?" +
        "access_token="+accessToken+"";

        GetJSONReader jsonReader= new GetJSONReader();
        JSONObject json = jsonReader.readJsonFromUrl(infoUrl);

        // Extracting the array from the JSON Object
        JSONObject jsonObject = json.getJSONObject("entry");
        return jsonObject;
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

    public String getIdFromURL(String repoURL, String accessToken) throws IOException, JSONException {
        // Creating the URL
        String gitlabUrl = "https://git.infotech.monash.edu/api/v4/projects/" + getBaseAPIURL(repoURL) + "?" +
        "access_token="+accessToken+"";

        // Class that reads from a URL and returns info in JSON format
        GetJSONReader jsonReader= new GetJSONReader();
        JSONObject json = jsonReader.readJsonFromUrl(gitlabUrl);
        return json.getJSONObject("entry").getString("id").toString();
    }
}
