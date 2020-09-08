package monash.edu.git;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class GitApplication {

	public static void main(String[] args) throws IOException, JSONException {
		//GitLabRepository gitLabRepository = new GitLabRepository("3398900");
		//String accessToken = "plzEnterOwnAccessToken. This won't work";
		//GitLabRepository gitLabRepository = new GitLabRepository("10273",accessToken);
		//JSONObject repoinfo = gitLabRepository.getInfo();
		//GitRepository gitRepository = new GitRepository("octocat", "Hello-World");
		SpringApplication.run(GitApplication.class, args);
	}

}
