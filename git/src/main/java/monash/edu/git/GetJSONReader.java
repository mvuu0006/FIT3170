package monash.edu.git;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class GetJSONReader {


    private static String readAll(Reader rd) throws IOException {
        /*
        Function that reads all the text from the json page
         */
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        /*
        Function that reads the JSON object from the given URL
         */
        //URL newUrl=new URL(url);
        //HttpURLConnection conn = (HttpURLConnection) newUrl.openConnection();

        //conn.setRequestProperty("Accept","application/vnd.github.inertia-preview+json");
        //conn.setRequestProperty("Content-Type","application/json");
        //conn.setRequestMethod("GET");
        //conn.setRequestProperty("Authorization", "019d9e8fdb7c2282c7aa91864ce52050692d1c02");

        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            //BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String jsonText = readAll(rd);
            jsonText="{ entry: "+jsonText +"}";
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            //conn.disconnect();
            is.close();
        }

    }




}
