package monash.edu.git;

import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
    public String hello() throws JSONException {
        return "hello";
    }

}
