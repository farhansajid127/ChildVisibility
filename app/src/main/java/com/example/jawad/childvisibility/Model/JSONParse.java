package com.example.jawad.childvisibility.Model;

import android.util.Log;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONParse {
    static String json = "";
    HttpURLConnection urlConnection;
    // constructor
    public JSONParse() {
    }
    public String getJSONFromUrl(String url) {
        StringBuilder result = new StringBuilder();

        try {
            URL url_ = new URL(url);
            urlConnection = (HttpURLConnection) url_.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }
        Log.d("Result" ,result.toString());
        json =result.toString();
        return json;

    }
}