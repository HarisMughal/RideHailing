package com.example.haris.mysqllogin;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class driverMaps extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Intent intent;
    private RequestData requestData;
    private User userData;
    private LocationManager locationManager;
    private Button onAccept;

    private LocationListener locationListner;
    private Location lastKnownLocation;
    boolean endRide = false;
    int tabeleId;
    boolean requestAccepted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_maps);
        intent = getIntent();
        onAccept = (Button) findViewById(R.id.buttonAccept);

        userData = new User();
        userData.setId(SharedPrefManager.getInstance(this).getUserId());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDriver);
        mapFragment.getMapAsync(this);

        requestData = (RequestData) getIntent().getSerializableExtra("userData");

        RelativeLayout mapLayout = (RelativeLayout)findViewById(R.id.mapDriverRelative);
//        mapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                LatLng driverLocation = new LatLng(intent.getDoubleExtra("driverLatitude", 0), intent.getDoubleExtra("driverLongitude", 0));
//
//                LatLng requestLocation = new LatLng(requestData.getLatitude(), requestData.getLongitude());
//
//                ArrayList<Marker> markers = new ArrayList<>();
//                if (mMap != null) {
//                    markers.add(mMap.addMarker(new MarkerOptions().position(requestLocation).title("Request Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));
//
//                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                    for (Marker marker : markers) {
//                        builder.include(marker.getPosition());
//                    }
//                    LatLngBounds bounds = builder.build();
//
//
//                    int padding = 60; // offset from edges of the map in pixels
//                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//
//                    mMap.animateCamera(cu);
//
//                }
//            }
//        });

        final Handler ha=new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function
                updateLocation();
                ha.postDelayed(this, 5000);
            }
        }, 5000);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        LatLng driverLocation = new LatLng(intent.getDoubleExtra("driverLatitude", 0), intent.getDoubleExtra("driverLongitude", 0));
        mMap.addMarker(new MarkerOptions().position(driverLocation).title("Your Location"));
        LatLng requestLocation = new LatLng(requestData.getLatitude(), requestData.getLongitude());
        mMap.addMarker(new MarkerOptions().position(requestLocation)
                .position(requestLocation).title("Request Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        mMap = googleMap;
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListner = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                onUpdateLocation(location);
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
        };
        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListner);
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);// can be used to get last known loaction
            onUpdateLocation(lastKnownLocation);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListner);
            else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListner);
                 lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);// can be used to get last known loaction
                if(lastKnownLocation != null) {
                    onUpdateLocation(lastKnownLocation);
                }
            }

        }


    }
    public void onUpdateLocation(Location location){
        mMap.clear();
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
        LatLng requestLocation = new LatLng(requestData.getLatitude(), requestData.getLongitude());
        mMap.addMarker(new MarkerOptions().position(requestLocation)
                .position(requestLocation).title("Request Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        if(requestAccepted) {
            if (userLocation.latitude - requestLocation.latitude > -1 && userLocation.latitude - requestLocation.latitude < 1) {
                if (userLocation.longitude - requestLocation.longitude > -1 && userLocation.longitude - requestLocation.longitude < 1) {
                    onAccept.setText("End Ride");
                    endRide = true;
                }
            }
        }

//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,5));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,10));
//          mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
//        Geocoder geooder = new Geocoder(getApplicationContext(), Locale.getDefault());
//        try {
//            List<Address> addressList = geooder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
//            if(addressList != null && addressList.size() > 0){
//                String fullAddress = "";
////                        Toast.makeText(getApplicationContext(), addressList.get(0).getAddressLine(0),
////                                Toast.LENGTH_LONG);
//                if(addressList.get(0).getAddressLine(0) != null){
//                    fullAddress += addressList.get(0).getAddressLine(0)+" ";
//                }
////                        if(addressList.get(0).getSubAdminArea() != null){
////                            fullAddress += addressList.get(0).getSubAdminArea()+" ";
////                        }
//                Toast.makeText(getApplicationContext(), fullAddress,
//                        Toast.LENGTH_LONG).show();
//            }else{
//                Toast.makeText(getApplicationContext(), "Could not find address",
//                        Toast.LENGTH_LONG).show();
//                // Log.d("address","Could not find address");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    public void onAccept(View v){
        if(endRide){

            StringRequest request = new StringRequest(Request.Method.POST, Constants.URLendRide, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean("error")) {
                            requestAccepted= false;
                            endRide = false;
                            startActivity(new Intent(getApplicationContext(), driverActivity.class));
                            finish();
                        }
                        Toast.makeText(getApplicationContext(), jsonObject.getString("response"),
                                Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> mMap = new HashMap<>();
                    mMap.put("id", String.valueOf(tabeleId));
                    mMap.put("driverid",String.valueOf(userData.getId()) );
                    return mMap;
                }
            };
            RequestHandler.getInstance(this).addToRequestQueue(request);
            return;
        }
        final StringRequest request = new StringRequest(Request.Method.POST, Constants.URLacceptRequest , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), "Request Accepted",
                                Toast.LENGTH_LONG).show();
                        tabeleId = jsonObject.getInt("id");
                        requestAccepted= true;
                    } else {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("response"),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> mMap = new HashMap<>();
                mMap.put("userId", String.valueOf(requestData.getId() ) );
                mMap.put("driverId", String.valueOf(userData.getId()));
                return mMap;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
    }
    public void updateLocation(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);


        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListner);

            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastKnownLocation != null) {

                // TODO: update driver Location
                final StringRequest request = new StringRequest(Request.Method.POST, Constants.URLupdateLocation , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {


                            } else {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("response"),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> mMap = new HashMap<>();
                        mMap.put("driverId", String.valueOf(SharedPrefManager.getInstance(getApplicationContext()).getUserId()) );
                        mMap.put("latitude", String.valueOf(lastKnownLocation.getLatitude()));
                        mMap.put("longitude", String.valueOf(lastKnownLocation.getLongitude()));
                        return mMap;
                    }
                };
                RequestHandler.getInstance(this).addToRequestQueue(request);


            }


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListner);
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                }

            }


        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.logoutItem:
                SharedPrefManager.getInstance(this).logout();//loged out
                finish();
                startActivity(new Intent(this, Login.class));
                break;
            case R.id.profile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.applicationmenu,menu);


        return true;
    }

}
