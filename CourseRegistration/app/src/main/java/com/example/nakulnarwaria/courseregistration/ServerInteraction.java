package com.example.nakulnarwaria.courseregistration;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ServerInteraction extends AppCompatActivity implements View.OnClickListener {

    private Button classRegistrationButton;
    private Button classesButton;
    private Button waitListButton;
    private String redId;
    private String password;
    ArrayList<String> registeredClasses = new ArrayList<String>();
    ArrayList<String> waitListedClasses = new ArrayList<String>();
    ArrayList<String> majors = new ArrayList<String>();
    String waitList;
    String registeredList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_interaction);
        getSupportActionBar().setTitle(getString(R.string.dashboard));
        classRegistrationButton=findViewById(R.id.classRegistrationButton);
        classesButton = findViewById(R.id.classesButton);
        waitListButton = findViewById(R.id.waitListButton);
        classRegistrationButton.setOnClickListener(this);
        classesButton.setOnClickListener(this);
        waitListButton.setOnClickListener(this);

        password = getIntent().getStringExtra("password");
        redId = getIntent().getStringExtra("redid");

    }

    @Override
    public void onClick(final View source) {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (!(networkInfo != null && networkInfo.isConnected())) {
            Toast.makeText(getApplicationContext(),getString(R.string.no_internet),Toast.LENGTH_SHORT).show();
            return;
        }
        if(source==classRegistrationButton){

            String url = getString(R.string.subject_list);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try{
                                for(int i=0;i<response.length();i++){
                                    JSONObject majorDetail = response.getJSONObject(i);
                                    String majorNameAndId = majorDetail.getString("title");
                                    majorNameAndId= "ID: "+majorDetail.getString("id")+" "+majorNameAndId;
                                    majors.add(majorNameAndId);

                                }
                                Intent searchCourse = new Intent(getApplicationContext(),SearchClass.class);
                                searchCourse.putStringArrayListExtra("majors",majors);
                                searchCourse.putExtra("redid",redId);
                                searchCourse.putExtra("password",password);
                                startActivity(searchCourse);
                            }catch (JSONException e){
                                Toast.makeText(getApplicationContext(),getString(R.string.no_selections_made),Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
            );
            RequestVolleyQueue.getInstance(getApplicationContext()).add(jsonArrayRequest);
        }
        else {
            String url = String.format(getString(R.string.student_classes_get),redId,password);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        if(response.has("error")){
                            Toast.makeText(getApplicationContext(),response.get("error").toString(),Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(response.has("waitlist")){
                            waitList = response.get("waitlist").toString();
                            waitList = waitList.substring(1,waitList.length()-1);
                            boolean empty=false;
                            if(waitList.replaceAll(" ","").equals(""))
                            {
                                waitListedClasses.clear();
                                empty=true;
                            }
                            if(source==waitListButton && !empty){
                                JSONObject data = new JSONObject();
                                data.put("classids",response.get("waitlist"));
                                final String classDetailsUrl = " https://bismarck.sdsu.edu/registration/classdetails";
                                CustomPostJsonArrayRequest customPostJsonArrayRequest = new CustomPostJsonArrayRequest(Request.Method.POST, classDetailsUrl, data,
                                        new Response.Listener<JSONArray>() {
                                            @Override
                                            public void onResponse(JSONArray response) {


                                                waitListedClasses.clear();
                                                try{
                                                    for(int i=0;i<response.length();i++){
                                                        JSONObject classDetail = response.getJSONObject(i);
                                                        String classNameAndId = classDetail.getString("title");
                                                        String timeOfClass = classDetail.getString("startTime")+"-"+ classDetail.getString("endTime");
                                                        String dayOfClass = classDetail.getString("days");
                                                        classNameAndId= classDetail.getString("id")+" "+classNameAndId + "\nDays of class - "+dayOfClass+"\nTime of class - "+timeOfClass+"\n";
                                                        waitListedClasses.add(classNameAndId);
                                                    }
                                                    Intent waitListedClassesListDisplay = new Intent(getApplicationContext(),CourseListDisplay.class);
                                                    waitListedClassesListDisplay.putExtra("already registered course","yes");
                                                    waitListedClassesListDisplay.putExtra("redid",redId);
                                                    waitListedClassesListDisplay.putExtra("password",password);
                                                    waitListedClassesListDisplay.putExtra("in waitlist","yes");
                                                    waitListedClassesListDisplay.putStringArrayListExtra("list",waitListedClasses);
                                                    startActivity(waitListedClassesListDisplay);
                                                }catch (JSONException e){
                                                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                                                    e.printStackTrace();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener(){
                                            @Override
                                            public void onErrorResponse(VolleyError error){
                                                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                                                error.printStackTrace();
                                            }

                                        }
                                );
                                RequestVolleyQueue.getInstance(getApplicationContext()).add(customPostJsonArrayRequest);
                            }
                            else if(source==waitListButton && empty){
                                Toast.makeText(getApplicationContext(),getString(R.string.empty_list),Toast.LENGTH_SHORT).show();
                            }
                        }
                        if(response.has("classes")){
                            registeredList = response.get("classes").toString();
                            registeredList=registeredList.substring(1,registeredList.length()-1);
                            boolean empty=false;
                            if(registeredList.replaceAll(" ","").equals(""))
                            {
                                empty=true;
                                registeredClasses.clear();
                            }

                            if(source==classesButton && !empty){
                                JSONObject data = new JSONObject();
                                data.put("classids",response.get("classes"));
                                String classDetailsUrl = getString(R.string.class_details_post);
                                CustomPostJsonArrayRequest customPostJsonArrayRequest = new CustomPostJsonArrayRequest(Request.Method.POST, classDetailsUrl, data,
                                        new Response.Listener<JSONArray>() {
                                            @Override
                                            public void onResponse(JSONArray response) {

                                                registeredClasses.clear();
                                                try{
                                                    for(int i=0;i<response.length();i++){
                                                        JSONObject classDetail = response.getJSONObject(i);
                                                        String classNameAndId = classDetail.getString("title");
                                                        String timeOfClass = classDetail.getString("startTime")+"-"+ classDetail.getString("endTime");
                                                        String dayOfClass = classDetail.getString("days");
                                                        classNameAndId= classDetail.getString("id")+" "+classNameAndId + "\nDays of class - "+dayOfClass+"\nTime of class - "+timeOfClass+"\n";
                                                        registeredClasses.add(classNameAndId);
                                                    }
                                                    Intent registeredClassesListDisplay = new Intent(getApplicationContext(),CourseListDisplay.class);
                                                    registeredClassesListDisplay.putExtra("already registered course","yes");
                                                    registeredClassesListDisplay.putExtra("redid",redId);
                                                    registeredClassesListDisplay.putExtra("password",password);
                                                    registeredClassesListDisplay.putExtra("in waitlist","no");
                                                    registeredClassesListDisplay.putStringArrayListExtra("list",registeredClasses);
                                                    startActivity(registeredClassesListDisplay);
                                                }catch (JSONException e){
                                                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                                                    e.printStackTrace();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener(){
                                            @Override
                                            public void onErrorResponse(VolleyError error){
                                                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                                                error.printStackTrace();
                                            }

                                        }
                                );
                                RequestVolleyQueue.getInstance(getApplicationContext()).add(customPostJsonArrayRequest);
                            }
                            else if(source==classesButton && empty){
                                Toast.makeText(getApplicationContext(),getString(R.string.empty_list),Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();

                }
            });
            RequestVolleyQueue.getInstance(getApplicationContext()).add(jsonObjectRequest);
        }
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent home = new Intent(this,Login.class);
            navigateUpTo(home);

        }
        return super.onKeyDown(keyCode, event);
    }

}
