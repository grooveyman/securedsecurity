package com.example.aboagyemaxwell.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



public class broadcastActivity extends AppCompatActivity {

     EditText tittle,message,tittle_r,message_r;
    Button send_message;
    ProgressBar progressBar;
    DatabaseReference databaseReference;

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;

    private RequestQueue mRequestQueue;
    private String URL = "https://fcm.googleapis.com/fcm/send";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);

        getSupportActionBar().setTitle("Broadcast Information");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        tittle= findViewById(R.id.tittle);
        message = findViewById(R.id.b_message);
        send_message = findViewById(R.id.send_broadcast_message);
        progressBar = findViewById(R.id.progressBar);

        mRequestQueue = Volley.newRequestQueue(this);
        FirebaseMessaging.getInstance().subscribeToTopic("security");

         if (getIntent().hasExtra("category") && getIntent().getStringExtra("category").equals("broadcast")){
            Intent intent = new Intent(broadcastActivity.this, ViewBroadcast.class);
            intent.putExtra("category",getIntent().getStringExtra("category"));
            intent.putExtra("brandId",getIntent().getStringExtra("brandId"));
            startActivity(intent);
        }



        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(tittle.getText().toString().trim())){
                    Toast.makeText(getApplicationContext(), "Title field can't be empty", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(message.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Message field cant be empty", Toast.LENGTH_SHORT).show();
                }else {
                    progressBar.setVisibility(View.VISIBLE);

//                    FirebaseDatabase.getInstance().getReference().child("Tokens").child("OkRcv4JpDpcKNvpvJtaV60U4tDl2\n").child("token").addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            String userToken = dataSnapshot.getValue(String.class);
////                            sendNotifications(userToken,"Trial","This is a test notification");
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });

                    NOTIFICATION_TITLE = tittle.getText().toString().trim();
                    NOTIFICATION_MESSAGE = message.getText().toString().trim();

                    broadcast information = new broadcast(tittle.getText().toString(),message.getText().toString());

                    // Write a message to the database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference().child("broadcasts");
                    DatabaseReference newbroadcast = myRef.push();



                    // notify



                    newbroadcast.setValue(information).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                sendNotification();
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),"broadcast Sent",Toast.LENGTH_SHORT).show();


//                                TOPIC = "/topics/userABC"; //topic has to match what the receiver subscribed to
//                                NOTIFICATION_TITLE = tittle.getText().toString();
//                                NOTIFICATION_MESSAGE = message.getText().toString();
//
//                                final JSONObject notification = new JSONObject();
//                                JSONObject notifcationBody = new JSONObject();
//                                try {
//                                    notifcationBody.put("title", NOTIFICATION_TITLE);
//                                    notifcationBody.put("message", NOTIFICATION_MESSAGE);
//
//                                    notification.put("to", TOPIC);
//                                    notification.put("data", notifcationBody);
//                                } catch (JSONException e) {
//                                    Log.e(TAG, "onCreate: " + e.getMessage() );
//                                }


                                tittle.getText().clear();
                                message.getText().clear();
                                startActivity(new Intent(getApplicationContext(), ViewBroadcast.class));

                            }else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(),"broadcast failed",Toast.LENGTH_SHORT).show();
                                }
                        }
                    });



                }


            }
        });


        /* notification using cloud messaging */







    }

    public void sendNotification() {

        //json object
        JSONObject mainObj = new JSONObject();
        try{

            mainObj.put("to", "/topics/"+"security");
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title",NOTIFICATION_TITLE);
            notificationObj.put("body",NOTIFICATION_MESSAGE);

            JSONObject extraData = new JSONObject();
            extraData.put("brandId","apb");
            extraData.put("category","broadcast");
            mainObj.put("notification", notificationObj);
            mainObj.put("data",extraData);



            //create json object request

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    mainObj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAAO3wp-Uk:APA91bGtF2n94JnfiJ5yFbtdVqB3lBJ62zIMa4WW3HKziYRyny9erjiVAiVznqElvrn2RpcrWVEQyxDUtvLDVwKNkJOh1DQL1XZ8yw_PGKQdheiUpt1cTBh2-SJLIEy2t6VVgd68ctEh");
                    return header;
                }
            };

            mRequestQueue.add(request);
        }catch (JSONException e){
            e.printStackTrace();
        }


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
}
