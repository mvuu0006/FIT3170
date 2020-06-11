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
import java.util.List;

@RestController
@RequestMapping("/git")
@CrossOrigin
public class GitController {

    GitService gitService = new GitService();

    private RestTemplate restTemplate;


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
    @GetMapping(path = "/projects/{projName}", produces="application/json")
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
            body.put("user", name);
            response.put("body", body);
        }
        response.put("status", status);
        return response.toString();
    }

    @PutMapping(path = "/projects/{projName}")
    public String putUser(@PathVariable("projName") String name) {
        if( name.equals("") ) {
            return "all users";
        }
        return "data for user: "+name;
    }
}
