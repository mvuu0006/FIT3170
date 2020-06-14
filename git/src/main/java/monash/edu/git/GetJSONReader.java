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
        String accessToken="300957cf2e509361ce59c68f5dab0b03a0a09501";

        // Creating the URL to set properties to it
        URL newUrl=new URL(url);
        HttpURLConnection conn = (HttpURLConnection) newUrl.openConnection();

        // Setting properties to the URL
        conn.setRequestProperty("Content-Type","application/json");
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", accessToken);

        // Reading from the URl and creating a JSON object
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String jsonText = readAll(rd);
            jsonText="{ entry: "+jsonText +"}";
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            conn.disconnect();
        }

    }




}
