package com.example.nakulnarwaria.personalinformation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BasicProfile extends AppCompatActivity implements View.OnClickListener {

    private final int REQUEST_MAJOR=0;
    private EditText firstNameET;
    private EditText lastNameET;
    private EditText ageET;
    private EditText emailET;
    private EditText phoneET;
    private TextView majorDisplayTV;
    private Button doneButton;
    public static final String personalData = "BasicProfileData";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_profile);

        firstNameET = findViewById(R.id.firstNameET);
        lastNameET = findViewById(R.id.lastNameET);
        ageET = findViewById(R.id.ageET);
        emailET = findViewById(R.id.emailET);
        phoneET = findViewById(R.id.phoneET);
        majorDisplayTV = findViewById(R.id.majorDisplayTV);
        doneButton = findViewById(R.id.doneButton);
        doneButton.setOnClickListener(this);
        majorDisplayTV.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){
                Intent selectMajorIntent = new Intent(view.getContext(),MajorSelection.class);
                startActivityForResult(selectMajorIntent,REQUEST_MAJOR);
                return false;
            }
        });

        // Restore preferences
        reloadData();
    }

    public void onClick(View source) {
        if(source == doneButton){
            Log.v("OnClick","Click");
            SharedPreferences personData = getSharedPreferences(personalData, 0);
            SharedPreferences.Editor personDataEditor = personData.edit();
            personDataEditor.putString("First Name",firstNameET.getText().toString());
            personDataEditor.putString("Last Name",lastNameET.getText().toString());
            personDataEditor.putString("Age",ageET.getText().toString());
            personDataEditor.putString("E-mail",emailET.getText().toString());
            personDataEditor.putString("Phone",phoneET.getText().toString());
            personDataEditor.putString("Major",majorDisplayTV.getText().toString());
            personDataEditor.commit();//Writes on main thread
            Toast informationsSaved= Toast.makeText(this,"Your information has been saved",Toast.LENGTH_SHORT);
            informationsSaved.show();
        }

    }

    public void onSaveInstanceState(Bundle onSaveInstanceState){
        super.onSaveInstanceState(onSaveInstanceState);
        Log.v("onSave","called");
    }


    @Override
    public void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        Log.v("OnRestore","Called");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode!=REQUEST_MAJOR){
            return;
        }
        switch(resultCode){
            case RESULT_OK:
                try{
                    majorDisplayTV.setText(data.getExtras().getString("Major"));
                    break;
                }
                catch(NullPointerException e){
                   Log.d("No String Found","Major Missing");
                }
            case RESULT_CANCELED:
                break;
        }
    }

    public void reloadData(){
        SharedPreferences personData = getSharedPreferences(personalData, 0);
        firstNameET.setText(personData.getString("First Name", ""));
        lastNameET.setText(personData.getString("Last Name", ""));
        ageET.setText(""+personData.getString("Age", ""));
        emailET.setText(personData.getString("E-mail", ""));
        phoneET.setText(personData.getString("Phone", ""));
        majorDisplayTV.setText(personData.getString("Major",""));
    }
}
