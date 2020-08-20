package monash.edu.git;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class GitLabRepository {
    private String id;
    private JSONObject commits;

    public GitLabRepository(String id) throws IOException, JSONException {
        this.id=id;

        String repoUrl="https://gitlab.com/api/v4/projects/"+id;
        constructRepoCommits(repoUrl);

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
