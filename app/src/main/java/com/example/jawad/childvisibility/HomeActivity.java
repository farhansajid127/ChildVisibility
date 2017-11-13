package com.example.jawad.childvisibility;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.jawad.childvisibility.Model.ChilData;
import com.example.jawad.childvisibility.Model.JSONParse;
import com.example.jawad.childvisibility.Model.ToastMessage;
import com.example.jawad.childvisibility.service.ChildLocationUpdate;
import com.example.jawad.jdmaterialdesign.MaterialProgress;
import com.example.jawad.jdmaterialdesign.StaticFunctions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.jawad.childvisibility.Model.ToastMessage.ErrorToast;
import static com.example.jawad.childvisibility.Model.ToastMessage.SuccessToast;
import static com.example.jawad.jdmaterialdesign.SharedPreferencesMethod.getDefaultInt;
import static com.example.jawad.jdmaterialdesign.SharedPreferencesMethod.getDefaultString;
import static com.example.jawad.jdmaterialdesign.SharedPreferencesMethod.setDefaultBoolean;
import static com.example.jawad.jdmaterialdesign.SharedPreferencesMethod.setDefaultInt;
import static com.example.jawad.jdmaterialdesign.SharedPreferencesMethod.setDefaultString;
import static com.example.jawad.jdmaterialdesign.StaticFunctions.GoTo;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener, RoutingListener {
    DrawerLayout dlayout;
    private GoogleMap mMap;
    MaterialProgress pd;
    ImageView MENU;
    ListView Child_drawer_list;
    LatLng MyLocationLatlng;
    DB db;
    int Status_ID;
    TextView LOGOUT;
    String Marker_Title;
    int PID;
    int follow_child_status = 0;
    List<LatLng> list;
    StaticFunctions st;
    ToastMessage toastMessage;
    ImageView Add_New_Child, Refresh_data;
    List<ChilData> chilDatas = new ArrayList<>();
    List<ChilData> child_db_list = new ArrayList<>();
    private LocationManager locationManager;
    private static final float MIN_DISTANCE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        MapIntigration();
        init();
        if (st.hasGPSConnection(HomeActivity.this)) {
            if (Status_ID == 1) {
                dlayout.setDrawerListener(new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        MENU.setImageResource(R.drawable.ic_arrow_back_24dp);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        MENU.setImageResource(R.drawable.ic_menu_24dp);
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {

                    }
                });
                Child_drawer_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        dlayout.closeDrawer(Gravity.START);
                        if(follow_child_status==0){
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(chilDatas.get(i).getAddress(), 13);
                            mMap.animateCamera(cameraUpdate);
                        }
                    }
                });
                Add_New_Child.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater factory = LayoutInflater.from(HomeActivity.this);
                        final View view = factory.inflate(R.layout.add_new_child_layout, null);
                        final Dialog dialog = new Dialog(HomeActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(view);
                        dialog.getWindow().setGravity(Gravity.CENTER);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        final EditText EMAIL = (EditText) view.findViewById(R.id.email_frgrt_psd);
                        final Button CANCEL = (Button) view.findViewById(R.id.cancel_button);
                        final Button OK = (Button) view.findViewById(R.id.ok_button);
                        CANCEL.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        OK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (EMAIL.length() != 0 && isEmailRegister(EMAIL.getText().toString())) {
                                    dialog.dismiss();
                                    pd.Show();
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            String url = "http://wasifapps.com/searchChild.php";
                                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            try {
                                                                JSONObject jsonObject = new JSONObject(response);
                                                                Log.d("response", jsonObject.getString("status"));
                                                                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                                                                    pd.Dismiss();
                                                                    db.addNewEmail(new ChilData(EMAIL.getText().toString()));
                                                                    SuccessToast("Child Register Successfully!!");
                                                                } else {
                                                                    pd.Dismiss();
                                                                    toastMessage.ErrorToast("Child Email is not Exist!!!");
                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                                pd.Dismiss();
                                                            }
                                                            Log.d("response", response);
                                                        }
                                                    }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    pd.Dismiss();
                                                    toastMessage.ErrorToast("Connection Error Try Again!!!");
                                                }
                                            }) {
                                                @Override
                                                protected Map<String, String> getParams() throws AuthFailureError {
                                                    HashMap<String, String> parm = new HashMap<>();
                                                    parm.put("email", EMAIL.getText().toString().trim());
                                                    parm.put("pid", PID + "");
                                                    return parm;
                                                }
                                            };
                                            Volley.newRequestQueue(HomeActivity.this).add(stringRequest);
                                        }
                                    }, 3000);
                                } else if (EMAIL.length() != 0) {
                                    if (!isEmailRegister(EMAIL.getText().toString())) {
                                        toastMessage.ErrorToast("Email Already Register!!");
                                    }
                                }
                            }
                        });
                        dialog.show();
                    }
                });
            } else {
                Calendar cur_cal = Calendar.getInstance();
                Intent intent = new Intent(HomeActivity.this, ChildLocationUpdate.class);
                PendingIntent pintent = PendingIntent.getService(HomeActivity.this, 0, intent, 0);
                AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, cur_cal.getTimeInMillis(), (30 * 1) * 1000, pintent);
                AlertDialog(0,"This is child account,so you can't perform any action by login this account.LocationUpdate service is properly working in this account.\n Thank for your Contribution.");
           }
        } else {
           AlertDialog(021, "No GPS connection available yet, Enable GPS for continue!");
        }
    }

    public void init() {
        LOGOUT = (TextView) findViewById(R.id.logout_btn);
        LOGOUT.setOnClickListener(this);
        dlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Child_drawer_list = (ListView) findViewById(R.id.drawer_child_list);
        dlayout.setScrimColor(Color.TRANSPARENT);
        db = new DB(HomeActivity.this);
        pd = new MaterialProgress(HomeActivity.this);
        MENU = (ImageView) findViewById(R.id.menue_btn);
        Add_New_Child = (ImageView) findViewById(R.id.ad_new_child);
        Refresh_data = (ImageView) findViewById(R.id.refresh_data);
        Refresh_data.setOnClickListener(this);
        toastMessage = new ToastMessage(HomeActivity.this);
        MENU.setOnClickListener(this);
        st = new StaticFunctions();
        pd.Show();
        Status_ID = getDefaultInt("status");
        PID = getDefaultInt("PID");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker arg0) {
                View v = getLayoutInflater().inflate(R.layout.infowindow_layout, null);
                String Name, Email, Distance;
                String[] result = arg0.getTitle().split(",");
                Name = result[0];
                Email = result[1];
                double x = Double.parseDouble(result[2]);
                DecimalFormat df = new DecimalFormat(".#");
                double dx = Double.parseDouble(df.format(x));
                Distance = "Distance: " + dx + " KM";
                final TextView UserName = (TextView) v.findViewById(R.id.name);
                final TextView UserEmail = (TextView) v.findViewById(R.id.email);
                final TextView UserDistance = (TextView) v.findViewById(R.id.distance);
                UserName.setText(Name);
                UserEmail.setText(Email);
                UserDistance.setText(Distance);
                return v;
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.d("marker title", marker.getTitle() + "");
                Marker_Title = marker.getTitle();
                follow_child_status = 1;
                new connectAsyncTask(makeURL(marker.getPosition().latitude, marker.getPosition().longitude, MyLocationLatlng.latitude, MyLocationLatlng.longitude)).execute();
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String[] result = marker.getTitle().split(",");
                if (result[0].equalsIgnoreCase("1")) {
                    return false;
                } else {
                    marker.showInfoWindow();
                }
                return false;
            }
        });
    }
    @Override
    public void onRoutingFailure() {
        pd.Dismiss();
    }
    @Override
    public void onRoutingStart() {

    }
    @Override
    public void onRoutingSuccess(PolylineOptions polylineOptions, Route route) {
        PolylineOptions polyoptions = new PolylineOptions();
        polyoptions.color(Color.parseColor("#6CC417"));
        polyoptions.width(28);
        polyoptions.addAll(polylineOptions.getPoints());
        mMap.addPolyline(polyoptions);
        pd.Dismiss();
    }
    @Override
    public void onRoutingCancelled() {
        ErrorToast("Errrooooooor cancellll");
        pd.Dismiss();
    }
    private class connectAsyncTask extends AsyncTask<Void, Void, String> {

        String url;

        connectAsyncTask(String urlPass) {
            url = urlPass;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd.Show();
            mMap.clear();
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONParse jParser = new JSONParse();
            String json = jParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                drawPath(result);
            }
        }
    }
    public String makeURL(double sourcelat, double sourcelog, double destlat, double destlog) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        return urlString.toString();
    }

    public void drawPath(String result) {

        try {
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            list = decodePoly(encodedString);
            Routing routing = new Routing.Builder()
                    .avoid(AbstractRouting.AvoidKind.FERRIES)
                    .travelMode(Routing.TravelMode.DRIVING)
                    .withListener(HomeActivity.this)
                    .waypoints(list.get(list.size() - 1), list.get(0))
                    .build();
            routing.execute();
            mMap.addMarker(new MarkerOptions()
                    .position(list.get(0))
                    .title(Marker_Title)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.person_marker)));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(list.get(list.size() - 1), 15);
            mMap.animateCamera(cameraUpdate);
        } catch (JSONException e) {
        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    public void MapIntigration() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, MIN_DISTANCE, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MyLocationLatlng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.animateCamera(cameraUpdate);
//       new GetChildDetail().execute();
        UploadMarker();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menue_btn:
                if (!dlayout.isDrawerOpen(Gravity.START)) {
                    dlayout.openDrawer(Gravity.START);
                } else {
                    dlayout.closeDrawer(Gravity.START);
                }
                break;
            case R.id.refresh_data:
                follow_child_status = 0;
                mMap.clear();
                pd.Show();
                chilDatas.clear();
                UploadMarker();
                break;
            case R.id.logout_btn:
                dlayout.closeDrawer(Gravity.START);
                pd.Show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pd.Dismiss();
                        setDefaultBoolean("rb",false);
                        GoTo(HomeActivity.this, LoginActivity.class);
                        finish();
                    }
                }, 3000);
                break;
        }
    }

    public void myLocation(View view) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(MyLocationLatlng, 13);
        mMap.animateCamera(cameraUpdate);
    }

    public void UploadMarker() {
        String url = "http://wasifapps.com/getAllChild.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Log.d("marker_ressponseeeeeeeeeeee", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("response", jsonObject.getString("status"));
                            if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("user_data");
                                for (int x = 0; x < jsonArray.length(); x++) {
                                    JSONObject object = jsonArray.getJSONObject(x);
                                    if (object.getString("lat").equalsIgnoreCase("") || object.getString("lat").length() == 0
                                            || object.getString("longi").equalsIgnoreCase("") || object.getString("longi").length() == 0) {
                                    } else {
                                        setDefaultString("latitude"+x , object.getString("lat"));
                                        setDefaultString("longitude"+x , object.getString("longi"));
                                        mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(object.getDouble("lat"), object.getDouble("longi")))
                                                .title(object.getString("f_name") + " " + object.getString("l_name") + "," + object.getString("email") + "," + CalculationByDistance(MyLocationLatlng, new LatLng(object.getDouble("lat"), object.getDouble("longi"))))
                                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.person_marker)));
                                        Log.d("Latitude", object.getDouble("lat") + "");
                                        Log.d("Longitude", object.getDouble("longi") + "");
                                        chilDatas.add(new ChilData(object.getString("f_name") + " " + object.getString("l_name"),
                                                object.getString("email"),
                                                object.getInt("id"),
                                                new LatLng(object.getDouble("lat"), object.getDouble("longi"))
                                        ));
                                    }
                                }
                                Child_drawer_list.setAdapter(new DrawerListAdapter(HomeActivity.this, chilDatas));
                                pd.Dismiss();
                                RecallMethod();
                            } else {
                                pd.Dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error + "");
                pd.Dismiss();
                RetryDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parm = new HashMap<>();
                parm.put("pid", PID + "");
                return parm;
            }
        };
        Volley.newRequestQueue(HomeActivity.this).add(stringRequest);
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    public String GetLocationName(double latitude, double longitude) {
        String cityName = "", stateName = "", countryName = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            cityName = addresses.get(0).getAddressLine(0);
            stateName = addresses.get(0).getAddressLine(1);
            countryName = addresses.get(0).getAddressLine(2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stateName + "," + cityName + "," + countryName;
    }

    public boolean isEmailRegister(String child_email) {
        boolean st = true;
        if (child_db_list.size() > 0) {
            for (int x = 0; x < child_db_list.size(); x++) {
                if (child_db_list.get(x).getEmail().equalsIgnoreCase(child_email)) {
                    st = false;
                }
            }
        }


        return st;
    }

    public void RecallMethod() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (st.hasGPSConnection(HomeActivity.this)) {
                    new GetChildDetailRecall().execute();
                }
            }
        }, 30000);
    }

    private class GetChildDetailRecall extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Object doInBackground(Object... params) {
            if (follow_child_status == 0) {
                Log.d("recal method" , "ok");
                String url = "http://wasifapps.com/getAllChild.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            public void onResponse(String response) {
                                Log.d("marker_ressponseeeeeeeeeeee", response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    Log.d("response", jsonObject.getString("status"));
                                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                                        Log.d("recall update marker ", "success");
                                        JSONArray jsonArray = jsonObject.getJSONArray("user_data");
                                        for (int x = 0; x < jsonArray.length(); x++) {
                                            JSONObject object = jsonArray.getJSONObject(x);
                                            if (object.getString("lat").equalsIgnoreCase("") || object.getString("lat").length() == 0
                                                    || object.getString("longi").equalsIgnoreCase("") || object.getString("longi").length() == 0) {

                                            } else {
                                                     if(x==0){
                                                         mMap.clear();
                                                     }
                                                     setDefaultString("latitude"+x , object.getString("lat"));
                                                     setDefaultString("longitude"+x , object.getString("longi"));
                                                     mMap.addMarker(new MarkerOptions()
                                                             .position(new LatLng(object.getDouble("lat"), object.getDouble("longi")))
                                                             .title(object.getString("f_name") + " " + object.getString("l_name") + "," + object.getString("email") + "," + CalculationByDistance(MyLocationLatlng, new LatLng(object.getDouble("lat"), object.getDouble("longi"))))
                                                             .icon(BitmapDescriptorFactory.fromResource(R.mipmap.person_marker)));
                                                     Log.d("Latitude", object.getDouble("lat") + "");
                                                     Log.d("Longitude", object.getDouble("longi") + "");
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error + "");
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> parm = new HashMap<>();
                        parm.put("pid", PID + "");
                        return parm;
                    }
                };
                Volley.newRequestQueue(HomeActivity.this).add(stringRequest);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            RecallMethod();
        }
    }

    public void RetryDialog() {
        LayoutInflater factory = LayoutInflater.from(HomeActivity.this);
        final View view = factory.inflate(R.layout.try_again_alert_dialog, null);
        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        final Button CANCEL = (Button) view.findViewById(R.id.cancel_button);
        final Button OK = (Button) view.findViewById(R.id.ok_button);
        CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                pd.Show();
                UploadMarker();
            }
        });
        dialog.show();
    }
    public void AlertDialog(final int id, String msg_body){
        LayoutInflater factory = LayoutInflater.from(HomeActivity.this);
        final View view = factory.inflate(R.layout.alert_notification, null);
        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        final Button OK = (Button) view.findViewById(R.id.ok_button);
        final TextView msg = (TextView) view.findViewById(R.id.body);
        msg.setText(msg_body);
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if(id==0){
                    finish();
                }else {
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                    finish();
                }
            }
        });
        dialog.show();
    }
}
