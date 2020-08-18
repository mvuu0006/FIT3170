package monash.edu.git;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


@RestController
@RequestMapping("/git")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "http://localhost:3003"}, maxAge = 0)
public class GitController {
    private ArrayList<Project> projects = new ArrayList<Project>();

    /**
     * Returns the project data stored in the system. I
     * @param id the user to retrieve repo data or
     * @return  a JSON object containing user info
     * @throws JSONException
     */
    @CrossOrigin
    @GetMapping(path = "/project/{projID}", produces="application/json")
    @ResponseBody
    public String getProject(@PathVariable("projID") String id) throws JSONException, NoEntryException {
        JSONObject response = new JSONObject();
        if( id.equals("") ) {
            throw new NoEntryException();
        }
        else {
            JSONObject body = new JSONObject();
            boolean found = false;
            for (Project project: projects) {
                if (project.getId().equals(id)) {
                    body.put("project", project.getProjectName());
                    found = true;
                }
            }
            if (!found) {
                throw new NoEntryException();
            }
            response.put("body", body);
            response.put("status", 200);
        }
        return response.toString();
    }

    @PutMapping(path = "/project")
    @ResponseStatus(code = HttpStatus.CREATED)
    @ResponseBody
    public void putUser(@RequestBody String req) throws ForbiddenException, JSONException {
        JSONObject requestJSON = new JSONObject(req);
        if( !requestJSON.has("projectId")) {
            throw new ForbiddenException();
        }
        String name;
        if (!requestJSON.has("projectName")) {
            name = "Untitled";
        }
        else {
            name = requestJSON.getString("projectName");
        }
        String id = requestJSON.getString("projectId");
        for (Project project : projects) {
            if (project.getId().equals(name)) {
                return;
            }
        }
        projects.add(new Project(name, id));
    }

    @GetMapping(path = "/project/{projId}/repos")
    @ResponseBody
    public String getProjectRepos(@PathVariable("projId") String projectId) throws NoEntryException, JSONException {
        if( projectId.equals("") ) {
            throw new NoEntryException();
        }
        for (Project project: projects) {
            if (project.getId().equals(projectId)) {
                return project.getRepositories().toString();
            }
        }
        throw new NoEntryException();
    }

    // This method returns repo info based on github username and repo name
    @GetMapping(path = "/github/project/{projId}/repos/{githubUsername}/{repoName}")
    @ResponseBody
    public String getRepo(@PathVariable("projId") String projectId,
                        @PathVariable("githubUsername") String githubUsername,
                        @PathVariable("repoName") String repoName) throws NoEntryException, JSONException {
        if( projectId.equals("")  || githubUsername.equals("") ) {
            throw new NoEntryException();
        }
        for (Project project: projects) {
            GitRepository repo = project.getRepositoryByUserName(githubUsername, repoName);
            if (repo != null && project.getId().equals(projectId)) {
                return repo.getInfo().toString();
            }
        }
        throw new NoEntryException();
    }


    // This method creates a new gitHub repo from username and repo name
    @CrossOrigin
    @PutMapping(path = "/project/{projId}/repos/addRepofromName")
    public void putRepo(@RequestBody String req) throws NoEntryException, ForbiddenException, JSONException {
        JSONObject requestJSON = new JSONObject(req);
        if( !requestJSON.has("projectId") || !requestJSON.has("githubUsername") || !requestJSON.has("repoName") || !requestJSON.has("gitSite")) {
            throw new ForbiddenException();
        }
        String githubUsername = requestJSON.getString("githubUsername");
        String repoName = requestJSON.getString("repoName");
        String projectId = requestJSON.getString("projectId");
        String gitSite = requestJSON.getString("gitSite");

        if( projectId.equals("") || githubUsername.equals("") || repoName.equals("") ) {
            return;
        }
        projects.add(new Project("Moo","Moo")); // Delete this line once done refactoring
        for (Project project : projects) {
            if (project.getId().equals(projectId)) {
                if (gitSite.equals("github")) {
                        project.addRepositoryByUsername(githubUsername, repoName);
                        String gitId = project.getRepositoryByUserName(githubUsername, repoName).getGitId();
                        postToUserService(projectId, gitId);
                        return;
                }
            }
        }
        throw new ForbiddenException();
    }

