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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;

public class UserChange extends AppCompatActivity implements View.OnClickListener {

    private EditText redIdEnterET;
    private EditText passwordEnterET;
    private Button loginNewButton;
    private String redId="";
    private String password="";
    public static final String personalData = "BasicProfileData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_change);
        redIdEnterET = findViewById(R.id.redIdEnterET);
        passwordEnterET = findViewById(R.id.passwordEnterET);
        loginNewButton=findViewById(R.id.loginNewButton);
        loginNewButton.setOnClickListener(this);
        reloadData();
        getSupportActionBar().setTitle(getString(R.string.sdsu_login));
        redId=redIdEnterET.getText().toString();
        password=passwordEnterET.getText().toString();
    }

    @Override
    public void onClick(View source) {
        if(source==loginNewButton){
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (!(networkInfo != null && networkInfo.isConnected())) {
                Toast.makeText(getApplicationContext(),getString(R.string.no_internet),Toast.LENGTH_SHORT).show();
                return;
            }
            password=passwordEnterET.getText().toString();
            redId=redIdEnterET.getText().toString();
            String url = String.format(getString(R.string.student_classes_get),redId,password);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(response.toString().contains("does not match")||response.toString().contains("No student with red id")){
                        Toast.makeText(getApplicationContext(),getString(R.string.incorrect_login),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    saveData();
                    Intent serverInteraction = new Intent(getApplicationContext(),ServerInteraction.class);
                    serverInteraction.putExtra("password",passwordEnterET.getText().toString());
                    serverInteraction.putExtra("redid",redIdEnterET.getText().toString());
                    startActivity(serverInteraction);

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error :",error.toString());
                }
            });
            RequestVolleyQueue.getInstance(getApplicationContext()).add(jsonObjectRequest);
        }
    }

    public void reloadData(){
        try{
            SharedPreferences personData = getSharedPreferences(personalData, 0);
            redIdEnterET.setText(personData.getString("Red Id", ""));
            passwordEnterET.setText(personData.getString("Password", ""));
        }
        catch(Exception e){
            redIdEnterET.setText("");
            passwordEnterET.setText("");
        }

    }

    public void saveData(){
        SharedPreferences personData = getSharedPreferences(personalData, 0);
        SharedPreferences.Editor personDataEditor = personData.edit();
        personDataEditor.putString("Red Id",redIdEnterET.getText().toString());
        personDataEditor.putString("Password",passwordEnterET.getText().toString());
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
