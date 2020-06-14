package monash.edu.git;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GitRepository {
    private JSONObject contributors;
    private JSONObject commits;
    private JSONObject repoInfo;
    public String githubUsername;
    public String repoName;
    private String gitId;

    // Constructor that creates the Repository object based on username and reponame
    public GitRepository(String gitUsername, String repoName) throws IOException, JSONException {
        this.githubUsername = gitUsername;
        this.repoName = repoName;
        constructRepoInfo(gitUsername, repoName);
    }

    // Constructor that creates the Repository object based on ID
    public GitRepository(String id) throws IOException, JSONException {
        this.gitId=id;
        String gitUrl = "https://api.github.com/repositories/" + id;

        // Class that reads from a URL and returns info in JSON format
        GetJSONReader jsonReader= new GetJSONReader();
        JSONObject json = jsonReader.readJsonFromUrl(gitUrl);

        // Extracting the array from the JSON Object
        JSONObject jsonObject = json.getJSONObject("entry");

        // Getting the username and reponame
        this.repoName=jsonObject.getString("name");
        this.githubUsername=jsonObject.getJSONObject("owner").getString("login");
        constructRepoInfo(this.githubUsername,this.repoName);

    }

    // Function that creates the RepoInfo JSOn object that will be used to display information on the front end
    private void constructRepoInfo(String gitUsername, String repoName) throws IOException, JSONException {
        constructRepoContributors(gitUsername, repoName);
        constructRepoCommits(gitUsername, repoName);
        repoInfo=new JSONObject();
        repoInfo.put("UserName", gitUsername);
        repoInfo.put("RepositoryName", repoName);
        repoInfo.put("commits", commits);
        repoInfo.put("contributions", contributors);
    }

    private void constructRepoContributors(String gitUsername, String repoName) throws IOException, JSONException {
        contributors=new JSONObject();

        // Creating a URL
        String reposUrl = "https://api.github.com/repos/" + gitUsername + "/" + repoName + "/contributors";

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

            contributors.put(name,contributionPercent);
        }

    }

    private void constructRepoCommits(String gitUsername, String repoName) throws IOException, JSONException {
        commits=new JSONObject();

        // Creating the URL
        String commitsUrl = "https://api.github.com/repos/" + gitUsername + "/" + repoName + "/commits";

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
    }

    public JSONObject getInfo() {
        return repoInfo;
    }

    public  JSONObject getContributors(){
        return contributors;
    }

    public  JSONObject getCommits(){
        return commits;
    }

    public String getGitId() {
        return gitId;
    }
}