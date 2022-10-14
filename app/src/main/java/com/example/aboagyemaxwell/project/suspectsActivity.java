package com.example.aboagyemaxwell.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class suspectsActivity extends AppCompatActivity {

    private EditText suspectName,suspectDetails,susProgramme;
    private Button submitBtn;
    private ImageButton suspectImg;
    private static final int GALLERY_REQUEST = 1;
    private StorageTask uploadTask;
    private Uri imageUri = null;
    String sus_name,susStatus,sus_Programme;
    String sus_details;

    private Spinner suspectSpinner;

    private ProgressDialog mProgress;
    private DatabaseReference mReference;

    private StorageReference mStorage;

    broadcastActivity broadcastActivity;

    String NOTIFICATION_TITLE, NOTIFICATION_MESSAGE;
    private RequestQueue mRequestQueue;
    private String URL = "https://fcm.googleapis.com/fcm/send";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suspects);

        broadcastActivity = new broadcastActivity();

        suspectName = findViewById(R.id.nameOfSuspect);
        suspectDetails = findViewById(R.id.detailsOfSuspect);
        suspectImg = findViewById(R.id.addImgOfSuspect);
        submitBtn = findViewById(R.id.setApbBtn);
        susProgramme = findViewById(R.id.programOfSuspect);
        mStorage = FirebaseStorage.getInstance().getReference("Image");
        mProgress = new ProgressDialog(this);
        mReference = FirebaseDatabase.getInstance().getReference().child("Suspects");
        mRequestQueue = Volley.newRequestQueue(this);

        FirebaseMessaging.getInstance().subscribeToTopic("security");

        if (getIntent().hasExtra("category") && getIntent().getStringExtra("category").equals("suspect")){
            Intent intent = new Intent(suspectsActivity.this, SuspectFragment.class);
            intent.putExtra("category",getIntent().getStringExtra("category"));
            intent.putExtra("brandId",getIntent().getStringExtra("brandId"));
            startActivity(intent);
        }

        addSusSpinner();

        suspectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CropImage.startPickImageActivity(suspectsActivity.this);
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }

    private void addSusSpinner() {
        suspectSpinner = findViewById(R.id.sus_spinner);
        List<String> sus = new ArrayList<String>();
        sus.add("Rusticated");
        sus.add("Suspect");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sus);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suspectSpinner.setAdapter(dataAdapter);

        suspectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                susStatus = suspectSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    private void startPosting() {
        mProgress.setMessage("Posting...");
        mProgress.show();
         sus_name = suspectName.getText().toString().trim();
         sus_details = suspectDetails.getText().toString().trim();
         sus_Programme = susProgramme.getText().toString().trim();

        if(!TextUtils.isEmpty(sus_name) && !TextUtils.isEmpty(sus_details) && !TextUtils.isEmpty(sus_Programme) && imageUri != null){
            NOTIFICATION_TITLE = "A suspect has been declared";
            NOTIFICATION_MESSAGE = sus_name+ " of " +sus_Programme+" has been declared a suspect.";
            FileUploader();

        }
    }

    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }


    private void FileUploader() {
//        UploadTask uploadTask = mStorage.putBytes()
        StorageReference ref = mStorage.child(System.currentTimeMillis() + "."+ getExtension(imageUri));



        ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String photoLink = uri.toString();

//                                String downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                                DatabaseReference newPost = mReference.push();
                                newPost.child("Suspect_Name").setValue(sus_name);
                                newPost.child("Suspect_Details").setValue(sus_details);
                                newPost.child("Suspect_Status").setValue(susStatus);
                                newPost.child("Suspect_Programme").setValue(sus_Programme);
                                newPost.child("image").setValue(photoLink);
                                mProgress.dismiss();
                                sendNotification();

                                startActivity(new Intent(suspectsActivity.this,MainActivity.class));

                            }
                        });
                                // Get a URL to the uploaded content

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            assert data != null;
            Uri imgUri = data.getData();
            CropImage.activity(imgUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
//            suspectImg.setImageURI(imageUri);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){
                assert result != null;
                Uri resultUri = result.getUri();
                suspectImg.setImageURI(resultUri);
                imageUri = resultUri;
            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                assert result != null;
                Exception error = result.getError();
            }
        }



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
            extraData.put("category","suspect");
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

}
