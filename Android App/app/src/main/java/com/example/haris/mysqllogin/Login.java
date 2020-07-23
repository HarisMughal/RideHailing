package com.example.haris.mysqllogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
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

public class Login extends AppCompatActivity implements View.OnClickListener{

    private EditText email;
    private EditText password;
    private Button login;
    private TextView noMember;
    private boolean doubleBackToExitPressedOnce = false;
    private ProgressDialog progressDialog;
    private User user;
    private Switch switchcSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            if(SharedPrefManager.getInstance(this).isDriver()){
                finish();
                startActivity(new Intent(this, driverActivity.class));
                return;
            }else{
                finish();
                startActivity(new Intent(this, MapsActivity.class));
                return;
            }
        }
        setupUI();
        switchcSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    switchcSelector.setText("Driver");
                } else {
                    //do stuff when Switch if OFF
                    switchcSelector.setText("User");
                }
            }
        });

    }
    public void setupUI(){
        email = (EditText) findViewById(R.id.emailLogin);
        password = (EditText) findViewById(R.id.passwordLogin);
        login = (Button) findViewById(R.id.login);
        noMember = (TextView) findViewById(R.id.notmemberEditText);
        login.setOnClickListener(this);
        noMember.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Authenticating");
        progressDialog.setMessage("Please Wait...");
        switchcSelector = (Switch) findViewById(R.id.switchSelector);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.login) {
            if(email.getText().toString().isEmpty()){
                Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
            }
            if (! (password.getText().toString().length() < 8)) {
                progressDialog.show();
                user = new User();
                user.setEmail(email.getText().toString().trim());
                user.setPassword(password.getText().toString().trim());
                StringRequest request = new StringRequest(Request.Method.POST, Constants.URL2, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {
                                //TODO: fix this
                                int id = jsonObject.getInt("id");
                                String username = jsonObject.getString("username");
                                String email = jsonObject.getString("email");
                                Toast.makeText(getApplicationContext(), jsonObject.getString("response"),
                                        Toast.LENGTH_LONG).show();
                                if(!switchcSelector.isChecked()){
                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(id, username, email,false);
                                    startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                                }else{
                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(id, username, email,true);
                                    startActivity(new Intent(getApplicationContext(), driverActivity.class));
                                }
                                finish();
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
                        mMap.put("password", user.getPassword());
                        if(switchcSelector.isChecked()){
                            mMap.put("tabel", "1");
                        }else{
                            mMap.put("tabel", "0");
                        }
                        return mMap;
                    }
                };
                RequestHandler.getInstance(this).addToRequestQueue(request);
            }else{
                Toast.makeText(this, "Password must be of length 8", Toast.LENGTH_SHORT).show();
            }
        }else if (id == R.id.notmemberEditText) {
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}
