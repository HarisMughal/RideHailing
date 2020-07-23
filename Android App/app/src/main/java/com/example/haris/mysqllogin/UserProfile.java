package com.example.haris.mysqllogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity implements View.OnClickListener {
    EditText name ,email,city,cnic,number,pkg;
    RadioGroup gender;
    Button submit;
    private ProgressDialog progressDialog;
    User user;
    StringRequest request;
    RadioButton radio1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setupUI();
        progressDialog.show();
        getData();
    }

    public void setupUI(){
        name = (EditText) findViewById(R.id.nameProfile);
        email = (EditText) findViewById(R.id.emailProfile);
        city = (EditText) findViewById(R.id.cityProfile);
        cnic = (EditText) findViewById(R.id.cnicProfile);
        number = (EditText) findViewById(R.id.numberProfile);
        pkg = (EditText) findViewById(R.id.packageProfile);
        gender = (RadioGroup) findViewById(R.id.radioGroup);
        submit = (Button) findViewById(R.id.Submit);
        user = new User();
        user.setEmail(SharedPrefManager.getInstance(this).getUserEmail());
        user.setDriver(SharedPrefManager.getInstance(getApplicationContext()).isDriver());
        radio1 = (RadioButton) findViewById(R.id.maleProfile);
        progressDialog = new ProgressDialog(this);
        if(user.isDriver()){
            submit.setVisibility(View.INVISIBLE);
            pkg.setVisibility(View.INVISIBLE);
            name.setEnabled(false);
            cnic.setEnabled(false);
            number.setEnabled(false);
            email.setEnabled(false);
            city.setEnabled(false);
            pkg.setEnabled(false);

        }else{
            submit.setOnClickListener(this);
            progressDialog.setTitle("Fetching Data");
            progressDialog.setMessage("Please Wait...");

        }
    }
    public void getData(){
        request = new StringRequest(Request.Method.POST, Constants.URLgetProfile, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        //TODO: fix this
                        String value = new String();
                        value = jsonObject.getString("username");
                        if( !value.equals("null"))
                            name.setText(value);
                        value = jsonObject.getString("email");
                        if( !value.equals("null"))
                            email.setText(value);
                        value = jsonObject.getString("cnic");
                        if( !value.equals("null"))
                            cnic.setText(value);
                        if(!user.isDriver()) {
                            value = jsonObject.getString("Package_No");
                            if (!value.equals("null"))
                                pkg.setText(value);
                        }
                        value = jsonObject.getString("city");
                        if( !value.equals("null"))
                            city.setText(value);
                        value = jsonObject.getString("Gender");
                        if( !value.equals("null")) {
                            if (value.equals("Male"))
                                gender.check(R.id.maleProfile);
                            else{
                                gender.check(R.id.femaleProfile);
                            }
                        }
                        value = jsonObject.getString("number");
                        if( !value.equals("null"))
                            number.setText(value);


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
                progressDialog.hide();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> mMap = new HashMap<>();
                mMap.put("email", user.getEmail());
                if(user.isDriver()){
                    mMap.put("tabel", "1");
                }else{
                    mMap.put("tabel", "0");
                }
                return mMap;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
    }

    @Override
    public void onClick(View v) {
        final String nameString,emailString,cincString,numberString,cityString, pkgString;
        nameString = name.getText().toString();
        if(nameString.isEmpty()){
            Toast.makeText(this,"Required Fields are missing",Toast.LENGTH_LONG).show();
            return;
        }
        emailString = email.getText().toString();
        if(emailString.isEmpty()){
            Toast.makeText(this,"Required Fields are missing",Toast.LENGTH_LONG).show();
            return;
        }
        cincString = cnic.getText().toString();
        if(cincString.isEmpty()){
            Toast.makeText(this,"Required Fields are missing",Toast.LENGTH_LONG).show();
            return;
        }
        pkgString = pkg.getText().toString();
        if(pkg.getText().toString().isEmpty()){
            Toast.makeText(this,"Required Fields are missing",Toast.LENGTH_LONG).show();
            return;
        }
        numberString = number.getText().toString();
        if(numberString.isEmpty()){
            Toast.makeText(this,"Required Fields are missing",Toast.LENGTH_LONG).show();
            return;
        }
        cityString = city.getText().toString();
        if(cityString.isEmpty()){
            Toast.makeText(this,"Required Fields are missing",Toast.LENGTH_LONG).show();
            return;
        }


        request = new StringRequest(Request.Method.POST, Constants.URLsubmitProfile, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
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
                mMap.put("username",nameString);
                mMap.put("email", emailString);
                mMap.put("cnic", cincString);
                mMap.put("pkg", pkgString);
                mMap.put("number", numberString);
                mMap.put("city", cityString);
                if(radio1.isChecked()){
                    mMap.put("gender", "Male");
                }
                else{
                    mMap.put("gender", "Female");
                }
                if(user.isDriver()){
                    mMap.put("tabel", "1");
                }else{
                    mMap.put("tabel", "0");
                }


                return mMap;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(request);

    }
}
