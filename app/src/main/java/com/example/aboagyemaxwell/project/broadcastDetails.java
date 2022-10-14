package com.example.aboagyemaxwell.project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class broadcastDetails extends AppCompatActivity {
    private String broadKey = null;
    private DatabaseReference mDatabase;
    private TextView broadTitle, broadMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_details);


//        String broad_key = getIntent().getExtras().getString("broad_Id");

//        mDatabase = FirebaseDatabase.getInstance().getReference().child("Suspects");

//        broadTitle = findViewById(R.id.broa);
//        susImageDetail = findViewById(R.id.susDetailImg);

    }
}
