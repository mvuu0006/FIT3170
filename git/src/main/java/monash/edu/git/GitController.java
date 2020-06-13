package monash.edu.git;

import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/git")
@CrossOrigin
public class GitController {

    GitService gitService = new GitService();

    private RestTemplate restTemplate;
    private ArrayList<Project> projects = new ArrayList<Project>();


    // Method responsible for getting all the contributors and their contribution percent
    @GetMapping("/contributors")
    public String getContributions() throws IOException, JSONException {
        return gitService.getContributions();
    }

    @RequestMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue="") String name) throws JSONException {
        System.out.println(name);

        return "hello, "+name;
    }

    /**
     * Returns the project data stored in the system. I
     * @param name the user to retrieve repo data or
     * @return  a JSON object containing user info
     * @throws JSONException
     */
    @GetMapping(path = "/project/{projName}", produces="application/json")
    @ResponseBody
    public String getUser(@PathVariable("projName") String name) throws JSONException, NoEntryException {
        JSONObject response = new JSONObject();
        JSONObject status = new JSONObject();
        //
        if( name.equals("") ) {
            status.put("message", "User Not Found");
            status.put("status_code", 404);
            throw new NoEntryException();
        }
        else {
            status.put("message", "OK");
            status.put("status_code", 200);
            JSONObject body = new JSONObject();
            boolean found = false;
            for (Project project: projects) {
                if (project.getProjectName().equals(name)) {
                    body.put("project", project.toString());
                    found = true;
                }
            }
            if (!found) {
                throw new NoEntryException();
            }
            response.put("body", body);
        }
        response.put("status", status);
        return response.toString();
    }

    @PutMapping(path = "/projects/{projName}")
    public void putUser(@PathVariable("projName") String name) throws NoEntryException {
        // TODO: Change NoEntryException to an exception that creates a 403 Forbidden
        if( name.equals("") ) {
            return;
        }
        for (Project project : projects) {
            if (project.getProjectName().equals(name)) {
                throw new NoEntryException();
            }
        }
        projects.add(new Project(name));
        return;
    }

    @GetMapping(path = "/projects/{projName}/repos")
    @ResponseBody
    public String getProjectRepos(@PathVariable("projName") String projectName) throws NoEntryException, JSONException {
        if( projectName.equals("") ) {
            throw new NoEntryException();
        }
        for (Project project: projects) {
            if (project.getProjectName().equals(projectName)) {
                return project.getRepositories().toString();
            }
        }
        throw new NoEntryException();
    }

    @GetMapping(path = "/projects/{projName}/repos/{githubUsername}/{repoName}")
    @ResponseBody
    public String getRepo(@PathVariable("projName") String projectName,
                        @PathVariable("githubUsername") String githubUsername,
                        @PathVariable("repoName") String repoName) throws NoEntryException, JSONException {
        if( projectName.equals("")  || githubUsername.equals("") ) {
            throw new NoEntryException();
        }
        for (Project project: projects) {
            GitRepository repo = project.getRepository(githubUsername, repoName);
            if (repo != null && project.getProjectName().equals(projectName)) {
                return repo.getInfo().toString();
            }
        }
        throw new NoEntryException();
    }

    @GetMapping(path = "/projects/{projName}/repos/{githubUsername}/{repoName}/contributors")
    @ResponseBody
    public String getRepoContributors(@PathVariable("projName") String projectName,
                        @PathVariable("githubUsername") String githubUsername,
                        @PathVariable("repoName") String repoName) throws NoEntryException, JSONException { 
        if( projectName.equals("")  || githubUsername.equals("") || repoName.equals("") ) {
            throw new NoEntryException();
        }
        for (Project project: projects) {
            if (project.getRepository(githubUsername, repoName) != null) {
                // Call project.getRepo().getContributors func and return the JSON for it
            }
        }
        throw new NoEntryException();
    }

    @GetMapping(path = "/projects/{projName}/repos/{githubUsername}/{repoName}/commits")
    @ResponseBody
    public String getRepoCommits(@PathVariable("projName") String projectName,
                        @PathVariable("githubUsername") String githubUsername,
                        @PathVariable("repoName") String repoName) throws NoEntryException, JSONException { 
        // if( projectName.equals("") || githubUsername.equals("") || repoName.equals("") ) {
        //     throw new NoEntryException();
        // }
        // for (Project project: projects) {
        //     if (project.hasRepository(githubUsername, repoName)) {
        //         // Call project.getRepo().getCommits func and return the JSON for it
        //     }
        // }
        // throw new NoEntryException();
        /* Debugging code */
        Project project = new Project("test");
        GitRepository repo = new GitRepository("hbak0001", "fit3157-asgn2");
        return repo.toString();
    }

    /* Remove this when testing is complete */
    @GetMapping(path = "/testcreate")
    @ResponseBody
    public String doTest() {
        Project project = new Project("test");
        projects.add(project);
        project.addRepository("hbak0001", "fit3157-asgn2");
        return "";
    }
}