    // This method returns contributors for a repo based on github username and repo name
    @GetMapping(path = "/github/project/{projId}/repos/{githubUsername}/{repoName}/contributors")
    @ResponseBody
    public String getRepoContributors(@PathVariable("projId") String projectId,
                        @PathVariable("githubUsername") String githubUsername,
                        @PathVariable("repoName") String repoName) throws NoEntryException {
        if( projectId.equals("")  || githubUsername.equals("") || repoName.equals("") ) {
            throw new NoEntryException();
        }
        for (Project project: projects) {
            if (project.getId().equals(projectId)) {
                if (project.getRepositoryByUserName(githubUsername, repoName) != null) {
                    return project.getRepositoryByUserName(githubUsername,repoName).getContributors().toString();
                }
            }
        }
        throw new NoEntryException();
    }

    // This method returns repo commuts based on github username and repo name
    @GetMapping(path = "/github/project/{projId}/repos/{githubUsername}/{repoName}/commits")
    @ResponseBody
    public String getRepoCommits(@PathVariable("projId") String projectId,
                        @PathVariable("githubUsername") String githubUsername,
                        @PathVariable("repoName") String repoName) throws NoEntryException, JSONException {
        if( projectId.equals("")  || githubUsername.equals("") || repoName.equals("") ) {
            throw new NoEntryException();
        }

        for (Project project: projects) {
            if (project.getId().equals(projectId)) {
                if (project.getRepositoryByUserName(githubUsername, repoName) != null) {
                    return project.getRepositoryByUserName(githubUsername,repoName).getCommits().toString();
                }
            }
        }
        throw new NoEntryException();
    }


    // This method returns repo info based on github id
    @GetMapping(path = "/github/project/{projId}/repos/{gitID}")
    @ResponseBody
    public String getRepoByID(@PathVariable("projId") String projectId,
                          @PathVariable("gitID") String gitID) throws NoEntryException, JSONException {
        if( projectId.equals("")  || gitID.equals("") ) {
            throw new NoEntryException();
        }
        for (Project project: projects) {
            GitRepository repo = project.getRepositoryByID(gitID);
            if (repo != null && project.getId().equals(projectId)) {
                return repo.getInfo().toString();
            }
        }
        throw new NoEntryException();
    }

    // This method adds a repo to the project based on github id
    @CrossOrigin
    @PutMapping(path = "/project/{projId}/repos/addRepofromID")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void putRepoByID(@RequestBody String req) throws ForbiddenException, JSONException {
        JSONObject requestJSON = new JSONObject(req);
        if( !requestJSON.has("projectId") || !requestJSON.has("gitId") || !requestJSON.has("gitSite")) {
            throw new ForbiddenException();
        }
        String gitId = requestJSON.getString("gitId");
        String projectId = requestJSON.getString("projectId");
        String gitSite = requestJSON.getString("gitSite");
        if( projectId.equals("") || gitId.equals("") || gitSite.equals("")) {
            return;
        }
        for (Project project : projects) {
            if (project.getId().equals(projectId)) {
                if (gitSite.equals("github")) {
                    project.addRepositoryByID(gitId);
                    postToUserService(projectId, gitId);
                    return;
                }
            }
        }
        throw new ForbiddenException();

    }

    // This method returns the contributors of a repo based on github id
    @GetMapping(path = "/github/project/{projId}/repos/{gitId}/contributors")
    @ResponseBody
    public String getRepoContributorsByID(@PathVariable("projId") String projectId,
                                      @PathVariable("gitId") String gitID) throws NoEntryException {
        if( projectId.equals("")  || gitID.equals("") ) {
            throw new NoEntryException();
        }
        for (Project project: projects) {
            if (project.getId().equals(projectId)) {
                if (project.getRepositoryByID(gitID) != null) {
                    return project.getRepositoryByID(gitID).getContributors().toString();
                }
            }
        }
        throw new NoEntryException();
    }

    // This method retuns repo commit info based on github id
    @GetMapping(path = "/github/project/{projId}/repos/{gitId}/commits")
    @ResponseBody
    public String getRepoCommitsByID(@PathVariable("projId") String projectId,
                                 @PathVariable("gitId") String gitId) throws NoEntryException, JSONException {
        if( projectId.equals("")  || gitId.equals("") ) {
            throw new NoEntryException();
        }
        for (Project project: projects) {
            if (project.getId().equals(projectId)) {
                if (project.getRepositoryByID(gitId) != null) {
                    return project.getRepositoryByID(gitId).getCommits().toString();
                }
            }
        }
        throw new NoEntryException();
    }

    // This method posts project id and git id back to the central site
    private void postToUserService(String projectId, String gitId) {
        try {
            URL url = new URL("http://localhost:3000/user-project-service/save-git");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            String jsonInputString = "{'projectId': '"+projectId+"', 'gitId': '"+gitId+"'}";
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }
        }
        catch (IOException e) {
            // e.printStackTrace();
            System.out.println("post to user-service failed");
        }
    }

}
