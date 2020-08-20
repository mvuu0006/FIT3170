package monash.edu.git;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class GitLabRepository {
    private String id;
    private JSONObject commits;
    private JSONObject issues;

    public GitLabRepository(String id) throws IOException, JSONException {
        this.id=id;

        String repoUrl="https://gitlab.com/api/v4/projects/"+id;
        constructRepoCommits(repoUrl);
        constructRepoIssues(repoUrl);

    }

    public void constructRepoIssues(String reposUrl) throws IOException, JSONException {
        String issuesUrl=reposUrl+"/issues";
        issues=new JSONObject();

        GetJSONReader jsonReader= new GetJSONReader();
        JSONObject json = jsonReader.readJsonFromUrl(issuesUrl);

        JSONArray jsonArray = json.getJSONArray("entry");

        for (int i=0; i<jsonArray.length();i++)
        {
            String issue_type=jsonArray.getJSONObject(i).getString("state");

            if(issue_type.equals("opened"))
            {
                String author = jsonArray.getJSONObject(i).getJSONObject("author").getString("name");
                if (issues.has(author))
                {
                    int issue_count=issues.getInt(author) + 1;
                    issues.put(author,issue_count);
                }
                else
                {
                    issues.put(author,1);
                }
            }
        }
        int a=0;

    }

    public void constructRepoCommits(String repoUrl) throws IOException, JSONException {
        String commitsUrl=repoUrl+"/repository/commits";
        commits = new JSONObject();

        GetJSONReader jsonReader= new GetJSONReader();
        JSONObject json = jsonReader.readJsonFromUrl(commitsUrl);

        JSONArray jsonArray = json.getJSONArray("entry");

        for (int i=0; i<jsonArray.length();i++)
        {
            String name=jsonArray.getJSONObject(i).getString("author_name");

            if (commits.has(name))
            {
                int commits_count=commits.getInt(name) + 1;
                commits.put(name,commits_count);
            }
            else
            {
                commits.put(name,1);
            }
        }
    }

}
