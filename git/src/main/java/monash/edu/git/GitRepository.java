package monash.edu.git;

import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

public class GitRepository {
    private JSONObject contributors;
    private JSONObject commits;
    private JSONObject repoInfo;
    private GetJSONReader jsonReader;
    public String githubUsername;
    public String repoName;
    GitService gitService=new GitService();

    public GitRepository(String gitUsername, String repoName) throws IOException, JSONException {
        jsonReader = new GetJSONReader();
        this.githubUsername = gitUsername;
        this.repoName = repoName;
        constructRepoInfo(gitUsername, repoName);
    }

    private void constructRepoInfo(String gitUsername, String repoName) throws IOException, JSONException {
        String urlString = "https://api.github.com/repos/" + gitUsername + "/" + repoName;
        JSONObject response = new JSONObject();
        try {
            response = jsonReader.readJsonFromUrl(urlString);
        }
        catch(IOException | JSONException e) {
            e.printStackTrace();
        }
        repoInfo = response;
        constructRepoContributors(gitUsername, repoName);
        constructRepoCommits(gitUsername, repoName);
    }

    private void constructRepoContributors(String gitUsername, String repoName) throws IOException, JSONException {
        contributors=gitService.getContributions(gitUsername,repoName);
    }

    private void constructRepoCommits(String gitUsername, String repoName) throws IOException, JSONException {
        commits=gitService.getCommits(gitUsername,repoName);
    }

    public JSONObject getInfo() {
        return repoInfo;
    }
}