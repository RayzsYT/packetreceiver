package de.rayzs.packetreceiver.utils.json;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class JsonReader {

    private String result;

    public JsonReader(String url) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Accept", "text/html");
            result = IOUtils.toString(con.getInputStream());
        } catch (IOException ignored) { }
    }

    public Object get(String search) {
        try {
            if (result.isEmpty()) return "invalid name";
            JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(result);
            return UUIDObject.get(search);
        } catch (ParseException error) { return "error -> " + error.getMessage(); }
    }

    public Object get(String searchingFor, String search) {
        try {
            if (result.isEmpty()) return "invalid name";
            JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(result);
            JSONObject secondObject = (JSONObject) UUIDObject.get(searchingFor);
            return secondObject.get(search);
        } catch (ParseException error) { return "error -> " + error.getMessage(); }
    }
}