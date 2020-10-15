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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/git")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "http://localhost:3003"}, maxAge = 0)
public class GitController {
    private DatabaseHandler dbHandler = new DatabaseHandler("jdbc:mysql://spmd-git-db-syd.chriccj5hso1.ap-southeast-2.rds.amazonaws.com:3306",
    "admin", "0xFDMui5vQoChrpit32x"); // Database username and password
    private GitHubInterface ghInterface = new GitHubInterface();
    private GitLabInterface glInterface = new GitLabInterface();


    /*
     PUT: New project into database
     */
    @PutMapping(path = "/project")
    @ResponseStatus(code = HttpStatus.CREATED)
    @ResponseBody
    public void putProject(@RequestParam("project-id") String id,
        @RequestParam("email") String email, @RequestParam("user-type") String user_type) throws ForbiddenException, JSONException, ClassNotFoundException {
        // Check that query params arent empty
        if( id.equals("") || email.equals("") || user_type.equals("")) {
            throw new ForbiddenException();
        }
        // Add project to database
        String addScript = "INSERT INTO gitdb.Project(projectId) VALUES ("+id+")";
        int rowsChanged = dbHandler.executeUpdate(addScript);
        // Throw error if row wasn't added
        if (rowsChanged == 0) {
            throw new ForbiddenException();
        }
        // Connect email to project
        switch (user_type){
            case "student":
                addScript = "INSERT INTO gitdb.StudentProject(emailStudent, projectId) VALUES('"+
                    email+"', "+id+")";
                break;
            case "teacher":
                addScript = "INSERT INTO gitdb.TeacherProject(idTeacher, projectId) VALUES('"+
                    email+"', "+id+")";
                break;
            default:
            throw new ForbiddenException();
        }
        rowsChanged = dbHandler.executeUpdate(addScript);
        // Throw error if row wasn't added
        if (rowsChanged == 0) {
            throw new ForbiddenException();
        }
    }


    /*
     GET: Repos currently attached to a project
     */
    @GetMapping(path = "/project/{project-id}")
    @ResponseBody
    public String getProjectRepos(@PathVariable("project-id") String id,
    @RequestParam("email") Optional<String> email, @RequestParam("user-type") Optional<String> user_type,
    @RequestParam Optional<String> token) throws NoEntryException, JSONException, ClassNotFoundException, IOException {
        if( id.equals("")) {
            throw new NoEntryException();
        }
        String getScript = "SELECT * FROM gitdb.Repository " +
            "WHERE id IN (SELECT idRepo FROM gitdb.ProjectRepo " +
                "WHERE projectId="+id+");";
        HashMap<String, FieldType> fields = new HashMap<String, FieldType>();
        fields.put("url", FieldType.STRING);
        fields.put("service", FieldType.STRING);
        fields.put("id", FieldType.STRING);
        JSONArray repos = new JSONArray();
        try {
            repos = dbHandler.executeQuery(getScript, fields);
        }
        catch (NoEntryException e) {
            return repos.toString();
        }
        for (int i = 0; i < repos.length(); i++) {
            if (repos.getJSONObject(i).getString("service").equals("gitlab") && token.isPresent()) {
                JSONObject repoInfo = glInterface.getRepoInfo(repos.getJSONObject(i).getString("id"), token.get());
                repos.getJSONObject(i).put("name", repoInfo.getString("name"));
            }
            else if (repos.getJSONObject(i).getString("service").equals("github")) {
                JSONObject repoInfo = ghInterface.getRepoInfo(repos.getJSONObject(i).getString("id"));
                repos.getJSONObject(i).put("name", repoInfo.getString("name"));
            }
        }
        if (repos.length() == 0) {
            return new JSONArray().toString();
        }
        return repos.toString();
    }



