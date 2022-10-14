package com.example.aboagyemaxwell.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetupApp extends AppCompatActivity {

    private EditText regEmail,regis_key;
    private Button regBtn;
    private TextView loginText;
    private ArrayList<String> keys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_app);

        keys = new ArrayList<String>( Arrays.asList("E\"31A4D2*1","@!77BC11j'","^C81c4jK/2","-)ad1431$.","1%0D1K32';") );
        regEmail = findViewById(R.id.regis_email);
        regBtn = findViewById(R.id.register_btn);
        loginText = findViewById(R.id.already_account);
        regis_key = findViewById(R.id.regis_secretKey);


        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });



        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String email = regEmail.getText().toString().trim();
                 String secret_key = regis_key.getText().toString().trim();



                    if(TextUtils.isEmpty(email) || TextUtils.isEmpty(secret_key)) {
                        Toast.makeText(getApplicationContext(),"Please no Fields can be empty ", Toast.LENGTH_SHORT).show();
                    }else

                         if(validate(secret_key,keys)){
                            if (!isEmailValid(email)){
                                Toast.makeText(getApplicationContext(),"Please email is not valid",Toast.LENGTH_SHORT).show();
                        }else {
                                Intent cRegister = new Intent(SetupApp.this, ContinueSetup.class);
                                cRegister.putExtra("fromEmail", email);
                                startActivity(cRegister);
                    }
                }else {
                             Toast.makeText(getApplicationContext(),"Please your secrey key is invalid. Talk to your IT department for a valid key",Toast.LENGTH_SHORT).show();
                         }

                    }
        });


    }

    public boolean isEmailValid(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else
            return false;
    }


    public boolean validate(String key, ArrayList<String> keys){
        boolean check = false;
        for (String t: keys){
            if(t.equals(key)){
                check=true;
            }
        }
        return check;
    }


}
