package com.example.haris.mysqllogin;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback , View.OnClickListener{

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListner;
    private Button bookRide;
    private ProgressDialog progressDialog;
    private StringRequest request;
    private boolean requestStatus;
    private  Location last;
    private User userData;
    private boolean driverStatus;
    private LatLng driverLatLng;
    private PromoDialog promoDialog;
    public  static String promo="1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        setupUI();

        final Handler ha=new Handler();
        ha.postDelayed(new Runnable()
        {

            @Override
            public void run() {
                //call function
                if(requestStatus) {
                    checkRide();
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);


                    } else {

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListner);

                        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (lastKnownLocation != null) {

                            onUpdateLocation(lastKnownLocation);

                        }


                    }
                    if (driverStatus){
                        isRideEnd();
                    }

                }
                ha.postDelayed(this, 5000);
            }
        }, 5000);


    }
    public void setupUI(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Requesting");
        progressDialog.setMessage("Now Booking...");
        mapFragment.getMapAsync(this);
        bookRide = (Button) findViewById(R.id.bookRide);
        bookRide.setOnClickListener(this);
        userData = new User();
        userData.setId(SharedPrefManager.getInstance(this).getUserId());
        requestStatus = false;
        driverStatus = false;
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
            Location last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);// can be used to get last known loaction
            onUpdateLocation(last);
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
                Location last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);// can be used to get last known loaction
                if(last != null) {
                    onUpdateLocation(last);
                }
            }

        }

        // Add a marker in Sydney and move the camera
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListner);
                last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);// can be used to get last known loaction


            }
        }
    }

    public void onUpdateLocation(Location location){
        mMap.clear();
        if(driverStatus){
            mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Your Ride"));
        }
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location").
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
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

    @Override
    public void onClick(View v) {
        int id  = v.getId();
        if(id == R.id.bookRide){
            if(!requestStatus) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListner);
                    last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);// can be used to get last known loaction
                    if (last != null) {
                        // TODO: request
                        request = new StringRequest(Request.Method.POST, Constants.URLRequestRide, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    Toast.makeText(getApplicationContext() , jsonObject.getString("response"),
                                            Toast.LENGTH_LONG).show();
                                    if(!jsonObject.getBoolean("error")) {
                                        bookRide.setText(R.string.cancle);
                                        requestStatus = true;
                                        startActivity(new Intent(getApplicationContext(), PromoActivity.class));
                                        //addPromo();

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.hide();
                                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();

                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> mMap = new HashMap<>();
                                mMap.put("id",String.valueOf(userData.getId()) );
                                mMap.put("lat",String.valueOf(last.getLatitude()));
                                mMap.put("lng", String.valueOf(last.getLongitude()));
                                return mMap;
                            }
                        };
                        RequestHandler.getInstance(this).addToRequestQueue(request);
                    } else {
                        Toast.makeText(this, "Could not find your location try again", Toast.LENGTH_LONG);
                    }


                }
            }else if(requestStatus && !driverStatus) {
                        // TODO: request

                        request = new StringRequest(Request.Method.POST, Constants.URLCancleRide, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("response"),
                                            Toast.LENGTH_LONG).show();
                                    if (!jsonObject.getBoolean("error")) {
                                        bookRide.setText(R.string.book);
                                        requestStatus = false;
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.hide();
                                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> mMap = new HashMap<>();
                                mMap.put("id", String.valueOf(userData.getId()));
                                return mMap;
                            }
                        };
                        RequestHandler.getInstance(this).addToRequestQueue(request);

                }
            }
        }
        public void checkRide(){
            request = new StringRequest(Request.Method.POST, Constants.URLfindDriver, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(!jsonObject.getBoolean("error")) {
                            if(jsonObject.getBoolean("book")){
                                driverStatus = true;
                                double lat = jsonObject.getDouble("lat");
                                double lng = jsonObject.getDouble("lng");
                                driverLatLng = new LatLng(lat,lng);
                            }else{
                                driverStatus = false;
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.hide();
                    Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> mMap = new HashMap<>();
                    mMap.put("userId",String.valueOf(userData.getId()) );
                    return mMap;
                }
            };
            RequestHandler.getInstance(this).addToRequestQueue(request);
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
    public void isRideEnd(){
       StringRequest request = new StringRequest(Request.Method.POST, Constants.URLgetFare, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        int fare = jsonObject.getInt("fare");
                        Toast.makeText(getApplicationContext(),"Your fare is "+fare,Toast.LENGTH_LONG).show();
                        requestStatus = false;
                        bookRide.setText(R.string.book);
                        driverStatus = false;
                        Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                        intent.putExtra("fare", fare);
                        startActivity(intent);
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
                progressDialog.hide();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> mMap = new HashMap<>();
                mMap.put("id", String.valueOf(userData.getId()));
                    mMap.put("promo", promo);
                return mMap;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(request);

    }


//    public void addPromo(){
//        promoDialog = new PromoDialog();
//        promoDialog.show(getSupportFragmentManager(),"promo");
//    }
}

