package com.example.haris.mysqllogin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText email;
    private EditText username;
    private EditText password;
    private EditText repassword;
    private Button register;
    private StringRequest request;
    private ProgressDialog progressDialog;
    private TextView member;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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



    }

    public void setupUI(){
        email = (EditText) findViewById(R.id.email);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        repassword = (EditText) findViewById(R.id.repassword);
        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(this);
        member = (TextView) findViewById(R.id.memberEditText);
        member.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Registering");
        progressDialog.setMessage("Now Registering..");
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.register)
        {
            if (register.getText().toString().length() < 8) {
                Toast.makeText(this, "Password must be of length 8", Toast.LENGTH_SHORT).show();
            } else if (!password.getText().toString().equals(repassword.getText().toString())) {
                Toast.makeText(this, "Password does not matches", Toast.LENGTH_SHORT).show();
            }else if(email.getText().toString().isEmpty()){
                Toast.makeText(this, "Required fields are missing", Toast.LENGTH_SHORT).show();
            }
            else if(username.getText().toString().isEmpty()){
                Toast.makeText(this, "Required fields are missing", Toast.LENGTH_SHORT).show();
            }
            else {
                progressDialog.show();
                user = new User();
                user.setEmail(email.getText().toString().trim());
                user.setPassword(password.getText().toString().trim());
                user.setUsername(username.getText().toString().trim());
                request = new StringRequest(Request.Method.POST, Constants.URL1, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("response"),
                                    Toast.LENGTH_LONG).show();
                            if (!jsonObject.getBoolean("error")) {
                                gotoLogIn();
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
                        mMap.put("username", user.getUsername());
                        mMap.put("email", user.getEmail());
                        mMap.put("password", user.getPassword());
                        return mMap;
                    }
                };
                RequestHandler.getInstance(this).addToRequestQueue(request);


            }
        }else if (id == R.id.memberEditText)
        {
                gotoLogIn();
        }

    }

    public void gotoLogIn(){
        Intent intent = new Intent(this,Login.class);
        startActivity(intent);
    }
}
