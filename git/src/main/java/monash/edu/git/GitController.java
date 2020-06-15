package monash.edu.git;


import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;


@RestController
@RequestMapping("/git")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "http://localhost:3003"}, maxAge = 0)
public class GitController {
    private ArrayList<Project> projects = new ArrayList<Project>();

    /**
     * Returns the project data stored in the system. I
     * @param name the user to retrieve repo data or
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
    public void putUser(@RequestBody String req) throws NoEntryException, JSONException {
        System.out.println(req);
        JSONObject requestJSON = new JSONObject(req);
        // TODO: Change NoEntryException to an exception that creates a 403 Forbidden
        if( !requestJSON.has("projectName") || !requestJSON.has("projectId")) {
            throw new NoEntryException();
        }
        String name = requestJSON.getString("projectName");
        String id = requestJSON.getString("projectId");
        for (Project project : projects) {
            if (project.getProjectName().equals(name)) {
                throw new NoEntryException();
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
        boolean success = false;
        for (Project project: projects) {
            if (project.getId().equals(projectId)) {
                success = true;
                return project.getRepositories().toString();
            }
        }
        if (!success) {
            throw new NoEntryException();
        }
        return "";
    }

    @GetMapping(path = "/project/{projId}/repos/{githubUsername}/{repoName}")
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

    @PutMapping(path = "/project/{projId}/repos/{githubUsername}/{repoName}")
    public void putRepo(@PathVariable("projId") String projectId,
    @PathVariable("githubUsername") String githubUsername,
    @PathVariable("repoName") String repoName) throws NoEntryException, JSONException {
        // Look at PUT mapping for project for an idea on what to code here
        //GitRepository repo = new GitRepository(githubUsername, repoName);

        // TODO: Change NoEntryException to an exception that creates a 403 Forbidden
        if( projectId.equals("") || githubUsername.equals("") || repoName.equals("") ) {
            return;
        }
        for (Project project : projects) {
            if (project.getId().equals(projectId)) {
                project.addRepositoryByUsername(githubUsername,repoName);
                return;
            }
        }
        throw new NoEntryException();
    }

    @GetMapping(path = "/project/{projId}/repos/{githubUsername}/{repoName}/contributors")
    @ResponseBody
    public String getRepoContributors(@PathVariable("projId") String projectId,
                        @PathVariable("githubUsername") String githubUsername,
                        @PathVariable("repoName") String repoName) throws NoEntryException, JSONException { 
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

    @GetMapping(path = "/project/{projId}/repos/{githubUsername}/{repoName}/commits")
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


    @GetMapping(path = "/project/{projId}/repos/{gitID}")
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

    @CrossOrigin
    @PutMapping(path = "/project/{projId}/repos/{gitID}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void putRepoByID(@PathVariable("projId") String projectId,
                        @PathVariable("gitID") String gitId) throws NoEntryException, JSONException, NoRepoException {
        // Look at PUT mapping for project for an idea on what to code here
        //GitRepository repo = new GitRepository(githubUsername, repoName);

        // TODO: Change NoEntryException to an exception that creates a 403 Forbidden
        if( projectId.equals("") || gitId.equals("")) {
            return;
        }
        for (Project project : projects) {
            if (project.getId().equals(projectId)) {
                project.addRepositoryByID(gitId);
                return;
            }
        }
        throw new NoEntryException();

    }

    @GetMapping(path = "/project/{projId}/repos/{gitId}/contributors")
    @ResponseBody
    public String getRepoContributorsByID(@PathVariable("projId") String projectId,
                                      @PathVariable("gitId") String gitID) throws NoEntryException, JSONException {
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

    @GetMapping(path = "/project/{projId}/repos/{gitId}/commits")
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

}
