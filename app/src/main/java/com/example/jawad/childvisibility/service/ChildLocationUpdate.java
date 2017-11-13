package com.example.jawad.childvisibility.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jawad.jdmaterialdesign.SharedPreferencesMethod;
import com.example.jawad.jdmaterialdesign.StaticFunctions;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.example.jawad.jdmaterialdesign.SharedPreferencesMethod.getDefaultInt;
import static com.example.jawad.jdmaterialdesign.SharedPreferencesMethod.getDefaultString;
import static com.example.jawad.jdmaterialdesign.SharedPreferencesMethod.setDefaultString;

public class ChildLocationUpdate extends Service implements LocationListener{
    final static String ACTION = "NotifyServiceAction";
    NotifyServiceReceiver notifyServiceReceiver;
    int  USERID,Login_status;
    double lat;
    double lng;
    HttpURLConnection urlConnection;
    StaticFunctions staticFunction;
    SharedPreferencesMethod sharedPreferencesMethod;
    LatLng userlocation;
    private LocationManager locationManager;
    @Override
    public void onCreate() {
        sharedPreferencesMethod = new SharedPreferencesMethod(ChildLocationUpdate.this);
        try {
            Log.d("service_location" , "Create");
            Login_status=sharedPreferencesMethod.getDefaultInt("status");
            if (Login_status==2) {
                try {
                    staticFunction = new StaticFunctions();
                    if(staticFunction.hasInternentConnection(ChildLocationUpdate.this) && hasGPSConnection() && staticFunction.hasGPSConnection(ChildLocationUpdate.this)) {
                        notifyServiceReceiver = new NotifyServiceReceiver();
                        Log.d("Testing ", "Current Location  Service got created");
                        super.onCreate();
                    }
                }catch (Exception e){
                }
            }
        }catch (Exception e){
            Log.d("Exception" , e+"");
        }

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferencesMethod = new SharedPreferencesMethod(ChildLocationUpdate.this);
        try {
            Log.d("service_location" , "start");
            staticFunction = new StaticFunctions();
            Login_status=sharedPreferencesMethod.getDefaultInt("status");
            if(Login_status==2){
                try{
                    if(staticFunction.hasInternentConnection(ChildLocationUpdate.this) && hasGPSConnection() && staticFunction.hasGPSConnection(ChildLocationUpdate.this)) {
                        Log.d("service==> " ,"Current Location Service created");
                        USERID =getDefaultInt("PID");
                        Log.d("CurrentUserID==> ", "" + USERID);
                        if(hasGPSConnection()){
                            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 100, this);
                            if(getUserlocation()!= null){
                                lat= getUserlocation().latitude;
                                Log.d("latiii", lat+"");
                                lng= getUserlocation().longitude;
                                Log.d("longi", lng+"");
                                if(!(Double.toString(lat).equalsIgnoreCase(getDefaultString("p_lat")) ||
                                        Double.toString(lng).equalsIgnoreCase(getDefaultString("p_long") ) )){
                                    setDefaultString("p_lat",Double.toString(lat));
                                    setDefaultString("p_long",Double.toString(lng));
                                    new UpdateChildLocation(USERID,lat,lng).execute();
                                }
                            }else {
                                Log.d("Current Location user Location  ======> " ,"error");
                            }
                        }
                    }
                }catch (Exception e){

                }

            }
        }catch (Exception e){
            Log.d("Exception" , e+"");
        }
        return  START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public LatLng getUserlocation() {
        return userlocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        Log.d("Latitude= > ", lat +"");
        userlocation = new LatLng(location.getLatitude(),location.getLongitude());
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

    public class NotifyServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION);
            registerReceiver(notifyServiceReceiver, intentFilter);
            Intent intent = new Intent(ChildLocationUpdate.this, ChildLocationUpdate.class);
            ChildLocationUpdate.this.startService(intent);

        }
    }
    public boolean hasGPSConnection(){
        LocationManager manager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(statusOfGPS){
            return true;
        }else{
            return false;
        }
    }
    private class UpdateChildLocation extends AsyncTask {
        int _id;
        double lat_, long_;
        public UpdateChildLocation(int id,double lat, double lng){
            _id = id;
            lat_=lat;
            long_ = lng;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Object doInBackground(Object... params) {
            String url = "http://wasifapps.com/updateChildLocationGet.php?id="+_id+"&lat="+lat_+"&long="+long_;
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
            Log.d("Result location update.." ,result.toString());
            Log.d("service_location" , result.toString());
            return null;
        }
        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
        }
    }
}
