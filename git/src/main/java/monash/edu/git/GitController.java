package monash.edu.git;

import org.json.JSONException;
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

    @RequestMapping("/contributors")
    public List<String> getContributions(String apiUrl) throws IOException, JSONException {
        return gitService.getContributions();
    }
}
