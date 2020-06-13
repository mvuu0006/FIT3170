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
        repositories = new ArrayList<GitRepository>();
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
            //repos.put(repo.getInfo());
        }
        reposObject.append("repos", repos);
        return reposObject;
    }

    public GitRepository getRepository(String gitUserName, String gitURL) {
        for (GitRepository repo : repositories) {
            if (gitUserName.equals(repo.githubUsername) && gitURL.equals(repo.repoName)) {
                return repo;
            }
        }
        return null;
    }

    public void addRepository(String gitUsername, String gitURL) {
        if (getRepository(gitUsername, gitURL) == null) {
            repositories.add(new GitRepository(gitUsername, gitURL));
        }
    }

}
