package monash.edu.git;

import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@SpringBootApplication
public class GitApplication {

	public static void main(String[] args) {


		try {
			GitRepository gitRepository=new GitRepository("1296269");
			int a=0;
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}

		SpringApplication.run(GitApplication.class, args);
		System.out.println("test");

	}

}
