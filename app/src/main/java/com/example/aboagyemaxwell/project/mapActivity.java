package com.example.aboagyemaxwell.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.aboagyemaxwell.project.alertsActivity.EXTRA_FULLNAME;
import static com.example.aboagyemaxwell.project.alertsActivity.EXTRA_GENDER;
import static com.example.aboagyemaxwell.project.alertsActivity.EXTRA_MESSAGE;
import static com.example.aboagyemaxwell.project.alertsActivity.EXTRA_REF_NUM;
import static com.example.aboagyemaxwell.project.alertsActivity.EXTRA_USERNAME;
import static com.example.aboagyemaxwell.project.alertsActivity.EXTRA_USER_UID;

public class mapActivity extends FragmentActivity implements OnMapReadyCallback {

    String username,full_name,ref_number,gender,messaege,user_uid;
    DatabaseReference reference;
    public final ArrayList<String> list = new ArrayList<>();
    public double latitude,longitude;
    GoogleMap map;

    double lat,longg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        Intent intent = getIntent();
        username = intent.getStringExtra(EXTRA_USERNAME);
        full_name = intent.getStringExtra(EXTRA_FULLNAME);
        ref_number = intent.getStringExtra(EXTRA_REF_NUM);
        gender = intent.getStringExtra(EXTRA_GENDER);
        messaege = intent.getStringExtra(EXTRA_MESSAGE);
        user_uid = intent.getStringExtra(EXTRA_USER_UID);



        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(mapActivity.this);
    }


        @Override
        public void onMapReady (final GoogleMap googleMap){



            reference = FirebaseDatabase.getInstance().getReference().child("location").child(user_uid);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        list.add(snapshot.getValue().toString());
                    }

                    latitude = Double.parseDouble(list.get(0));
                    longitude = Double.parseDouble(list.get(1));

                    LatLng latLng = new LatLng(latitude, longitude);
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(full_name+" triggered alert from here");
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
                    googleMap.addMarker(markerOptions);


//                    map = googleMap;
//                    LatLng latLng = new LatLng(latitude, longitude);
//                    map.addMarker(new MarkerOptions().position(latLng).title(full_name + " triggered the alert"));
//                    map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                }




                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),"Opss something went wrong",Toast.LENGTH_SHORT).show();
                }
            });

        }

}
