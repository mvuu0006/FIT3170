package monash.edu.git;

import java.io.IOException;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GitHubInterface {


    public JSONArray getRepoContributors(String repoURL) throws IOException, JSONException {
        JSONArray contributors = new JSONArray();

        String[] urlComponents = repoURL.split("/");
        String username = urlComponents[urlComponents.length-2];
        String repoName = urlComponents[urlComponents.length-1];
        
        // Creating a URL
        String reposUrl = "https://api.github.com/repos/" + username + "/" + repoName + "/contributors";

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


    public JSONArray getRepoCommits(String repoURL) throws IOException, JSONException {
        JSONArray commits =new JSONArray();

        // Former code that used repository URL instead of ID
        // // Creating the URL
        // String[] urlComponents = repoURL.split("/");
        // String username = urlComponents[urlComponents.length-2];
        // String repoName = urlComponents[urlComponents.length-1];

        // String commitsUrl = "https://api.github.com/repos/" + username + "/" + repoName + "/commits";
        String commitsUrl = "https://api.github.com/repositories/" + repoURL + "/commits";

        // Class that reads from a URL and returns info in JSON format
        GetJSONReader jsonReader= new GetJSONReader();
        JSONObject json = jsonReader.readJsonFromUrl(commitsUrl);

        // Extracting the array from the JSON Object
        JSONArray jsonArray = json.getJSONArray("entry");
        // Collate commits list
        for (int i=0;i<jsonArray.length();i++)
        {
            JSONObject commit_info = new JSONObject();
            // Add author name to commit_info
            commit_info.put("author", jsonArray.getJSONObject(i).getJSONObject("commit").getJSONObject("committer").getString("name"));
            // Add commit message to commit_info
            commit_info.put("message", jsonArray.getJSONObject(i).getJSONObject("commit").getString("message"));
            // Add commit date to commit_info
            commit_info.put("date", jsonArray.getJSONObject(i).getJSONObject("commit").getJSONObject("committer").getString("date"));
            // Add author email to commit_info
            commit_info.put("author", jsonArray.getJSONObject(i).getJSONObject("commit").getJSONObject("committer").getString("email"));
            // Add commit to array
            commits.put(commit_info);
        }
        return commits;
    }

    public JSONArray getLastContributions(String repoURL) throws IOException, JSONException {
        JSONArray lastmodified = new JSONArray();
        JSONObject lastthing;
        JSONArray contributors = this.getRepoCommits(repoURL);
        boolean found = false;
        for (int i = 0; i < contributors.length(); i++){
            found = false;
            for (int j = 0; j < lastmodified.length(); j++){
                if (lastmodified.getJSONObject(j).getString("email").equals(contributors.getJSONObject(i).getString("email"))){
                    found = true;
                    break;
                }
            }
            if (found == false){
                lastthing = new JSONObject();
                lastthing.put("email",contributors.getJSONObject(i).getString("email"));
                lastthing.put("date", contributors.getJSONObject(i).getString("date"));
                lastmodified.put(lastthing);
            }
        }
        return lastmodified;
    }

    public String getIdFromURL(String repoURL) throws IOException, JSONException {
        // Creating the URL
        String[] urlComponents = repoURL.split("/");
        String username = urlComponents[urlComponents.length-2];
        String repoName = urlComponents[urlComponents.length-1];

        String githubUrl = "https://api.github.com/repos/" + username + "/" + repoName;

        // Class that reads from a URL and returns info in JSON format
        GetJSONReader jsonReader= new GetJSONReader();
        JSONObject json = jsonReader.readJsonFromUrl(githubUrl);
        return json.getJSONObject("entry").getString("id").toString();
    }

    public JSONObject getRepoInfo(String repoURL) throws IOException, JSONException {
        // Creating the URL
        String infoURL = "https://api.github.com/repositories/" + repoURL;

        // Class that reads from a URL and returns info in JSON format
        GetJSONReader jsonReader= new GetJSONReader();
        JSONObject json = jsonReader.readJsonFromUrl(infoURL);

        // Extracting the array from the JSON Object
        JSONObject jsonObject = json.getJSONObject("entry");
        return jsonObject;
    }
}
