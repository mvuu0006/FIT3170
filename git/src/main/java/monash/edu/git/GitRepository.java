package monash.edu.git;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class GitRepository {
    private JSONObject contributors;
    private JSONObject commits;
    private JSONObject repoInfo;
    private GetJSONReader jsonReader;
    public String githubUsername;
    public String repoName;

    public GitRepository(String gitUsername, String repoName) {
        jsonReader = new GetJSONReader();
        this.githubUsername = gitUsername;
        this.repoName = repoName;
        constructRepoInfo(gitUsername, repoName);
    }

    private void constructRepoInfo(String gitUsername, String repoName) {
        String urlString = "https://api.github.com/repos/" + gitUsername + "/" + repoName;
        JSONObject response = new JSONObject();
        try {
            response = jsonReader.readJsonFromUrl(urlString);
        }
        catch(IOException e) {

        }
        catch(JSONException e) {

        }
        repoInfo = response;
        constructRepoContributors(gitUsername, repoName);
        constructRepoCommits(gitUsername, repoName);
    }

    private void constructRepoContributors(String gitUsername, String repoName) {
        String urlString = "https://api.github.com/repos/" + gitUsername + "/" + repoName + "/contributors";
        JSONObject response = new JSONObject();
        try {
            response = jsonReader.readJsonFromUrl(urlString);
        }
        catch(IOException e) {

        }
        catch(JSONException e) {

        }
        contributors = response;
    }

    private void constructRepoCommits(String gitUsername, String repoName) {
        String urlString = "https://api.github.com/repos/" + gitUsername + "/" + repoName + "/commits";
        JSONObject response = new JSONObject();
        try {
            response = jsonReader.readJsonFromUrl(urlString);
        }
        catch(IOException e) {

        }
        catch(JSONException e) {

        }
        commits = response;
    }

    public JSONObject getInfo() {
        return repoInfo;
    }
}