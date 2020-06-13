package monash.edu.git;

import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@SpringBootApplication
public class GitApplication {

	public static void main(String[] args) {

//		GitService gitService = new GitService();
//		try {
//			gitService.getCommits();
//		} catch (IOException | JSONException e) {
//			e.printStackTrace();
//		}

		try {
			GitRepository gitRepository=new GitRepository("octocat","Hello-World");
			int a=0;
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}

		SpringApplication.run(GitApplication.class, args);
		System.out.println("test");

	}

}
