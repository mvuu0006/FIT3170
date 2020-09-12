package monash.edu.git;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class GitLabRepository {
    private String id;
    private String accesstoken;
    private JSONObject issues;
    private String repoName;
    private JSONObject merge_requests;
    private JSONObject branches;
    private JSONObject contribution;
    private JSONObject allcommits;
    private JSONArray tableData;

    private ArrayList<String> label;
    private ArrayList<int[]> dataSet;

    public GitLabRepository(String id) throws IOException, JSONException {
        this.id=id;

        String repoUrl="https://gitlab.com/api/v4/projects/"+id;
        //String repoUrl="https://git.infotech.monash.edu/api/v4/projects/"+id;
        allcommits = new JSONObject();
        constructAllCommits(repoUrl, 1, new ArrayList<String>());
        constructRepoIssues(repoUrl);
        constructBranchInfoandMergeRequests(repoUrl);
        constructBasicInfo(repoUrl);
        getInfo();
    }
    public GitLabRepository(String id, String accesstoken) throws IOException, JSONException {
        this.id=id;
        this.accesstoken = accesstoken;
        String repoUrl="https://git.infotech.monash.edu/api/v4/projects/"+id;
        allcommits = new JSONObject();
        allcommits.put("Timestamps", new ArrayList<>());
        constructAllCommits(repoUrl, 1, new ArrayList<String>());
        constructRepoIssues(repoUrl);
        constructBranchInfoandMergeRequests(repoUrl);
        constructBasicInfo(repoUrl);
        //System.out.print(repoName);
        getInfo();

    }

    public void constructBasicInfo(String repoUrl) throws IOException, JSONException {
        if (accesstoken != null){
            repoUrl+= "?access_token=" + this.accesstoken;
        }

        GetJSONReader jsonReader= new GetJSONReader();
        JSONObject json = jsonReader.readJsonFromUrl(repoUrl);

        JSONObject jsonObject = json.getJSONObject("entry");

        repoName=jsonObject.getString("name");

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
        }
        branchcommits.put("Timestamps", commitdates);
        constructRepoContributions(branchcommits);
        return branchcommits;
    }

    public void constructAllCommits(String repoUrl, Integer page_no, ArrayList<String> commitdates) throws IOException, JSONException {
        String commitsUrl=repoUrl+"/repository/commits?all=true&per_page=10000&page="+String.valueOf(page_no);
        if (this.accesstoken != null) {
            commitsUrl +="&access_token="+this.accesstoken;}
        GetJSONReader jsonReader= new GetJSONReader();
        JSONObject json = jsonReader.readJsonFromUrl(commitsUrl);
        JSONArray jsonArray = json.getJSONArray("entry");
        tableData=new JSONArray();
        createTableData(jsonArray);
        createDataSet(jsonArray);
        if (jsonArray.length() != 0) {

            for (int i=0; i<jsonArray.length();i++)
            {
                String name=jsonArray.getJSONObject(i).getString("author_name");

                if (this.allcommits.has(name))
                {
                    int commits_count=this.allcommits.getInt(name) + 1;
                    this.allcommits.put(name,commits_count);
                }
                else
                {
                    this.allcommits.put(name,1);
                }
                commitdates.add(jsonArray.getJSONObject(i).getString("created_at"));
            }
            this.allcommits.put("Timestamps", commitdates);
            constructRepoContributions(this.allcommits);
            constructAllCommits(repoUrl, page_no+1, commitdates);}
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
        repoInfo.put("issues",issues);
        repoInfo.put("gitID", id);
        repoInfo.put("merge_requests", merge_requests);
        repoInfo.put("branches", branches);
        repoInfo.put("contribution", contribution);
        repoInfo.put("all_commits", allcommits);
        repoInfo.put("repoName", repoName);
        repoInfo.put("tableData",tableData);
        repoInfo.put("data", dataSet);
        repoInfo.put("labels", label);

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

    public void createTableData(JSONArray commitInfo) throws JSONException {
        for (int i=0;i<commitInfo.length();i++)
        {
            String name=commitInfo.getJSONObject(i).getString("author_name");
            String date=commitInfo.getJSONObject(i).getString("committed_date");
            String commit_desc=commitInfo.getJSONObject(i).getString("message");

            JSONObject table_entry=new JSONObject();
            table_entry.put("name",name);
            table_entry.put("date",date);
            table_entry.put("commit_description",commit_desc);

            tableData.put(table_entry);
       }
    }

    private void createDataSet(JSONArray jsonArray) throws JSONException {
        label=new ArrayList<String>();
        dataSet = new ArrayList<int[]>();

        for(int i=0;i<jsonArray.length();i++)
        {
            String name=jsonArray.getJSONObject(i).getString("author_name");
            String date=jsonArray.getJSONObject(i).getString("committed_date");


            if (label.contains(name))
            {
                int month= Integer.parseInt(date.substring(5,7))-1;
                int[]data = dataSet.get(label.indexOf(name));
                data[month]=data[month]+1;
                dataSet.set(label.indexOf(name),data);
            }
            else{

                label.add(name);

                int[] data = {0,0,0,0,0,0,0,0,0,0,0,0};
                int month= Integer.parseInt(date.substring(5,7))-1;
                data[month]=1;
                dataSet.add(data);
            }
        }

    }

}

