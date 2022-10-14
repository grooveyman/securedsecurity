package com.example.aboagyemaxwell.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;

public class alertsActivity extends AppCompatActivity implements MyAdapter.onItemClickListener{
    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_FULLNAME = "full_name";
    public static final String EXTRA_REF_NUM = "ref_number";
    public static final String EXTRA_GENDER = "gender";
    public static final String EXTRA_USER_UID = "user_uid";
    public static  final String EXTRA_MESSAGE = "message";
    private static final long REFRESH_DELAY = 2000;

    DatabaseReference reference;
    RecyclerView recyclerView;
    ArrayList<message> list;
    MyAdapter adapter;

    private PullToRefreshView mPullRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);
        mPullRefresh = findViewById(R.id.pull_to_refresha);

        getSupportActionBar().setTitle("Alerts");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra("category") && getIntent().getStringExtra("category").equals("alerts")){
            Intent intent = new Intent(alertsActivity.this, alertsActivity.class);
            intent.putExtra("category",getIntent().getStringExtra("category"));
            intent.putExtra("brandId",getIntent().getStringExtra("brandId"));
            startActivity(intent);
        }

        recyclerView = findViewById(R.id.myRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list = new ArrayList<message>();

        reference = FirebaseDatabase.getInstance().getReference().child("alerts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    message p = dataSnapshot1.getValue(message.class);
                    list.add(p);
                }
                adapter = new MyAdapter(getApplicationContext(),list);

                recyclerView.setAdapter(adapter);
                LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getApplicationContext(),R.anim.layout_anim);
                recyclerView.setLayoutAnimation(animationController);
                adapter.setOnItemClickListener(alertsActivity.this);

                //send notification to students for new message received from personnel
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Opss something went wrong",Toast.LENGTH_SHORT).show();
            }
        });


        mPullRefresh.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                list = new ArrayList<message>();

                reference = FirebaseDatabase.getInstance().getReference().child("alerts");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            message p = dataSnapshot1.getValue(message.class);
                            list.add(p);
                        }


                        adapter = new MyAdapter(getApplicationContext(),list);
                        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getApplicationContext(),R.anim.layout_anim);
                        recyclerView.setLayoutAnimation(animationController);
//                        recyclerView.setAdapter(adapter);
                        adapter.setOnItemClickListener(alertsActivity.this);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"Opss something went wrong",Toast.LENGTH_SHORT).show();
                    }
                });

                mPullRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullRefresh.setRefreshing(false);
                    }
                }, REFRESH_DELAY);
            }
        });






    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == item.getItemId()){
            //end Activity
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(getApplicationContext(),mapActivity.class);
        message message = list.get(position);
        detailIntent.putExtra(EXTRA_USERNAME,message.getUsername());
        detailIntent.putExtra(EXTRA_FULLNAME,message.getFull_name());
        detailIntent.putExtra(EXTRA_REF_NUM,message.getRef_number());
        detailIntent.putExtra(EXTRA_GENDER,message.getGender());
        detailIntent.putExtra(EXTRA_MESSAGE,message.getGender());
        detailIntent.putExtra(EXTRA_USER_UID,message.getUser_uid());

        startActivity(detailIntent);
    }
}
