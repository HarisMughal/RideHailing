package com.example.haris.mysqllogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class PromoActivity extends AppCompatActivity implements View.OnClickListener {

    Button enter;
    Button submit;
    EditText enterPromo;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo);
        setupUI();
    }
    public void setupUI(){
        enter = (Button) findViewById(R.id.enterPromoButton);
        submit = (Button) findViewById(R.id.cancelPromoButton);
        enterPromo = (EditText) findViewById(R.id.enterPromoEdit);
        progressDialog = new ProgressDialog(this);
        enter.setOnClickListener(this);
        submit.setOnClickListener(this);
        progressDialog.setTitle("Verifying");
        progressDialog.setMessage("Please wait");
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.enterPromoButton)
        {
            final String promo = enterPromo.getText().toString();
            if(promo.isEmpty()){
                Toast.makeText(this, "Enter Promo", Toast.LENGTH_SHORT).show();
                return;
            }
            StringRequest request = new StringRequest(Request.Method.POST, Constants.URLchcekPromo, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean("error")) {
                            //TODO: fix this
                            Toast.makeText(getApplicationContext(), jsonObject.getString("response"),
                                    Toast.LENGTH_LONG).show();
                            if(jsonObject.getBoolean("promo")){
                                MapsActivity.promo = promo;
                                finish();
                            }

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
                    mMap.put("promo", promo);
                    return mMap;
                }
            };
            RequestHandler.getInstance(this).addToRequestQueue(request);
        }
        else if(i == R.id.cancelPromoButton)
        {
            finish();
        }
    }
}
