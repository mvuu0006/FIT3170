package monash.edu.git;

import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class GitApplication {

	public static void main(String[] args) throws IOException, JSONException {
		//GitLabRepository gitLabRepository = new GitLabRepository("3398900");
		SpringApplication.run(GitApplication.class, args);
	}

}
