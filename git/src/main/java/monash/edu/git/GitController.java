package monash.edu.git;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
@CrossOrigin
public class GitController {

    GitService gitService;

//    @Autowired
//    public GitController(GitService myGitService)
//    {
//        this.gitService=myGitService;
//    }

    @RequestMapping("/contributors")
    public List<String> getContributions(String apiUrl)
    {
        return gitService.getContributions(apiUrl);
    }
}
