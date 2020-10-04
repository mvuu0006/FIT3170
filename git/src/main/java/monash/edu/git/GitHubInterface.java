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
        System.out.println(jsonArray.toString());

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

        // Creating the URL
        String[] urlComponents = repoURL.split("/");
        String username = urlComponents[urlComponents.length-2];
        String repoName = urlComponents[urlComponents.length-1];

        String commitsUrl = "https://api.github.com/repos/" + username + "/" + repoName + "/commits";

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
            // Add commit to array
            commits.put(commit_info);
        }
        return commits;
    }
}
