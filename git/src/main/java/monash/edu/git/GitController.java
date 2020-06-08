package monash.edu.git;

import netscape.javascript.JSObject;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/")
@CrossOrigin
public class GitController {

    GitService gitService;

    // Method responsible for getting all the contributors and their contribution percent
    @RequestMapping("/contributors")
    public JSONObject getContributions(String apiUrl) throws IOException, JSONException {
        return gitService.getContributions();
    }
}
