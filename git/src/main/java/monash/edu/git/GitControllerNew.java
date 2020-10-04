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
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/git-db")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "http://localhost:3003"}, maxAge = 0)
public class GitControllerNew {
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
    @GetMapping(path = "/project")
    @ResponseBody
    public String getProjectRepos(@RequestParam("project-id") String id,
    @RequestParam("email") String email, @RequestParam("user-type") String user_type) throws NoEntryException, JSONException, ClassNotFoundException {
        if( id.equals("") || email.equals("") || user_type.equals("")) {
            throw new NoEntryException();
        }
        String getScript = "SELECT * FROM gitdb.Repository " +
            "WHERE url IN (SELECT idRepo FROM gitdb.ProjectRepo " +
                "WHERE projectId="+id+");";
        HashMap<String, FieldType> fields = new HashMap<String, FieldType>();
        fields.put("url", FieldType.STRING);
        fields.put("service", FieldType.STRING);
        JSONArray repos = dbHandler.executeQuery(getScript, fields);
        return repos.toString();
    }



    /*
     PUT: Repository into database
     */
    @CrossOrigin
    @PutMapping(path = "/project/{project-id}/repository")
    public void putRepo(@PathVariable("project-id") String id,
        @RequestParam("service") String service, @RequestParam("url") String url) throws NoEntryException, ForbiddenException, JSONException, ClassNotFoundException {
        if( url.equals("")  || service.equals("") || id.equals("") ) {
            throw new NoEntryException();
        }
        // TODO: Add restrictions to database to standardise service syntax (eg. all lower case)
        String putScript = "INSERT INTO gitdb.Repository(url, service) VALUES('"+url+"', '"+service+"')";
        int rowsChanged = dbHandler.executeUpdate(putScript);
        // Throw error if row wasn't added
        if (rowsChanged == 0) {
            throw new ForbiddenException();
        }
        putScript = "INSERT INTO gitdb.ProjectRepo(idRepo, projectId) VALUES('"+url+"', '"+id+"')";
        rowsChanged = dbHandler.executeUpdate(putScript);
        // Throw error if row wasn't added
        if (rowsChanged == 0) {
            throw new ForbiddenException();
        }
        return;
    }


    /*
     GET: Repository Commits (name of commitor and number of commits). Use for timeline and pie chart??
     */
    @GetMapping(path = "/project/{project-id}/repository/contribution")
    @ResponseBody
    public String getRepoContribution(@PathVariable("project-id") String id, @RequestParam("email") String email,
        @RequestParam("url") String url,
        @RequestParam("token") Optional<String> token) throws NoEntryException, JSONException, ClassNotFoundException, IOException {
        if( id.equals("")  || url.equals("") || email.equals("")) {
            throw new NoEntryException();
        }
        // Database code: 404 is returned if email does not have link to project id
        String findScript =  "SELECT * FROM gitdb.ProjectRepo " +
                               "WHERE projectId="+id+" AND EXISTS( " +
                                    "SELECT * FROM gitdb.StudentProject " +
                                        "WHERE emailStudent='"+email+"' AND projectId="+id+" " +
                                ") AND idRepo='"+url+"';";
        HashMap<String, FieldType> fields = new HashMap<String, FieldType>();
        fields.put("idRepo", FieldType.STRING);
        fields.put("projectId", FieldType.INT);
        JSONArray rowMap = dbHandler.executeQuery(findScript, fields);
        // Find out service from database
        findScript = "SELECT service FROM gitdb.Repository WHERE url='"+url+"'";
        fields.clear();
        fields.put("service", FieldType.STRING);
        JSONArray serviceMap = dbHandler.executeQuery(findScript, fields);
        if (serviceMap.length() > 1){
            throw new ForbiddenException();
        }
        String service = serviceMap.getJSONObject(0).getString("service").toLowerCase();
        // Get Contributors
        JSONArray contributors = new JSONArray();
        switch (service) {
            case "github":
                contributors = ghInterface.getRepoContributors(url);
                break;
            case "gitlab":
                if (token.isPresent()){
                    contributors = glInterface.getRepoCommits(url, token.get());
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
    public String getRepoCommits(@PathVariable("project-id") String id, @RequestParam("email") String email,
        @RequestParam("url") String url,
        @RequestParam("token") Optional<String> token) throws NoEntryException, JSONException, ClassNotFoundException, IOException {
        if( id.equals("")  || url.equals("") || email.equals("")) {
            throw new NoEntryException();
        }
        // Database code: 404 is returned if email does not have link to project id
        String findScript =  "SELECT * FROM gitdb.ProjectRepo " +
                               "WHERE projectId="+id+" AND EXISTS( " +
                                    "SELECT * FROM gitdb.StudentProject " +
                                        "WHERE emailStudent='"+email+"' AND projectId="+id+" " +
                                ") AND idRepo='"+url+"';";
        HashMap<String, FieldType> fields = new HashMap<String, FieldType>();
        fields.put("idRepo", FieldType.STRING);
        fields.put("projectId", FieldType.INT);
        JSONArray rowMap = dbHandler.executeQuery(findScript, fields);
        // Find out service from database
        findScript = "SELECT service FROM gitdb.Repository WHERE url='"+url+"'";
        fields.clear();
        fields.put("service", FieldType.STRING);
        JSONArray serviceMap = dbHandler.executeQuery(findScript, fields);
        if (serviceMap.length() > 1){
            throw new ForbiddenException();
        }
        String service = serviceMap.getJSONObject(0).getString("service").toLowerCase();
        // Get Contributors
        JSONArray contributors = new JSONArray();
        switch (service) {
            case "github":
                contributors = ghInterface.getRepoCommits(url);
                break;
            case "gitlab":
                if (token.isPresent()){
                    contributors = glInterface.getRepoCommits(url, token.get());
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
     GET: GitLab Access Token
     */
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

}
