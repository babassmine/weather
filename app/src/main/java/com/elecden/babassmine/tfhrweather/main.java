package com.elecden.babassmine.tfhrweather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;


//async task that retrieves a bitmap array of the hourly conditions
class getImage extends AsyncTask<String, Integer, Bitmap[]>{

    @Override
    protected Bitmap[] doInBackground(String... params) {
        Bitmap[] images = new Bitmap[24];
        URL url = null;
        HttpURLConnection connection = null;
        for (int i=0; i<24; i++) {
            try {
//                Log.d("Image URL", params[i]);//, Toast.LENGTH_LONG);
//                Log.d("Image URL", params[i].replace("k","i"));//, Toast.LENGTH_LONG);
                url = new URL(params[i]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


            try {
                connection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                Log.w("Image connection", "bad");
            }

            try {
                connection.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                images[i] = BitmapFactory.decodeStream((InputStream) connection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return images;
    }

    protected void onPostExecute(Bitmap result) {
        return;
    }
}

//async task that connect to weather underground that pulls down the weather forecast
class getWeather extends AsyncTask<URL, Integer, JsonObject>{

    @Override
    protected JsonObject doInBackground(URL... params) {
        URL url = params[0];
        JsonParser jp = new JsonParser();

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonElement root = null;
        try {
            root = jp.parse(new InputStreamReader((InputStream) connection.getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root.getAsJsonObject();
    }

    @Override
    protected void onPostExecute(JsonObject result) {
        return;
    }

}

public class main extends ActionBarActivity implements LocationListener{

    private DatabaseManager mydManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JsonParser jp = new JsonParser();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        Location current = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        ConnectivityManager conn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = conn.getActiveNetworkInfo();


        getWeather g = new getWeather();
        JsonObject response = null;
        Bitmap[] icon_image = new Bitmap[24];
        Date today = new Date();
//        Log.w("wlocation", current.getLatitude()+","+current.getLongitude());


        try {
                if (current==null){
                    try{
                        Thread.sleep(2000);
                    }catch (InterruptedException e){

                    }
                    if (current==null){
                        Toast.makeText(this, "GPS not enabled", Toast.LENGTH_LONG).show();
                    }
                }
//            while (current==null){
//                if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//                    Toast.makeText(this, "GPS not enabled", Toast.LENGTH_LONG);
//                }
//            }
            IpLookup i = new IpLookup();
            String ip = null;
            if(current==null) {
                ip = i.execute().get();
                if (ip!=null){
                    response = g.execute(new URL("http://api.wunderground.com/api/7940be4d28156ec3/hourly/q/" + ip + ".json")).get();
                }else{
                    Toast.makeText(this, "Internet Connectivity not enabled", Toast.LENGTH_LONG).show();
                }
            }else{
                response = g.execute(new URL("http://api.wunderground.com/api/7940be4d28156ec3/hourly/q/"+ current.getLatitude()+","+current.getLongitude()+ ".json")).get();

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        JsonArray ja = response.get("hourly_forecast").getAsJsonArray();

        String[] datal = new String[24];
        for (int i=0; i<24; i++){
            datal[i] = ja.get(i).getAsJsonObject().get("FCTTIME").getAsJsonObject().get("civil").getAsString()+","+
                    ja.get(i).getAsJsonObject().get("temp").getAsJsonObject().get("english").getAsString()+","+
                    ja.get(i).getAsJsonObject().get("condition").getAsString()+","+
                    ja.get(i).getAsJsonObject().get("humidity").getAsString()+","+
                    ja.get(i).getAsJsonObject().get("icon_url").getAsString();

        }

        String[] temp = new String[24];
        String[] hum = new String[24];
        String[] con = new String[24];
        String[] hr = new String[24];
        String[] gif = new String[24];

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        int i=0;
        int k=0;
        String[] tmp = null;

        do{
            tmp = datal[k].split(",");
            temp[i] = new String(tmp[1]);
            hum[i] = new String(tmp[3]);
            con[i] = new String(tmp[2]);
            hr[i] = new String(tmp[0]);
            gif[i] = new String(tmp[4].replace("k","i"));

            k++;
            i++;

        }while(k<23);
        getImage ic = new getImage();
        try{
            icon_image = ic.execute(gif).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        ArrayList<HourlyForecast> itemsList = new ArrayList<HourlyForecast>();

        HourlyForecast[] list = new HourlyForecast[24];
        for (int j=0; j< 23; j++){
            itemsList.add(new HourlyForecast(hr[j],temp[j],hum[j],con[j], icon_image[j]));

        }
        forecast_adapter adapter = new forecast_adapter(this, itemsList);
        ListView lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(adapter);

        TextView print_date = (TextView) findViewById(R.id.label5);
        print_date.setText(today.getDate()+"/"+(today.getMonth()+1)+"/"+String.valueOf(today.getYear()).substring(1));



    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

