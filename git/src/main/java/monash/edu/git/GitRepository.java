package monash.edu.git;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GitRepository {
    private JSONObject contributors;
    private JSONObject commits;
    private JSONObject repoInfo;
    public String githubUsername;
    public String repoName;
    private JSONObject commitTime;
    private String gitId;
    private JSONArray tableData;

    private ArrayList<String> label;
    private ArrayList<int[]> dataSet;

    // Constructor that creates the Repository object based on username and reponame
    public GitRepository(String gitUsername, String repoName) throws IOException, JSONException {
        this.githubUsername = gitUsername;
        this.repoName = repoName;

        // Creating the URL
        String repoUrl = "https://api.github.com/repos/" + gitUsername + "/" + repoName;

        // Class that reads from a URL and returns info in JSON format
        GetJSONReader jsonReader= new GetJSONReader();
        JSONObject json = jsonReader.readJsonFromUrl(repoUrl);

        // Extracting the array from the JSON Object
        JSONObject jsonObject = json.getJSONObject("entry");
        // Getting the gitId
        this.gitId = jsonObject.getString("id");

        constructRepoInfo(gitUsername, repoName);
    }

    // Constructor that creates the Repository object based on ID
    public GitRepository(String id) throws IOException, JSONException {
        this.gitId=id;
        String gitUrl = "https://api.github.com/repositories/" + id;

        // Class that reads from a URL and returns info in JSON format
        GetJSONReader jsonReader= new GetJSONReader();
        JSONObject json = jsonReader.readJsonFromUrl(gitUrl);

        // Extracting the array from the JSON Object
        JSONObject jsonObject = json.getJSONObject("entry");

        // Getting the username and reponame
        this.repoName=jsonObject.getString("name");
        this.githubUsername=jsonObject.getJSONObject("owner").getString("login");
        this.gitId = jsonObject.getString("id");
        constructRepoInfo(this.githubUsername,this.repoName);

    }

    // Function that creates the RepoInfo JSOn object that will be used to display information on the front end
    private void constructRepoInfo(String gitUsername, String repoName) throws IOException, JSONException {
        constructRepoContributors(gitUsername, repoName);
        constructRepoCommits(gitUsername, repoName);
        repoInfo=new JSONObject();
        repoInfo.put("UserName", gitUsername);
        repoInfo.put("RepositoryName", repoName);
        repoInfo.put("commits", commits);
        repoInfo.put("contributions", contributors);
        repoInfo.put("GitId",gitId);

        repoInfo.put("labels", label);
        repoInfo.put("data", dataSet);
        repoInfo.put("tableData",tableData);
    }

    private void constructRepoContributors(String gitUsername, String repoName) throws IOException, JSONException {
        contributors=new JSONObject();

        // Creating a URL
        String reposUrl = "https://api.github.com/repos/" + gitUsername + "/" + repoName + "/contributors";

        // Class that reads from a URL and returns info in JSON format
        GetJSONReader jsonReader= new GetJSONReader();
        JSONObject json = jsonReader.readJsonFromUrl(reposUrl);

        // Extracting the array from the JSON Object
        JSONArray jsonArray = json.getJSONArray("entry");

        // Calculating the total cotributions
        int totalContributions=0;
        for(int i=0;i<jsonArray.length();i++)
        {
            totalContributions=totalContributions+jsonArray.getJSONObject(i).getInt("contributions");
        }

        // Calculating the contribution percentage of each team member in the repo
        int other = 0;
        for(int i=0;i<jsonArray.length();i++)
        {
            int individualContribution=jsonArray.getJSONObject(i).getInt("contributions");
            double contributionPercent=((double)individualContribution/totalContributions)*100;
            contributionPercent=Math.round(contributionPercent*100.0)/100.0;
            if (contributionPercent <= 3) {
                other += contributionPercent;}
            else{
            String name=jsonArray.getJSONObject(i).getString("login");
            contributors.put(name,contributionPercent);}
        }
        if (other != 0){contributors.put("Other", other);}
    }

    private void constructRepoCommits(String gitUsername, String repoName) throws IOException, JSONException {
        commits=new JSONObject();
        commitTime= new JSONObject();
        tableData= new JSONArray();

        // Creating the URL
        String commitsUrl = "https://api.github.com/repos/" + gitUsername + "/" + repoName + "/commits";

        // Class that reads from a URL and returns info in JSON format
        GetJSONReader jsonReader= new GetJSONReader();
        JSONObject json = jsonReader.readJsonFromUrl(commitsUrl);

        // Extracting the array from the JSON Object
        JSONArray jsonArray = json.getJSONArray("entry");

        createDataSet(jsonArray);
        createTableData(jsonArray);
        // Loop that goes through all the commits and extracts its authors
        // Also increments commit number if author already exists in commit JSONObject
        for (int i=0;i<jsonArray.length();i++)
        {

            String name=jsonArray.getJSONObject(i).getJSONObject("commit").getJSONObject("author").getString("name");
            String time=jsonArray.getJSONObject(i).getJSONObject("commit").getJSONObject("committer").getString("date");
            if (commits.has(name))
            {
                int existingComments=commits.getInt(name)+1;
                commits.put(name, existingComments);
            }
            else {
                if(!commitTime.has(time))
                {
                    commitTime.put(name,time);

                }
                commits.put(name,1);
            }
        }
    }

    private void createDataSet(JSONArray jsonArray) throws JSONException {
        //pieObj = new JSONObject();

        label=new ArrayList<String>();
        dataSet = new ArrayList<int[]>();

        for(int i=0;i<jsonArray.length();i++)
        {
            //pieChartObject pieChartObj = new pieChartObject();
            String name=jsonArray.getJSONObject(i).getJSONObject("commit").getJSONObject("author").getString("name");
            String date=jsonArray.getJSONObject(i).getJSONObject("commit").getJSONObject("committer").getString("date");


            if (label.contains(name))
            {
                //pieChartObj= (pieChartObject) pieObj.get(name);
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

    public void createTableData(JSONArray commitInfo) throws JSONException {
        for (int i=0;i<commitInfo.length();i++)
        {
            String name=commitInfo.getJSONObject(i).getJSONObject("commit").getJSONObject("author").getString("name");
            String date=commitInfo.getJSONObject(i).getJSONObject("commit").getJSONObject("committer").getString("date");
            String commit_desc=commitInfo.getJSONObject(i).getJSONObject("commit").getString("message");

            //2012-03-06T23:06:50Z
            date=date.substring(0,10)+ " at "+date.substring(11,19);

            JSONObject table_entry=new JSONObject();
            table_entry.put("name",name);
            table_entry.put("date",date);
            table_entry.put("commit_description",commit_desc);

            tableData.put(table_entry);
        }
    }

    public JSONObject getInfo() {
        return repoInfo;
    }

    public  JSONObject getContributors(){
        return contributors;
    }

    public  JSONObject getCommits(){
        return commits;
    }

    public String getGitId() {
        return gitId;
    }
}