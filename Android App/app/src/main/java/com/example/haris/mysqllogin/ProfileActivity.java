package com.example.haris.mysqllogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    ListView profileListView;
    ArrayList<String> profileInfo = new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupUI();
        profileInfo.clear();
        profileInfo.add("Profile");
        profileInfo.add("History");
        profileListView.setAdapter(arrayAdapter);
        profileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(i == 0){
                    startActivity(new Intent(getApplicationContext(), UserProfile.class));

                }

            }
        });

    }
    public void setupUI(){
        profileListView = (ListView) findViewById(R.id.profileListView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, profileInfo);
    }
}