    /*
     POST: Repository into database
     */
    @CrossOrigin
    @ResponseBody
    @PostMapping(path = "/project/{project-id}/repository")
    public String putRepo(@PathVariable("project-id") String id,
        @RequestParam("service") String service, @RequestParam("url") String url, @RequestParam("token") Optional<String> token) throws NoEntryException, ForbiddenException, JSONException, ClassNotFoundException, IOException {
        if( url.equals("")  || service.equals("") || id.equals("") ) {
            throw new NoEntryException();
        }
        String repo_id = null;
        switch (service) {
            case "gitlab":
                if (token.isPresent()){
                    repo_id = glInterface.getIdFromURL(url, token.get());
                }
                else {
                    throw new ForbiddenException();
                }
                break;
            case "github":
                repo_id = ghInterface.getIdFromURL(url);
                break;
            default:
        }
        // TODO: Add restrictions to database to standardise service syntax (eg. all lower case)
        String putScript = "INSERT INTO gitdb.Repository(url, service, id) VALUES('"+url+"', '"+service+"', '"+repo_id+"')";
        int rowsChanged = dbHandler.executeUpdate(putScript);
        // Throw error if row wasn't added (Don't)
        if (rowsChanged == 0) {
            //throw new ForbiddenException();
        }
        putScript = "INSERT INTO gitdb.ProjectRepo(idRepo, projectId, serviceRepo) VALUES('"+repo_id+"', '"+id+"', '"+service+"')";
        rowsChanged = dbHandler.executeUpdate(putScript);
        // Throw error if row wasn't added
        if (rowsChanged == 0) {
            throw new ForbiddenException();
        }
        JSONObject returnObject = new JSONObject();
        returnObject.put("repo-id", repo_id);
        returnObject.put("repo-url", url);
        returnObject.put("repo-service", service);
        return returnObject.toString();
    }


    /*
     GET: Repository Commits (name of commitor and number of commits). Use for timeline and pie chart??
     */
    @GetMapping(path = "/project/{project-id}/repository/contribution")
    @ResponseBody
    public String getRepoContribution(@PathVariable("project-id") String id, @RequestParam("email") String email,
        @RequestParam("repo-id") String repo_id,
        @RequestParam("token") Optional<String> token) throws NoEntryException, JSONException, ClassNotFoundException, IOException {
        if( id.equals("")  || repo_id.equals("") || email.equals("")) {
            throw new NoEntryException();
        }
        // Database code: 404 is returned if email does not have link to project id
        String findScript =  "SELECT * FROM gitdb.ProjectRepo " +
                               "WHERE projectId="+id+" AND EXISTS( " +
                                    "SELECT * FROM gitdb.StudentProject " +
                                        "WHERE emailStudent='"+email+"' AND projectId="+id+" " +
                                ") AND idRepo='"+repo_id+"';";
        HashMap<String, FieldType> fields = new HashMap<String, FieldType>();
        fields.put("idRepo", FieldType.STRING);
        fields.put("serviceRepo", FieldType.STRING);
        fields.put("projectId", FieldType.INT);
        JSONArray rowMap = dbHandler.executeQuery(findScript, fields);
        String service = rowMap.getJSONObject(0).getString("serviceRepo").toLowerCase();
        // Get Contributors
        JSONArray contributors = new JSONArray();
        switch (service) {
            case "github":
                contributors = ghInterface.getRepoContributors(repo_id);
                break;
            case "gitlab":
                if (token.isPresent()){
                    contributors = glInterface.getRepoCommits(repo_id, token.get());
                }
                else {
                    throw new ForbiddenException();
                }
                break;
            default:
                break;
        }
        return(contributors.toString());
    }


    /*
     GET: Repository Commits (commit info)
     */
    @GetMapping(path = "/project/{project-id}/repository/commits")
    @ResponseBody
    public String getRepoCommits(@PathVariable("project-id") String project_id, @RequestParam("email") String email,
        @RequestParam("repo-id") String repo_id,
        @RequestParam("token") Optional<String> token) throws NoEntryException, JSONException, ClassNotFoundException, IOException {
        if( project_id.equals("")  || repo_id.equals("") || email.equals("")) {
            throw new NoEntryException();
        }
        // Database code: 404 is returned if email does not have link to project id
        String findScript =  "SELECT * FROM gitdb.ProjectRepo " +
                               "WHERE projectId="+project_id+" AND EXISTS( " +
                                    "SELECT * FROM gitdb.StudentProject " +
                                        "WHERE emailStudent='"+email+"' AND projectId="+project_id+" " +
                                ") AND idRepo='"+repo_id+"';";
        HashMap<String, FieldType> fields = new HashMap<String, FieldType>();
        fields.put("idRepo", FieldType.STRING);
        fields.put("serviceRepo", FieldType.STRING);
        fields.put("projectId", FieldType.INT);
        JSONArray rowMap = dbHandler.executeQuery(findScript, fields);
        if (rowMap.length() > 1){
            throw new ForbiddenException();
        }
        String service = rowMap.getJSONObject(0).getString("serviceRepo").toLowerCase();
        // Get Contributors
        JSONArray contributors = new JSONArray();
        switch (service) {
            case "github":
                contributors = ghInterface.getRepoCommits(repo_id);
                break;
            case "gitlab":
                if (token.isPresent()){
                    contributors = glInterface.getRepoCommits(repo_id, token.get());
                }
                else {
                    throw new ForbiddenException();
                }
                break;
            default:
                break;
        }
        return(contributors.toString());
    }


