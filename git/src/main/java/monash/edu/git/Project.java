package monash.edu.git;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class Project {
    private String projectName;
    private ArrayList<GitRepository> repositories;
    private String email;

    public Project(String name) {
        this.projectName = name;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getEmail() {
        return email;
    }

    public JSONObject getRepositories() throws JSONException {
        JSONObject reposObject = new JSONObject();
        JSONArray repos = new JSONArray();
        for (GitRepository repo : repositories) {
            repos.put(repo.getInfo());
        }
        reposObject.append("repos", repos);
        return reposObject;
    }

    public boolean hasRepository(String gitUserName, String gitURL) {
        for (GitRepository repo : repositories) {
            JSONObject info = repo.getInfo();
            if (info["name"].equals(gitUserName) && info["URL"].equals(gitURL)) {
                return true;
            }
        }
        return false;
    }

    public void addRepository(String gitUsername, String gitURL) {
        if (!hasRepository(gitUsername, gitURL)) {
            repositories.add(new GitRepository(gitUsername, gitURL));
        }
    }

}
