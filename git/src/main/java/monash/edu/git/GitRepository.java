package monash.edu.git;

import org.json.JSONObject;

public class GitRepository {
    private JSONObject commits;
    private JSONObject contributors;

    public void setCommits(JSONObject commits) {
        this.commits = commits;
    }

    public void setContributors(JSONObject contributors) {
        this.contributors = contributors;
    }
}
