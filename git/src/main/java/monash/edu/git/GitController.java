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
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

@RestController
@RequestMapping("/git")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "http://localhost:3003",
"http://spmd-git-frontend.s3-website-ap-southeast-2.amazonaws.com"}, maxAge = 0)
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
    public void putProject(@RequestParam("project-id") String project_id,
        @RequestParam("id_token") String id_token) throws ForbiddenException, JSONException, ClassNotFoundException, IOException {
        // Check that query params arent empty
        if( project_id.equals("")) {
            throw new ForbiddenException();
        }
        String email = checkAccess(project_id, id_token, false);
        // Add project to database
        String addScript = "INSERT INTO gitdb.Project(projectId) VALUES ("+project_id+")";
        int rowsChanged = dbHandler.executeUpdate(addScript);
        // Throw error if row wasn't added
        if (rowsChanged == 0) {
            throw new ForbiddenException();
        }
        // Connect email to project
        addScript = "INSERT INTO gitdb.StudentProject(emailStudent, projectId) VALUES('"+
            email+"', "+project_id+")";
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
    public String getProjectRepos(@PathVariable("project-id") String project_id,
    @RequestParam("id_token") String id_token,
    @RequestParam Optional<String> token) throws NoEntryException, JSONException, ClassNotFoundException, IOException {
        checkAccess(project_id, id_token, true);
        if( project_id.equals("")) {
            throw new NoEntryException();
        }
        String getScript = "SELECT * FROM gitdb.Repository " +
            "WHERE id IN (SELECT idRepo FROM gitdb.ProjectRepo " +
                "WHERE projectId="+project_id+");";
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
            try {
                if (repos.getJSONObject(i).getString("service").equals("gitlab") && token.isPresent()) {
                    JSONObject repoInfo = glInterface.getRepoInfo(repos.getJSONObject(i).getString("id"), token.get());
                    repos.getJSONObject(i).put("name", repoInfo.getString("name"));
                }
                else if (repos.getJSONObject(i).getString("service").equals("github")) {
                    JSONObject repoInfo = ghInterface.getRepoInfo(repos.getJSONObject(i).getString("id"));
                    repos.getJSONObject(i).put("name", repoInfo.getString("name"));
                }
            }
            catch (IOException e) {
                String msg = "An error occured when getting the repo info for " + repos.getJSONObject(i).getString("service") +
                    ": " + repos.getJSONObject(i).getString("url");
                System.out.println(msg);
                System.out.println(e.getMessage());
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
        @RequestParam("service") String service, @RequestParam("url") String url, @RequestParam("token") Optional<String> token, @RequestParam("id_token") String id_token) throws NoEntryException, ForbiddenException, JSONException, ClassNotFoundException, IOException {
        if( url.equals("")  || service.equals("") || id.equals("") ) {
            throw new NoEntryException();
        }
        checkAccess(id, id_token, true);
        String repo_id = null;
        String repo_name = null;
        switch (service) {
            case "gitlab":
                if (token.isPresent()){
                    repo_id = glInterface.getIdFromURL(url, token.get());
                    repo_name = glInterface.getRepoInfo(repo_id, token.get()).getString("name");
                }
                else {
                    throw new ForbiddenException();
                }
                break;
            case "github":
                repo_id = ghInterface.getIdFromURL(url);
                repo_name = ghInterface.getRepoInfo(repo_id).getString("name");
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
        returnObject.put("repo-name", repo_name);
        return returnObject.toString();
    }


    /*
     GET: Repository Commits (name of commitor and number of commits). Use for timeline and pie chart??
     */
    @GetMapping(path = "/project/{project-id}/repository/contribution")
    @ResponseBody
    public String getRepoContribution(@PathVariable("project-id") String id, @RequestParam("email") String email,
        @RequestParam("repo-id") String repo_id,
        @RequestParam("token") Optional<String> token,
        @RequestParam("id_token") String id_token) throws NoEntryException, JSONException, ClassNotFoundException, IOException {
        checkAccess(id, id_token, true);
        if( id.equals("")  || repo_id.equals("") || email.equals("")) {
            throw new NoEntryException();
        }
        // Database code: 404 is returned if email does not have link to project id
        String findScript =  "SELECT * FROM gitdb.ProjectRepo " +
                               "WHERE projectId="+id+" AND idRepo='"+repo_id+"';";
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
    public String getRepoCommits(@PathVariable("project-id") String project_id,
        @RequestParam("repo-id") String repo_id,
        @RequestParam("token") Optional<String> token,
        @RequestParam("id_token") String id_token) throws NoEntryException, JSONException, ClassNotFoundException, IOException {
        if( project_id.equals("")  || repo_id.equals("")) {
            throw new NoEntryException();
        }
        String email = checkAccess(project_id, id_token, true);
        // Database code: 404 is returned if email does not have link to project id
        String findScript =  "SELECT * FROM gitdb.ProjectRepo " +
                               "WHERE projectId="+project_id+" AND idRepo='"+repo_id+"';";
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
        try{
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
        catch (IOException e) {
            System.out.println(e.getMessage());
            throw new NoEntryException();
        }
    }


    /*
     GET: Repository Commits (commit info)
     */
    @GetMapping(path = "/project/{project-id}/repository/last-changed-email")
    @ResponseBody
    public String getLastChangedEmail(@PathVariable("project-id") String project_id, @RequestParam("emails") String email,
                                 @RequestParam("git-ids") String repo_id,
                                 @RequestParam("id-token") String id_token) throws NoEntryException, JSONException, ClassNotFoundException, IOException {
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
        checkAccess(project_id, id_token, true);
        String findScript =  "SELECT * FROM gitdb.ProjectRepo " +
                "WHERE projectId="+project_id+";";
        HashMap<String, FieldType> fields = new HashMap<String, FieldType>();
        fields.put("idRepo", FieldType.STRING);
        fields.put("serviceRepo", FieldType.STRING);
        fields.put("projectId", FieldType.INT);
        JSONArray rowMap = dbHandler.executeQuery(findScript, fields);

        String service = rowMap.getJSONObject(0).getString("serviceRepo").toLowerCase();

        // Initialising final array
        JSONArray finalchanged = new JSONArray();
        for (int i=0; i<email_array.size(); i++){
            finalchanged.put(new JSONObject().put("email",email_array.get(i)));
        }

        JSONObject token_obj = new JSONObject(getGitlabStatus(project_id));
        String token = token_obj.getString("gitlab-access-token");
        // Looping through the git id's to get the last changed contributors
        for (int i=0; i<rowMap.length(); i++) {
            JSONObject git_info = rowMap.getJSONObject(i);
            if (git_array.contains(git_info.getString("idRepo"))) {
                JSONObject lastchanged = new JSONObject();
                switch (git_info.getString("serviceRepo")) {
                    case "github":
                        lastchanged = ghInterface.getLastContributions(git_info.getString("idRepo"));
                        break;
                    case "gitlab":
                        lastchanged = glInterface.getLastContributions(git_info.getString("idRepo"), token);
                        break;
                    default:
                        break;
                }
                // comparing the contributors to the final array and updating it if needed.
                for (int j=0; j< finalchanged.length(); j++){
                    var currentemail = finalchanged.getJSONObject(j).getString("email");
                    if (lastchanged.has(currentemail)){
                        if (finalchanged.getJSONObject(j).has("lastModified")){
                            if (finalchanged.getJSONObject(j).getString("lastModified").compareTo(lastchanged.getString(currentemail))< 0 ||
                            finalchanged.getJSONObject(j).getString("lastModified").equals("N/A")){
                                finalchanged.getJSONObject(j).put("lastModified", lastchanged.getString(currentemail));
                            }
                        }
                        else {
                            finalchanged.getJSONObject(j).put("lastModified", lastchanged.getString(currentemail));
                        }
                    }
                    else if (!(finalchanged.getJSONObject(j).has("lastModified"))) {
                        finalchanged.getJSONObject(j).put("lastModified", "N/A");
                    }
                }
            }
        }
        return(finalchanged.toString());
    }


    /*
     GET: GitLab Access Token
     */
    @GetMapping(path = "/project/{project-id}/gitlab-access-code")
    @ResponseBody
    @ResponseStatus(code = HttpStatus.OK)
    public String getAccessToken(@RequestParam String code, @RequestParam String redirect_uri,
    @PathVariable("project-id") String project_id, @RequestParam("id_token") String id_token) throws NoEntryException, JSONException, ClassNotFoundException, IOException {
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

        checkAccess(project_id, id_token, true);

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
            // Update DB with access token
            JSONObject tokenObject = new JSONObject(content.toString());
            String updateScript = "UPDATE gitdb.Project " +
                        "SET `gitlabToken` = '"+tokenObject.getString("access_token")+"' " +
                        "WHERE projectId = "+project_id+";";
            int changed_rows = dbHandler.executeUpdate(updateScript);
            assert (changed_rows == 1);
            /*
                Step 4: Send access token back to front-end
             */
            return content.toString();
        } catch (IOException e) {
        }

        throw new NoEntryException();

    }

    @GetMapping(path = "/project/{project-id}/gitlab-info")
    @ResponseBody
    @ResponseStatus(code = HttpStatus.OK)
    public String getGitlabStatus(@PathVariable("project-id") String project_id) throws JSONException {
        JSONObject glStatus = new JSONObject();
        glStatus.put("has-gitlab", "False");
        glStatus.put("gitlab-access-token", "None");
        if( project_id.equals("")) {
            throw new NoEntryException();
        }
        String getScript = "SELECT * FROM gitdb.Repository " +
            "WHERE id IN (SELECT idRepo FROM gitdb.ProjectRepo " +
                "WHERE projectId="+project_id+");";
        HashMap<String, FieldType> fields = new HashMap<String, FieldType>();
        fields.put("url", FieldType.STRING);
        fields.put("service", FieldType.STRING);
        fields.put("id", FieldType.STRING);
        JSONArray repos = new JSONArray();
        try {
            repos = dbHandler.executeQuery(getScript, fields);
            for(int i = 0; i < repos.length(); i++) {
                if (repos.getJSONObject(i).getString("service").equals("gitlab")) {
                    glStatus.put("has-gitlab", "True");
                    getScript = "SELECT * FROM gitdb.Project " +
                        "WHERE projectId="+project_id+";";
                    fields.clear();
                    fields.put("gitlabToken", FieldType.STRING);
                    JSONArray projects = dbHandler.executeQuery(getScript, fields);
                    assert projects.length() == 1;
                    glStatus.put("gitlab-access-token",
                        projects.getJSONObject(0).getString("gitlabToken"));
                    return glStatus.toString();
                }
            }
        }
        catch (NoEntryException | ClassNotFoundException e) {
            return glStatus.toString();
        }
        return glStatus.toString();
    }


    private String authenticateGoogleProfile(String id_token, boolean is_put) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
        .setAudience(Collections.singletonList("12178522373-eiukpdtqbjg8cmj0no3tjbmisl3qres2.apps.googleusercontent.com")).build();

        try{
            GoogleIdToken idToken = verifier.verify(id_token);
            if (idToken != null) {
                Payload payload = idToken.getPayload();

                String userId = payload.getSubject();
                // Get profile information from payload
                String email = payload.getEmail();
                boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");
                String locale = (String) payload.get("locale");
                String familyName = (String) payload.get("family_name");
                String givenName = (String) payload.get("given_name");
                return email;
            }
            else {
                System.out.println("Invalid ID token.");
            }
        }
        catch (IOException | GeneralSecurityException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String checkAccess(String project_id, String id_token, boolean is_get) throws ForbiddenException, ClassNotFoundException, JSONException, IOException {
        String auth_email;
        // Get email from google api
        auth_email = authenticateGoogleProfile(id_token, is_get);
        if(auth_email == null) {
            throw new UnauthorisedException();
        }
        // Check if email is attached to the project
        GetJSONReader jsonReader= new GetJSONReader();
        String url = "http://spmdhomepage-env.eba-upzkmcvz.ap-southeast-2.elasticbeanstalk.com/user-project-service/get-user?email="+auth_email;
        JSONObject json = jsonReader.readJsonFromUrl(url);
        JSONArray projects = json.getJSONObject("entry").getJSONArray("projects");
        for (int i = 0; i < projects.length(); i++) {
            JSONObject project = projects.getJSONObject(i);
            if (project.getString("projectId").equals(project_id)) {
                return auth_email;
            }
        }
        if (!is_get) {
            return auth_email;
        }
        throw new ForbiddenException();
    }
    /*
    Delete: Repository from project
    */
    @CrossOrigin
    @ResponseBody
    @DeleteMapping(path = "/project/{project-id}/repository")
    public String deleteRepo(@PathVariable("project-id") String id, @RequestParam("repo-id") String repo_id,
                          @RequestParam("token") Optional<String> token) throws NoEntryException, ForbiddenException, ClassNotFoundException, JSONException {
        if( repo_id.equals("") || id.equals("") ) {
            throw new NoEntryException();
        }
        String deleteScript =  "DELETE FROM gitdb.ProjectRepo " +
                "WHERE projectId="+id+" AND " +
                "idRepo='"+repo_id+"';";

        int rowsChanged = dbHandler.executeUpdate(deleteScript);
        // Throw error if row wasn't changed
        if (rowsChanged == 0) {
            throw new ForbiddenException();
        }
        else {
            JSONObject returnObject = new JSONObject();
            returnObject.put("repo-id", repo_id);
            return returnObject.toString();
        }
    }

}
