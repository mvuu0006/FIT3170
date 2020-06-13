package monash.edu.git;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;


public class GitService {
    //GitRepository gitRepository=new GitRepository();

    public String getContributions() throws IOException, JSONException {
        //Creating a JSONObject that stores all the contributions
        JSONObject contribution = new JSONObject();

        // Original URL : https://github.com/tensorflow/tensorflow.git
        String reposUrl="https://api.github.com/repos/tensorflow/tensorflow/contributors?per_page=500";
        reposUrl="https://api.github.com/repos/octocat/Hello-World/contributors";


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

            contribution.put(name,contributionPercent);
        }
        //gitRepository.setContributors(contribution);
        return contribution.toString();
    }

    public String getCommits() throws IOException, JSONException {
        //JSONObject to store commit information
        JSONObject commits = new JSONObject();

        // Hardcoded commits URL
        String commitsUrl="https://api.github.com/repos/octocat/Hello-World/commits";

        // Class that reads from a URL and returns info in JSON format
        GetJSONReader jsonReader= new GetJSONReader();
        JSONObject json = jsonReader.readJsonFromUrl(commitsUrl);

        // Extracting the array from the JSON Object
        JSONArray jsonArray = json.getJSONArray("entry");

        // Loop that goes through all the commits and extracts its authors
        // Also increments commit number if author already exists in commit JSONObject
        for (int i=0;i<jsonArray.length();i++)
        {
            String name=jsonArray.getJSONObject(i).getJSONObject("commit").getJSONObject("committer").getString("name");
            if (commits.has(name))
            {
                int existingComments=commits.getInt(name)+1;
                commits.put(name, existingComments);
            }
            else {
                commits.put(name,1);
            }
        }

        //gitRepository.setCommits(commits);

        return commits.toString();
    }
}
