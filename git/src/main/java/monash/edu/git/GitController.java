package monash.edu.git;


import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;


@RestController
@RequestMapping("/git")
@CrossOrigin
public class GitController {
    private ArrayList<Project> projects = new ArrayList<Project>();

//    @RequestMapping("/hello")
//    public String hello(@RequestParam(value = "name", defaultValue="") String name) throws JSONException {
//        System.out.println(name);
//
//        return "hello, "+name;
//    }

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
                    body.put("project", project.getProjectInfo());
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

    @PutMapping(path = "/project/{projName}")
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
    }

    @GetMapping(path = "/projects/{projName}/repos")
    @ResponseBody
    public String getProjectRepos(@PathVariable("projName") String projectName) throws NoEntryException, JSONException {
        if( projectName.equals("") ) {
            throw new NoEntryException();
        }
        for (Project project: projects) {
            if (project.getProjectName().equals(projectName)) {
                // TODO: The below function may not contain exactly what we want it to
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

    @PutMapping(path = "/projects/{projName}/repos/{githubUsername}/{repoName}")
    public void putRepo(@PathVariable("projName") String projectName,
    @PathVariable("githubUsername") String githubUsername,
    @PathVariable("repoName") String repoName) throws NoEntryException, JSONException {
        // Look at PUT mapping for project for an idea on what to code here
        //GitRepository repo = new GitRepository(githubUsername, repoName);

        // TODO: Change NoEntryException to an exception that creates a 403 Forbidden
        if( projectName.equals("") || githubUsername.equals("") || repoName.equals("") ) {
            return;
        }
        for (Project project : projects) {
            if (project.getProjectName().equals(projectName)) {
                project.addRepository(githubUsername,repoName);
                return;
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
        return "";
    }
}
