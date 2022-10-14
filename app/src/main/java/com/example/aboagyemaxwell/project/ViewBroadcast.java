package com.example.aboagyemaxwell.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yalantis.phoenix.PullToRefreshView;

public class ViewBroadcast extends AppCompatActivity {
    private static final long REFRESH_DELAY = 2000;
    private RecyclerView recyclerView;
    private DatabaseReference broadcastReference;

    private PullToRefreshView mPullRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_broadcast);
        mPullRefresh = findViewById(R.id.pull_to_refresh1);

        if (getIntent().hasExtra("category")){
//            String category = getIntent().getStringExtra("category");
//            String brand = getIntent().getStringExtra("brandId");
//            categotyTv.setText(category);
//            brandTv.setText(brand);
            Toast.makeText(getApplicationContext(), "Here", Toast.LENGTH_SHORT).show();
        }

        recyclerView = findViewById(R.id.viewBroadcast);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        broadcastReference = FirebaseDatabase.getInstance().getReference().child("broadcasts");



//        FloatingActionButton fab = findViewById(R.id.fabBroadcast   );
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(), broadcastActivity.class));
//            }
//        });




        mPullRefresh.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //reload or refresh the recyler view items
                FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<broadcast>()
                        .setQuery(broadcastReference, broadcast.class)
                        .build();


                FirebaseRecyclerAdapter<broadcast, BroadcastViewholder> adapter =  new FirebaseRecyclerAdapter<broadcast, BroadcastViewholder>(options){

                    @NonNull
                    @Override
                    public BroadcastViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.broadcast_view, parent, false);
                        ViewBroadcast.BroadcastViewholder viewHolder = new ViewBroadcast.BroadcastViewholder(view);
                        return viewHolder;

                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final BroadcastViewholder broadcastViewholder, int i, @NonNull broadcast broadcast) {
                        final String broadcastId =  getRef(i).getKey();

                        broadcastReference.child(broadcastId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild("title")){

                                    final String broadTitle = dataSnapshot.child("title").getValue().toString();
                                    final String broadMessage = dataSnapshot.child("message").getValue().toString();

                                    broadcastViewholder.broadcastTitle.setText(broadTitle);

                                    broadcastViewholder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            AlertDialog.Builder broadcastAlert = new AlertDialog.Builder(ViewBroadcast.this);
                                            broadcastAlert.setTitle(broadTitle);
                                            broadcastAlert.setMessage(broadMessage);
                                            broadcastAlert.setPositiveButton("OK", null);
                                            broadcastAlert.setCancelable(true);
                                            broadcastAlert.create().show();

//                                    Intent singleBroad = new Intent(getApplicationContext(),broadcastDetails.class);
//                                    singleBroad.putExtra("broad_Id",broadcastId);
//                                    startActivity(singleBroad);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                };
                recyclerView.setAdapter(adapter);
                LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getApplicationContext(),R.anim.layout_anim);
                recyclerView.setLayoutAnimation(animationController);

                adapter.startListening();
                mPullRefresh.postDelayed(new Runnable() {


                    @Override
                    public void run() {
                        mPullRefresh.setRefreshing(false);
                    }
                }, REFRESH_DELAY);
            }
        });




    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.broadcast_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.addbroadcast){
            startActivity(new Intent(getApplicationContext(), broadcastActivity.class));
        }

        return super.onOptionsItemSelected(item);


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<broadcast>()
                .setQuery(broadcastReference, broadcast.class)
                .build();


        FirebaseRecyclerAdapter<broadcast, BroadcastViewholder> adapter =  new FirebaseRecyclerAdapter<broadcast, BroadcastViewholder>(options){

            @NonNull
            @Override
            public BroadcastViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.broadcast_view, parent, false);
                ViewBroadcast.BroadcastViewholder viewHolder = new ViewBroadcast.BroadcastViewholder(view);
                return viewHolder;

            }
            String broadTitle;
            String broadMessage;

            @Override
            protected void onBindViewHolder(@NonNull final BroadcastViewholder broadcastViewholder, int i, @NonNull broadcast broadcast) {
                final String broadcastId =  getRef(i).getKey();


                broadcastReference.child(broadcastId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild("title")){
                         broadTitle = dataSnapshot.child("title").getValue().toString();
                        broadMessage = dataSnapshot.child("message").getValue().toString();


                           //notify user of a new broaadcast
//                            tell_user(broadTitle,broadMessage);

                            broadcastViewholder.broadcastTitle.setText(broadTitle);

                            broadcastViewholder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    Intent singleBroad = new Intent(getApplicationContext(),broadcastDetails.class);
//                                    singleBroad.putExtra("broad_Id",broadcastId);
//                                    startActivity(singleBroad);
                                    AlertDialog.Builder broadcastAlert = new AlertDialog.Builder(ViewBroadcast.this);
                                    broadcastAlert.setTitle(broadTitle);
                                    broadcastAlert.setMessage(broadMessage);
                                    broadcastAlert.setPositiveButton("OK", null);
                                    broadcastAlert.setCancelable(true);
                                    broadcastAlert.create().show();

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
//                broadcastReference.addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                        tell_user(broadTitle,broadMessage);
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
            }
        };

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(positionStart);
            }
        });
        recyclerView.setAdapter(adapter);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getApplicationContext(),R.anim.layout_anim);
        recyclerView.setLayoutAnimation(animationController);
        adapter.startListening();
    }



    public static class BroadcastViewholder extends RecyclerView.ViewHolder{
        TextView broadcastTitle;

        public BroadcastViewholder(View itemView){
            super(itemView);

            broadcastTitle = itemView.findViewById(R.id.broadcastTitle);


        }
    }


    //function to trigger notifications
    public void tell_user(String Tittle,String Message){
          final String CHANNEL_ID ="Notificatioon" ;

        //check os version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Broadcast", importance);
            channel.setDescription("All is well");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }




        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, ViewBroadcast.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(Tittle)
                .setContentText(Message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);



        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());



    }




}
