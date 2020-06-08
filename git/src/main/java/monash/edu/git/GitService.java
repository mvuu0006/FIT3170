package monash.edu.git;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class GitService {
    String gitUrl="https://api.github.com/projects/1002604";


    public List<String> getContributions() throws IOException, JSONException {

        List<String> commitList = new ArrayList<String>();

        // Original URL : https://github.com/tensorflow/tensorflow.git
        String reposUrl="https://api.github.com/repos/tensorflow/tensorflow/contributors";

        // THis is a test URL that has a jsonObject
        //String reposUrl="http://echo.jsontest.com/key/value/one/two";

        // Class that reads from a URL
        GetJSONReader jsonReader= new GetJSONReader();
        JSONObject json = jsonReader.readJsonFromUrl(reposUrl);

        return commitList;
    }
}
