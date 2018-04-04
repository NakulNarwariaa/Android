package com.example.nakulnarwaria.courseregistration;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

public class StudentProfile extends AppCompatActivity implements View.OnClickListener {

    private EditText firstNameET;
    private EditText lastNameET;
    private EditText redIdET;
    private EditText emailET;
    private EditText passwordET;
    private Button saveProfileButton;
    private Button continueButton;
    public static final String personalData = "BasicProfileData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        firstNameET = findViewById(R.id.firstNameET);
        lastNameET = findViewById(R.id.lastNameET);
        redIdET = findViewById(R.id.ageET);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        saveProfileButton.setOnClickListener(this);
        continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(this);
        continueButton.setEnabled(false);
        CertificateApplication bismarck = new CertificateApplication();
        bismarck.trustBismarckCertificate();
        getSupportActionBar().setTitle(getString(R.string.edit_profile));

    }

    public void onClick(View source) {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (!(networkInfo != null && networkInfo.isConnected())) {
            Toast.makeText(getApplicationContext(),getString(R.string.no_internet),Toast.LENGTH_SHORT).show();
            return;
        }
        if(source == saveProfileButton){
            if(firstNameET.getText().toString().isEmpty() || lastNameET.toString().isEmpty() || redIdET.getText().toString().isEmpty() || emailET.getText().toString().isEmpty() || passwordET.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(),R.string.empty_field,Toast.LENGTH_SHORT).show();
            }
            else if(redIdET.getText().toString().length()!=9){
                Toast.makeText(getApplicationContext(),R.string.red_id_constraint,Toast.LENGTH_SHORT).show();
            }
            else{
                Log.v("OnClick","Click");
                String url=getString(R.string.register_student);
                JSONObject data = new JSONObject();
                try {
                    data.put("firstname", firstNameET.getText().toString());
                    data.put("lastname", lastNameET.getText().toString());
                    data.put("redid",redIdET.getText().toString());
                    data.put("password",passwordET.getText().toString());
                    data.put("email",emailET.getText().toString());
                } catch (JSONException error) {
                    Log.e("rew", "JSON eorror", error);
                    return;
                }
                Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("rew", response.toString());
                        if(!response.toString().contains("error")){
                            Toast.makeText(getApplicationContext(),getString(R.string.student_registered),Toast.LENGTH_SHORT).show();
                            continueButton.setEnabled(true);
                            saveData();
                        }
                        else
                        {
                            try {
                                Toast.makeText(getApplicationContext(),response.get("error").toString(),Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                Response.ErrorListener failure = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("rew", "post fail " + new String(error.networkResponse.data));
                        Toast.makeText(getApplicationContext(),error.networkResponse.data.toString(),Toast.LENGTH_SHORT).show();
                    }
                };

                JsonObjectRequest postRequest= new JsonObjectRequest(url,data, success,failure);
                RequestVolleyQueue.getInstance(getApplicationContext()).add(postRequest);

            }
        }

        if(source == continueButton){
            Intent serverInteraction = new Intent(getApplicationContext(),ServerInteraction.class);
            serverInteraction.putExtra("password",passwordET.getText().toString());
            serverInteraction.putExtra("redid",redIdET.getText().toString());
            startActivity(serverInteraction);
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

    public void saveData(){
        SharedPreferences personData = getSharedPreferences(personalData, 0);
        SharedPreferences.Editor personDataEditor = personData.edit();
        personDataEditor.putString("First Name",firstNameET.getText().toString());
        personDataEditor.putString("Last Name",lastNameET.getText().toString());
        personDataEditor.putString("Red Id",redIdET.getText().toString());
        personDataEditor.putString("E-mail",emailET.getText().toString());
        personDataEditor.putString("Password",passwordET.getText().toString());
        personDataEditor.commit();//Writes on main thread
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        if(getCurrentFocus() != null){
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return true;
    }
}
