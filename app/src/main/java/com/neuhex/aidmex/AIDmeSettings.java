package com.neuhex.aidmex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AIDmeSettings extends AppCompatActivity{

    EditText sosContact1, sosContact2, sosContact3, sosContact4, sosContact5;
    EditText userAge,userWeight,userHeight;
    String str_sosContact1, str_sosContact2, str_sosContact3, str_sosContact4, str_sosContact5;
    String str_userAge,str_userWeight,str_userHeight;
    Button btn_sosClear,btn_sosSave,btn_userClear,btn_userSave;

    SharedPreferences SharedPref_AIDmeSOS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidme_settings);
        sosContact1=findViewById(R.id.am_contact1);
        sosContact2=findViewById(R.id.am_contact2);
        sosContact3=findViewById(R.id.am_contact3);
        sosContact4=findViewById(R.id.am_contact4);
        sosContact5=findViewById(R.id.am_contact5);
        userAge=findViewById(R.id. user_age);
        userWeight=findViewById(R.id. user_weight);
        userHeight=findViewById(R.id. user_height);
        btn_sosClear=findViewById(R.id. button_amClear1);
        btn_sosSave=findViewById(R.id. button_amSave1);
        btn_userClear=findViewById(R.id. button_amClear2);
        btn_userSave=findViewById(R.id. button_amSave2);

        SharedPref_AIDmeSOS=getSharedPreferences("AIDmeSOSPref", Context.MODE_PRIVATE);

        SharedPreferences AM_SP= getApplicationContext().getSharedPreferences("AIDmeSOSPref", Context.MODE_PRIVATE);

        String saved_SOSContact1=AM_SP.getString("SOSContact1", "");
        String saved_SOSContact2=AM_SP.getString("SOSContact2", "");
        String saved_SOSContact3=AM_SP.getString("SOSContact3", "");
        String saved_SOSContact4=AM_SP.getString("SOSContact4", "");
        String saved_SOSContact5=AM_SP.getString("SOSContact5", "");

        String saved_userAge=AM_SP.getString("userAge","");
        String saved_userWeight=AM_SP.getString("userWeight","");
        String saved_userHeight=AM_SP.getString("userHeight","");

        if(saved_SOSContact1.length()!=0){
            sosContact1.setText(saved_SOSContact1);
        }
        if(saved_SOSContact2.length()!=0){
            sosContact2.setText(saved_SOSContact2);
        }
        if(saved_SOSContact3.length()!=0){
            sosContact3.setText(saved_SOSContact3);
        }
        if(saved_SOSContact4.length()!=0){
            sosContact4.setText(saved_SOSContact4);
        }
        if(saved_SOSContact5.length()!=0){
            sosContact5.setText(saved_SOSContact5);
        }


        if(saved_userAge.length()!=0){
            userAge.setText(saved_userAge);
        }
        if(saved_userWeight.length()!=0){
            userWeight.setText((saved_userWeight));
        }
        if(saved_userHeight.length()!=0){
            userHeight.setText(saved_userHeight);
        }

        checkSMSPermissions();

        btn_sosSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_sosContact1=sosContact1.getText().toString();
                str_sosContact2=sosContact2.getText().toString();
                str_sosContact3=sosContact3.getText().toString();
                str_sosContact4=sosContact4.getText().toString();
                str_sosContact5=sosContact5.getText().toString();

                SharedPreferences.Editor editor= SharedPref_AIDmeSOS.edit();

                editor.putString("SOSContact1",str_sosContact1);
                editor.putString("SOSContact2",str_sosContact2);
                editor.putString("SOSContact3",str_sosContact3);
                editor.putString("SOSContact4",str_sosContact4);
                editor.putString("SOSContact5",str_sosContact5);
                editor.commit();
                Toast.makeText(AIDmeSettings.this, "Contacts Saved!", Toast.LENGTH_LONG).show();

            }
        });

        btn_sosClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_sosContact1="";
                str_sosContact2="";
                str_sosContact3="";
                str_sosContact4="";
                str_sosContact5="";

                sosContact1.setText(str_sosContact1);
                sosContact2.setText(str_sosContact2);
                sosContact3.setText(str_sosContact3);
                sosContact4.setText(str_sosContact4);
                sosContact5.setText(str_sosContact5);

                SharedPreferences.Editor editor= SharedPref_AIDmeSOS.edit();

                editor.putString("SOSContact1",str_sosContact1);
                editor.putString("SOSContact2",str_sosContact2);
                editor.putString("SOSContact3",str_sosContact3);
                editor.putString("SOSContact4",str_sosContact4);
                editor.putString("SOSContact5",str_sosContact5);
                editor.commit();
                Toast.makeText(AIDmeSettings.this, "Contacts Cleared!", Toast.LENGTH_LONG).show();

            }
        });

        btn_userSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_userAge=userAge.getText().toString();
                str_userWeight=userWeight.getText().toString();
                str_userHeight=userHeight.getText().toString();

                SharedPreferences.Editor editor= SharedPref_AIDmeSOS.edit();

                editor.putString("userAge",str_userAge);
                editor.putString("userWeight",str_userWeight);
                editor.putString("userHeight",str_userHeight);
                editor.commit();
                Toast.makeText(AIDmeSettings.this, "User Info Saved!", Toast.LENGTH_LONG).show();

            }
        });

        btn_userClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_userAge="";
                str_userWeight="";
                str_userHeight="";

                userAge.setText(str_userAge);
                userWeight.setText(str_userWeight);
                userHeight.setText(str_userHeight);

                SharedPreferences.Editor editor= SharedPref_AIDmeSOS.edit();

                editor.putString("userAge",str_userAge);
                editor.putString("userWeight",str_userWeight);
                editor.putString("userHeight",str_userHeight);
                editor.commit();
                Toast.makeText(AIDmeSettings.this, "User Info Cleared!", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void checkSMSPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    42);
        }
    }
}