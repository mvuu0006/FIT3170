package monash.edu.git;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class Project {
    private String projectName;
    private ArrayList<GitRepository> repositories;
    private String email="";

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

    public JSONArray getRepositories() throws JSONException {
        //JSONObject reposObject = new JSONObject();
        JSONArray repos = new JSONArray();
        for (GitRepository repo : repositories) {
            repos.put(repo.getInfo());
        }
        //reposObject.append("repos", repos);
        return repos;
    }

    public GitRepository getRepositoryByUserName(String gitUserName, String gitURL) {
        for (GitRepository repo : repositories) {
            if (gitUserName.equals(repo.githubUsername) && gitURL.equals(repo.repoName)) {
                return repo;
            }
        }
        return null;
    }

    public GitRepository getRepositoryByID(String id) {
        for (GitRepository repo : repositories) {
            if (id.equals(repo.getGitId())) {
                return repo;
            }
        }
        return null;
    }

    public void addRepositoryByUsername(String gitUsername, String gitURL) {
        if (getRepositoryByUserName(gitUsername, gitURL) == null) {
            try {
                repositories.add(new GitRepository(gitUsername, gitURL));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void addRepositoryByID(String id) {
        if (getRepositoryByID(id) == null) {
            try {
                repositories.add(new GitRepository(id));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }



    public JSONObject getProjectInfo()
    {
        JSONObject projectInfo= new JSONObject();
        try {
            projectInfo.put("projectName", projectName);
            projectInfo.put("email", email);
            projectInfo.put("repositories",repositories);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return projectInfo;
    }

}
