package com.example.haris.mysqllogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetUP extends AppCompatActivity {


    private EditText  ip ;
    private Button setUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        ip = (EditText) findViewById(R.id.ip);
        setUp = (Button) findViewById(R.id.setup);

        setUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipString = ip.getText().toString().trim();
                if(!ipString.isEmpty()) {
                    Constants.setURL(ip.getText().toString());
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(SetUP.this, "Enter IP", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}
