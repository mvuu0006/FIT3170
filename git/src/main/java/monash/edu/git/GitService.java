package monash.edu.git;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;


public class GitService {
    String gitUrl="https://api.github.com/projects/1002604";

    public JSONObject getContributions() throws IOException, JSONException {
        //Creating a JSONObject that stores all the contributions
        JSONObject contribution = new JSONObject();

        // Original URL : https://github.com/tensorflow/tensorflow.git
        String reposUrl="https://api.github.com/repos/tensorflow/tensorflow/contributors?per_page=500";

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

        return contribution;
    }
}