    /*
     GET: Repository Commits (commit info)
     */
    @GetMapping(path = "/project/{project-id}/repository/last-changed-email")
    @ResponseBody
    public String getLastChangedEmail(@PathVariable("project-id") String project_id, @RequestParam("emails") String email,
                                 @RequestParam("git-ids") String repo_id,
                                 @RequestParam("token") Optional<String> token) throws NoEntryException, JSONException, ClassNotFoundException, IOException {
        if( project_id.equals("")  || repo_id.equals("") || email.equals("")) {
            throw new NoEntryException();
        }
        // Seperating project id's and emails into arrays
        var singleid = "";
        List<String> git_array = new ArrayList<String>();
        for (int i=0; i<repo_id.length();i++){
            if (repo_id.charAt(i) == ','){
                git_array.add(singleid);
                singleid = "";
            }
            else if (i == repo_id.length()-1){
                git_array.add(singleid+repo_id.charAt(i));
                singleid = "";
            }
            else{
                singleid += repo_id.charAt(i);
            }
        }
        List<String> email_array = new ArrayList<String>();
        for (int i=0; i<email.length();i++){
            if (email.charAt(i) == ','){
                email_array.add(singleid);
                singleid = "";
            }
            else if (i == email.length()-1){
                email_array.add(singleid+email.charAt(i));
                singleid = "";
            }
            else{
                singleid += email.charAt(i);
            }
        }
        // Database code: 404 is returned if email does not have link to project id
        String findScript =  "SELECT * FROM gitdb.ProjectRepo " +
                "WHERE projectId="+project_id+" AND EXISTS( " +
                "SELECT * FROM gitdb.StudentProject " +
                "WHERE emailStudent='"+email_array.get(0)+"' AND projectId="+project_id+" " +
                ") AND idRepo='"+git_array.get(0)+"';";
        HashMap<String, FieldType> fields = new HashMap<String, FieldType>();
        fields.put("idRepo", FieldType.STRING);
        fields.put("serviceRepo", FieldType.STRING);
        fields.put("projectId", FieldType.INT);
        JSONArray rowMap = dbHandler.executeQuery(findScript, fields);
        if (rowMap.length() > 1){
            throw new ForbiddenException();
        }
        String service = rowMap.getJSONObject(0).getString("serviceRepo").toLowerCase();

        // Initialising final array
        JSONArray finalchanged = new JSONArray();
        for (int i=0; i<email_array.size(); i++){
            finalchanged.put(new JSONObject().put("email",email_array.get(i)));
        }

        // Looping through the git id's to get the last changed contributors
        for (int i=0; i<git_array.size(); i++) {
            JSONObject lastchanged = new JSONObject();
            switch (service) {
                case "github":
                    lastchanged = ghInterface.getLastContributions(git_array.get(i));
                    break;
                case "gitlab":
                    if (token.isPresent()) {
                        lastchanged = glInterface.getLastContributions(git_array.get(i), token.get());
                    } else {
                        throw new ForbiddenException();
                    }
                    break;
                default:
                    break;
            }
            // comparing the contributors to the final array and updating it if needed.
            for (int j=0; j< finalchanged.length(); j++){
                var currentemail = finalchanged.getJSONObject(j).getString("email");
                if (lastchanged.has(currentemail)){
                    if (finalchanged.getJSONObject(j).has("lastModified")){
                        if (finalchanged.getJSONObject(j).getString("lastModified").compareTo(lastchanged.getString(currentemail))< 0){
                            finalchanged.getJSONObject(j).put("lastModified", lastchanged.getString(currentemail));
                        }
                    }
                    else {
                        finalchanged.getJSONObject(j).put("lastModified", lastchanged.getString(currentemail));
                    }
                }
            }
        }
        return(finalchanged.toString());
    }


    /*
     GET: GitLab Access Token
     */
    @GetMapping(path = "/gitlab-access-code")
    @ResponseBody
    @ResponseStatus(code = HttpStatus.OK)
    public String getAccessToken(@RequestParam String code, @RequestParam String redirect_uri) throws NoEntryException, JSONException {
        /*
            STEP 3: Get access token from gitlab using authorisation code (see frontend for previous steps)
        */
        String getAccessCodeURL = "https://git.infotech.monash.edu/oauth/token" +
                "?code=" + code +
                "&client_id=" + "2b2676dd243b35a0cef351c2a5a03cbf5360221219967226d4393b3715a50bef" +
                "&client_secret=" + "30a8af89112f5ba74383f307d69cf08093d8a13e52480bd53740de0b8ca8fab9" +
                "&grant_type=authorization_code" +
                "&redirect_uri="+redirect_uri;

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

}
