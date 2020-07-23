package com.example.haris.mysqllogin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText cardNumber, namePay, ammountPay;
    private Button pay;
    StringRequest request;
    private ProgressDialog progressDialog;
    TextView fareInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setupUi();
    }

    public void setupUi(){
        cardNumber = (EditText) findViewById(R.id.cardNumber);
        namePay= (EditText) findViewById(R.id.namePay);
        ammountPay = (EditText) findViewById(R.id.ammountPay);
        pay = (Button) findViewById(R.id.pay);
        pay.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Completing transaction");
        progressDialog.setMessage("Please wait...");
        fareInfo = (TextView) findViewById(R.id.fareInfo);
        Intent intent = getIntent();
        fareInfo.setText(String.valueOf(intent.getIntExtra("fare",0)));

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.pay){
            final String cardString = cardNumber.getText().toString();
            if(cardString.isEmpty()) {
                Toast.makeText(this, "Required fields are missing", Toast.LENGTH_LONG).show();
                return;
            }
            final String namePayString = namePay.getText().toString();
            if(namePayString.isEmpty()) {
                Toast.makeText(this, "Required fields are missing", Toast.LENGTH_LONG).show();
                return;
            }
            final String ammountPayString = ammountPay.getText().toString();
            if(ammountPayString.isEmpty()) {
                Toast.makeText(this, "Required fields are missing", Toast.LENGTH_LONG).show();
                return;
            }
            request = new StringRequest(Request.Method.POST, Constants.URLpayment, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();

                        Toast.makeText(getApplicationContext(),response ,
                                Toast.LENGTH_LONG).show();

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
                    mMap.put("amount", ammountPayString);
                    mMap.put("cardnumber", cardString);
                    mMap.put("name", namePayString);
                    return mMap;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(
                    5000,
                    5,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestHandler.getInstance(this).addToRequestQueue(request);

        }
    }
}
