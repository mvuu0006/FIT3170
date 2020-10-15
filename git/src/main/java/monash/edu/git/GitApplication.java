package monash.edu.git;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

@SpringBootApplication
public class GitApplication {

	public static void main(String[] args) throws IOException, JSONException {
		//GitLabRepository gitLabRepository = new GitLabRepository("3398900");
		String accessToken = "fcadf860759cbeb018f91d78167fb5a7e69138f446672834e209f25a0c0e7d08";
		GitLabInterface gitLabRepository = new GitLabInterface();
		JSONArray repoinfo = gitLabRepository.getRepoCommits("10273",accessToken);
		JSONObject commitinfo = gitLabRepository.getLastContributions("10273",accessToken);
		var x = "10273,10238";
		//GitRepository gitRepository = new GitRepository("octocat", "Hello-World");
		SpringApplication.run(GitApplication.class, args);
	}

}
