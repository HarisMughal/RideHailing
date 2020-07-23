package com.example.haris.mysqllogin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class driverActivity extends AppCompatActivity  {

    ListView requestListView;
    ArrayList<String> requests = new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    EditText limit;
    Button setLimit;
    int intLimit = 100000000;


    ArrayList<RequestData> userData = new ArrayList<RequestData>();

    LocationManager locationManager;
    LocationListener locationListener;
    RequestData[] requestedData;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    updateListView(lastKnownLocation);

                }

            }


        }

    }

    public  void setupUI(){
        requestListView = (ListView) findViewById(R.id.requestListView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, requests);
        limit = (EditText) findViewById(R.id.limit);
        setLimit =(Button) findViewById(R.id.setLimit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        setupUI();
        setLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(limit.getText().toString().isEmpty()){
                    intLimit = 100000000;
                }else{
                    intLimit = Integer.parseInt(limit.getText().toString());
                }
            }
        });

        setTitle("Nearby Requests");




        requests.clear();

        requests.add("Getting nearby requests...");

        requestListView.setAdapter(arrayAdapter);

        requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(driverActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (userData.size() > i && lastKnownLocation != null) {

                        Intent intent = new Intent(getApplicationContext(), driverMaps.class);
                        intent.putExtra("userData", userData.get(i));
                        intent.putExtra("driverLatitude", lastKnownLocation.getLatitude());
                        intent.putExtra("driverLongitude", lastKnownLocation.getLongitude());

                        startActivity(intent);


                    }

                }

            }
        });



        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                updateListView(location);

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
        };
        final Handler ha=new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function
                onRef();
                ha.postDelayed(this, 5000);
            }
        }, 5000);


    }
    public void updateListView(final Location location) {
        if (location != null) {
            //TODO: fetch user locxations
            if(location != null){

                final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.URLgetRequest, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            requests.clear();
                            JSONArray jsonArray = response.getJSONArray("response");
                            if (jsonArray.length() > 0) {
                                requestedData = new RequestData[10];
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    requestedData[i] = new RequestData();
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    requestedData[i].setId(jsonObject.getInt("id"));
                                    requestedData[i].setLatitude(jsonObject.getDouble("lat"));
                                    requestedData[i].setLongitude(jsonObject.getDouble("lng"));
                                    requestedData[i].setDistance(location.getLatitude(), location.getLongitude());
                                    if( requestedData[i].getDistance() < intLimit ) {
                                        requests.add(String.format("%.3f", requestedData[i].getDistance()) + " KM");
                                    }
                                    userData.add(i,requestedData[i]);
                                }
                            }else{
                                requests.add("No request at the moment");
                            }
                            arrayAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
                RequestHandler.getInstance(this).addToRequestQueue(request);
            }else{
                Toast.makeText(this,"Unable to find your location",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onRef(){


            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);


            } else {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (lastKnownLocation != null) {

                    updateListView(lastKnownLocation);

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

