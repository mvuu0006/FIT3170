package monash.edu.git;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

public class GitLabRepository {
    private String id;
    private String accesstoken;
    private JSONObject commits;
    private JSONObject issues;
    private JSONObject issue_stats;
    private JSONObject merge_requests;
    private JSONObject branches;
    private JSONObject contribution;
    private Set<String> timeSet;

    public GitLabRepository(String id) throws IOException, JSONException {
        this.id=id;

            String repoUrl="https://gitlab.com/api/v4/projects/"+id;
        //String repoUrl="https://git.infotech.monash.edu/api/v4/projects/"+id;
        timeSet = new HashSet<String>();
        commits = new JSONObject();
        commits = constructRepoCommits(repoUrl, "master");
        constructRepoIssues(repoUrl);
        constructBranchInfoandMergeRequests(repoUrl);
    }
    public GitLabRepository(String id, String accesstoken) throws IOException, JSONException {
        this.id=id;
        this.accesstoken = accesstoken;
        String repoUrl="https://git.infotech.monash.edu/api/v4/projects/"+id;
        timeSet = new HashSet<String>();
        commits = new JSONObject();
        commits = constructRepoCommits(repoUrl, "master");
        constructRepoIssues(repoUrl);
        constructBranchInfoandMergeRequests(repoUrl);

    }

    public void constructRepoIssues(String reposUrl) throws IOException, JSONException {
        String issuesUrl=reposUrl+"/issues";
        if (accesstoken != null){
            issuesUrl+= "?access_token=" + this.accesstoken;
        }
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

    }

    public JSONObject constructRepoCommits(String repoUrl, String branch) throws IOException, JSONException {
        String commitsUrl=repoUrl+"/repository/commits?ref_name="+branch;
        if (this.accesstoken != null) {
                commitsUrl +="&access_token="+this.accesstoken;}
        JSONObject branchcommits = new JSONObject();

        GetJSONReader jsonReader= new GetJSONReader();
        JSONObject json = jsonReader.readJsonFromUrl(commitsUrl);

        JSONArray jsonArray = json.getJSONArray("entry");
        ArrayList<String> commitdates = new ArrayList<>();

        for (int i=0; i<jsonArray.length();i++)
        {
            String name=jsonArray.getJSONObject(i).getString("author_name");

            if (branchcommits.has(name))
            {
                int commits_count=branchcommits.getInt(name) + 1;
                branchcommits.put(name,commits_count);
            }
            else
            {
                branchcommits.put(name,1);
            }
            commitdates.add(jsonArray.getJSONObject(i).getString("created_at"));
            timeSet.add(jsonArray.getJSONObject(i).getString("created_at"));
        }
        branchcommits.put("Timestamps", commitdates);
        constructRepoContributions(branchcommits);
        return branchcommits;
    }

    public void constructRepoContributions(JSONObject myBranchCommits) throws JSONException {
        int contributions_total=0;
        contribution = new JSONObject();
        Iterator<String> keys = myBranchCommits.keys();

        while (keys.hasNext())
        {
            String key=keys.next();
            if (key!="Timestamps")
            {
                contributions_total= contributions_total+myBranchCommits.getInt(key);
            }
        }

        Iterator<String> commit_iterator = myBranchCommits.keys();

        while (commit_iterator.hasNext())
        {
            String name=commit_iterator.next();
            if (name != "Timestamps") {
                int contribution_percent = (myBranchCommits.getInt(name) *100/ contributions_total);
                contribution.put(name, contribution_percent);
            }
        }
        int a=0;
    }
    public JSONObject getInfo() throws IOException, JSONException{
        JSONObject repoInfo = new JSONObject();
        repoInfo.put("commits",commits);
        repoInfo.put("issues",issues);
        repoInfo.put("gitID", id);
        repoInfo.put("issue_stats", issue_stats);
        repoInfo.put("merge_requests", merge_requests);
        repoInfo.put("branches", branches);
        repoInfo.put("contribution", contribution);
        repoInfo.put("time_sets", timeSet);

        return repoInfo;
    }

    public void constructBranchInfoandMergeRequests(String repoUrl) throws IOException, JSONException {

        String merge_requestsUrl=repoUrl+"/merge_requests";
        if (this.accesstoken != null) {
            merge_requestsUrl +="?access_token="+this.accesstoken;}
        merge_requests = new JSONObject();
        JSONArray mergearray = new JSONArray();
        GetJSONReader jsonReader= new GetJSONReader();
        JSONObject json = jsonReader.readJsonFromUrl(merge_requestsUrl);
        mergearray = json.getJSONArray("entry");

        for (int i=0; i<mergearray.length();i++)
        {
            String created=mergearray.getJSONObject(i).getString("created_at");
            JSONObject mergeinfo = new JSONObject();
            mergeinfo.put("title", mergearray.getJSONObject(i).getString("title"));
            mergeinfo.put("author",(mergearray.getJSONObject(i).getJSONObject("author").getString("name")));
            merge_requests.put(created, mergeinfo);
        }

        String branchesUrl=repoUrl+"/repository/branches";
        if (this.accesstoken != null) {
            branchesUrl +="?access_token="+this.accesstoken;}
        branches = new JSONObject();
        JSONArray branchesarray = new JSONArray();

        jsonReader= new GetJSONReader();
        json = jsonReader.readJsonFromUrl(branchesUrl);
        branchesarray = json.getJSONArray("entry");
        for (int i=0; i<mergearray.length();i++)
        {
            String name=branchesarray.getJSONObject(i).getString("name");
            JSONObject branchinfo = new JSONObject();
            branchinfo.put("mergedStatus", branchesarray.getJSONObject(i).getString("merged"));
            branchinfo.put("lastAuthor",(branchesarray.getJSONObject(i).getJSONObject("commit").getString("author_name")));
            branchinfo.put("lastCommitTime",(branchesarray.getJSONObject(i).getJSONObject("commit").getString("created_at")));
            branchinfo.put("branchCommits", constructRepoCommits(repoUrl, name));
            branches.put(name, branchinfo);
        }

    }

}

