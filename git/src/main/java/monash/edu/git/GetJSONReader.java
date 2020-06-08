package monash.edu.git;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

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
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            //jsonText=jsonText.substring(1,jsonText.length()-1);
            jsonText="{ entry: "+jsonText +"}";
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }

    }




}
