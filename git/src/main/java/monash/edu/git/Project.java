package monash.edu.git;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Project {
    private String projectName;
    private ArrayList<GitRepository> repositories;
    private String email="";
    private String id;

    public Project(String name, String id) {
        this.projectName = name;
        this.id = id;
        repositories = new ArrayList<GitRepository>();
    }

    public String getProjectName() {
        return projectName;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public JSONArray getRepositories() throws JSONException {
        // Loop that returns a JSONArray of all the repos
        JSONArray repos = new JSONArray();
        for (GitRepository repo : repositories) {
            repos.put(repo.getInfo());
        }
        return repos;
    }

    // Method that gets a repo from the project by username
    public GitRepository getRepositoryByUserName(String gitUserName, String gitURL) {
        for (GitRepository repo : repositories) {
            if (gitUserName.equals(repo.githubUsername) && gitURL.equals(repo.repoName)) {
                return repo;
            }
        }
        return null;
    }

    // Method that gets a repo from the project by ID
    public GitRepository getRepositoryByID(String id) {
        for (GitRepository repo : repositories) {
            if (id.equals(repo.getGitId())) {
                return repo;
            }
        }
        return null;
    }

    // Method that adds repository by username
    public void addRepositoryByUsername(String gitUsername, String gitURL) {
        if (getRepositoryByUserName(gitUsername, gitURL) == null) {
            try {
                GitRepository newRepo = new GitRepository(gitUsername, gitURL);
                repositories.add(newRepo);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // Method that adds repository by ID
    public void addRepositoryByID(String id) throws NoRepoException {
        if (getRepositoryByID(id) == null) {
            try {
                repositories.add(new GitRepository(id));
            } catch (IOException | JSONException e) {
                //e.printStackTrace();
                if (e instanceof FileNotFoundException) {
                    throw new NoRepoException();
                }
            }
        }
        System.out.println(repositories.size());
    }

    // Method that returns a JSONObject containing project info
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
