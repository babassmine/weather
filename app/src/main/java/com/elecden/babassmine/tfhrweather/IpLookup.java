package com.elecden.babassmine.tfhrweather;

import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by babassmine on 3/14/15.
 * Copyright Reserved
 * Discover Salone App
 */
public class IpLookup extends AsyncTask<Void, Integer, String> {

    @Override
    protected String doInBackground(Void... params) {
        String ip = null;
        String sURL = "http://api.wunderground.com/api/7940be4d28156ec3/geolookup/q/autoip.json";
        //7940be4d28156ec3
        // Connect to the URL
        try {
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();
            if(request.getResponseCode()==200){
                JsonParser jp = new JsonParser();
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
                JsonObject rootobj = root.getAsJsonObject(); // may be Json Array if it's an array, or other type if a primitive
                ip = rootobj.get("location").getAsJsonObject().get("zip").getAsString();
            }else {
                return ip;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ip;
    }

}
