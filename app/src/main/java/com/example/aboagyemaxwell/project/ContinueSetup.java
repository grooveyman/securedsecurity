package com.example.aboagyemaxwell.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ContinueSetup extends AppCompatActivity {

    private Spinner genderSpinner;
    private Button nextBtn;
    private EditText username, fullname;
    private String genderString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue_setup);

        nextBtn = findViewById(R.id.register_btnNext);
        username = findViewById(R.id.regis_username);
        fullname = findViewById(R.id.regis_fullname);
        addItemSpinner();


        /* getting data from previous intent */
        Intent intent = getIntent();
        final String email = intent.getStringExtra("fromEmail");



        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String uname = username.getText().toString().trim();
                String fname = fullname.getText().toString().trim();
                if(!TextUtils.isEmpty(uname) || !TextUtils.isEmpty(fname)) {
                    Intent i = new Intent(getApplicationContext(), FinishSetup.class);
                    i.putExtra("email", email);
                    i.putExtra("usname",uname);
                    i.putExtra("flname", fname);
                    i.putExtra("gender", genderString);

                    startActivity(i);
                }else
                    Toast.makeText(getApplicationContext(), "Please enter username and or fullname", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void addItemSpinner() {

        genderSpinner = (Spinner)findViewById(R.id.genderSpinner);
        List<String> gender = new ArrayList<String>();
        gender.add("Male");
        gender.add("Female");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, gender);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(dataAdapter);


        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                genderString = genderSpinner.getSelectedItem().toString();
//                Toast.makeText(getApplicationContext(), genderSpinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
