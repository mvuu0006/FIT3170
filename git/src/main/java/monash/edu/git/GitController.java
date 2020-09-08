package monash.edu.git;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     *
     * @param id the user to retrieve repo data or
     * @return a JSON object containing user info
     * @throws JSONException
     */
    @CrossOrigin
    @GetMapping(path = "/project/{projID}", produces = "application/json")
    @ResponseBody
    public String getProject(@PathVariable("projID") String id) throws JSONException, NoEntryException {
        JSONObject response = new JSONObject();

        if (id.equals("")) {
            throw new NoEntryException();
        } else {
            JSONObject body = new JSONObject();
            boolean found = false;
            for (Project project : projects) {
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
        // TODO: Change NoEntryException to an exception that creates a 403 Forbidden
        if (!requestJSON.has("projectId")) {
            throw new NoEntryException();
        }
        String name;
        if (!requestJSON.has("projectName")) {
            name = "Untitled";
        } else {
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
    public String getProjectRepos(@PathVariable("projId") String projectId) throws NoEntryException, JSONException, IOException {
        if (projectId.equals("")) {
            throw new NoEntryException();
        }
        boolean success = false;
        for (Project project : projects) {
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
        if (projectId.equals("") || githubUsername.equals("")) {
            throw new NoEntryException();
        }
        boolean success = false;
        for (Project project : projects) {
            GitRepository repo = project.getRepositoryByUserName(githubUsername, repoName);
            if (repo != null && project.getId().equals(projectId)) {
                success = true;
                return repo.getInfo().toString();
            }
        }
        if (!success) {
            throw new NoEntryException();
        }
        return "";
    }

    @PutMapping(path = "/project/{projId}/repos/{githubUsername}/{repoName}")
    public void putRepo(@PathVariable("projId") String projectId,
                        @PathVariable("githubUsername") String githubUsername,
                        @PathVariable("repoName") String repoName) throws NoEntryException, JSONException {
        // Look at PUT mapping for project for an idea on what to code here
        //GitRepository repo = new GitRepository(githubUsername, repoName);

        // TODO: Change NoEntryException to an exception that creates a 403 Forbidden
        if (projectId.equals("") || githubUsername.equals("") || repoName.equals("")) {
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
                                      @PathVariable("repoName") String repoName) throws NoEntryException, JSONException {
        if (projectId.equals("") || githubUsername.equals("") || repoName.equals("")) {
            throw new NoEntryException();
        }
        boolean success = false;
        for (Project project : projects) {
            if (project.getId().equals(projectId)) {
                if (project.getRepositoryByUserName(githubUsername, repoName) != null) {
                    success = true;
                    return project.getRepositoryByUserName(githubUsername, repoName).getContributors().toString();
                }
            }
        }
        if (!success) {
            throw new NoEntryException();
        }
        return "";
    }

    // This method returns repo commuts based on github username and repo name
    @GetMapping(path = "/github/project/{projId}/repos/{githubUsername}/{repoName}/commits")
    @ResponseBody
    public String getRepoCommits(@PathVariable("projId") String projectId,
                                 @PathVariable("githubUsername") String githubUsername,
                                 @PathVariable("repoName") String repoName) throws NoEntryException, JSONException {
        if (projectId.equals("") || githubUsername.equals("") || repoName.equals("")) {
            throw new NoEntryException();
        }

        for (Project project : projects) {
            if (project.getId().equals(projectId)) {
                if (project.getRepositoryByUserName(githubUsername, repoName) != null) {
                    return project.getRepositoryByUserName(githubUsername, repoName).getCommits().toString();
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
        if (projectId.equals("") || gitID.equals("")) {
            throw new NoEntryException();
        }
        for (Project project : projects) {
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
    public void putRepoByID(@PathVariable("projId") String projectId,
                            @PathVariable("gitID") String gitId) throws NoEntryException, JSONException, NoRepoException {
        // Look at PUT mapping for project for an idea on what to code here
        //GitRepository repo = new GitRepository(githubUsername, repoName);
        // TODO: Change NoEntryException to an exception that creates a 403 Forbidden
        if (projectId.equals("") || gitId.equals("")) {
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
                                          @PathVariable("gitId") String gitID) throws NoEntryException, JSONException {
        if (projectId.equals("") || gitID.equals("")) {
            throw new NoEntryException();
        }
        boolean success = false;
        for (Project project : projects) {
            if (project.getId().equals(projectId)) {
                if (project.getRepositoryByID(gitID) != null) {
                    success = true;
                    return project.getRepositoryByID(gitID).getContributors().toString();
                }
            }
        }
        if (!success) {
            throw new NoEntryException();
        }
        return "";
    }

    // This method retuns repo commit info based on github id
    @GetMapping(path = "/github/project/{projId}/repos/{gitId}/commits")
    @ResponseBody
    public String getRepoCommitsByID(@PathVariable("projId") String projectId,
                                     @PathVariable("gitId") String gitId) throws NoEntryException, JSONException {
        if (projectId.equals("") || gitId.equals("")) {
            throw new NoEntryException();
        }
        boolean success = false;
        for (Project project : projects) {
            if (project.getId().equals(projectId)) {
                if (project.getRepositoryByID(gitId) != null) {
                    success = true;
                    return project.getRepositoryByID(gitId).getCommits().toString();
                }
            }
        }
        if (!success) {
            throw new NoEntryException();
        }
        return "";
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
            String jsonInputString = "{'projectId': '" + projectId + "', 'gitId': '" + gitId + "'}";
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
        } catch (IOException e) {
            // e.printStackTrace();
            System.out.println("post to user-service failed");
        }
    }

    @GetMapping(path = "/gitlab-access-code")
    @ResponseBody
    @ResponseStatus(code = HttpStatus.OK)
    public String getAccessToken(@RequestParam String code) throws NoEntryException, JSONException {
        /*
            STEP 3: Get access token from gitlab using authorisation code (see frontend for previous steps)
        */
        String getAccessCodeURL = "https://git.infotech.monash.edu/oauth/token" +
                "?code=" + code +
                "&client_id=" + "25202383ac02265444e0ea55882782b3f85ba6baf53da0565652b3f9054613dc" +
                "&client_secret=" + "264287a16f1a7228d0444f94f68ba268c9d77a17adfbaf0bab1892d22192276c" +
                "&grant_type=authorization_code" +
                "&redirect_uri=http://localhost:3001";

        JSONArray jsonArray = new JSONArray();

        try {
            URL url = new URL(getAccessCodeURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            int tokenStatus = con.getResponseCode();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream())
            );
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            /*
                Step 4: Send access token back to front-end
             */
            return content.toString();
        } catch (IOException e) {
        }

        throw new NoEntryException();

    }


    @PutMapping(path = "/project/{projId}/labRepos/{accesstoken}/{repoID}")
    public void putlabRepo(@PathVariable("projId") String projectId,
                           @PathVariable("accesstoken") String accesstoken,
                           @PathVariable("repoID") String repoID) throws NoEntryException, JSONException {
        // Look at PUT mapping for project for an idea on what to code here
        //GitRepository repo = new GitRepository(githubUsername, repoName);

        // TODO: Change NoEntryException to an exception that creates a 403 Forbidden
        if (projectId.equals("") || accesstoken.equals("") || repoID.equals("")) {
            return;
        }
        for (Project project : projects) {
            if (project.getId().equals(projectId)) {
                project.addRepositoryByIDandToken(repoID,accesstoken);
                //String gitId = project.getRepositoryByUserName(accesstoken, repoID).getGitId();
                postToUserService(projectId, repoID);
                return;
            }
        }
        throw new NoEntryException();
    }
    @PutMapping(path = "/project/{projId}/labRepos/{repoID}")
    public void putlabReponoToken(@PathVariable("projId") String projectId,
                           @PathVariable("repoID") String repoID) throws NoEntryException, JSONException {
        // Look at PUT mapping for project for an idea on what to code here
        //GitRepository repo = new GitRepository(githubUsername, repoName);

        // TODO: Change NoEntryException to an exception that creates a 403 Forbidden
        if (projectId.equals("") || repoID.equals("")) {
            return;
        }
        for (Project project : projects) {
            if (project.getId().equals(projectId)) {
                project.addLabRepositoryByID(repoID);
                postToUserService(projectId, repoID);
                return;
            }
        }
        throw new NoEntryException();
    }
}