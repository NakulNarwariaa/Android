package com.example.nakulnarwaria.courseregistration;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

public class CourseDetails extends AppCompatActivity implements View.OnClickListener {

    private TextView titleDisplayTV;
    private TextView timeDisplayTV;
    private TextView locationDisplayTV;
    private TextView instructorDisplayTV;
    private TextView courseNoDisplayTV;
    private TextView unitDisplayTV;
    private TextView seatsDisplayTV;
    private TextView enrolledDisplayTV;
    private TextView waitlistedDisplayTV;
    private TextView prerequisiteDisplayTV;
    private TextView descriptionDisplayTV;
    private Button addOrDropButton;
    private String courseNo;
    private String course;
    private String redId;
    private String password;
    private String alreadyRegistered;
    private String inWaitList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        getSupportActionBar().setTitle(getString(R.string.course_description));
        titleDisplayTV = findViewById(R.id.titleDisplayTV);
        timeDisplayTV= findViewById(R.id.timeDisplayTV);
        locationDisplayTV = findViewById(R.id.locationDisplayTV);
        instructorDisplayTV = findViewById(R.id.instructorDisplayTV);
        courseNoDisplayTV = findViewById(R.id.courseNoDisplayTV);
        unitDisplayTV = findViewById(R.id.unitsDisplayTV);
        seatsDisplayTV = findViewById(R.id.seatsDisplayTV);
        enrolledDisplayTV = findViewById(R.id.enrolledDisplayTV);
        waitlistedDisplayTV = findViewById(R.id.waitlistedDisplayTV);
        prerequisiteDisplayTV = findViewById(R.id.prerequisiteDisplayTV);
        prerequisiteDisplayTV.setMovementMethod(new ScrollingMovementMethod());
        descriptionDisplayTV = findViewById(R.id.descriptionDisplayTV);
        descriptionDisplayTV.setMovementMethod(new ScrollingMovementMethod());
        addOrDropButton = findViewById(R.id.addOrDropButton);
        addOrDropButton.setOnClickListener(this);
        course = getIntent().getStringExtra("course number");
        courseNo = course.split(" ")[0];
        redId = getIntent().getStringExtra("redid");
        password = getIntent().getStringExtra("password");
        alreadyRegistered = getIntent().getStringExtra("already registered course");
        inWaitList = getIntent().getStringExtra("in waitlist");
        if(alreadyRegistered.equals("yes")){
            addOrDropButton.setText(getString(R.string.drop_classes));
        }
        else{
            addOrDropButton.setText(getString(R.string.add_class));
        }
        loadDataIntoDisplay(courseNo);
    }

    private void loadDataIntoDisplay(String courseNo) {
       //load course details of courseNo and display on TVs.
        String url = String.format(getString(R.string.class_details),courseNo);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(response.has("error")){
                    try {
                        Toast.makeText(getApplicationContext(),response.get("error").toString(),Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                try {
                    if(response.has("title"))
                        titleDisplayTV.setText(response.get("title").toString());
                    if(response.has("startTime") && response.has("endTime"))
                        timeDisplayTV.setText(response.getString("startTime")+" to "+response.getString("endTime"));
                    if(response.has("building"))
                        locationDisplayTV.setText(response.get("building").toString());
                    if(response.has("instructor"))
                        instructorDisplayTV.setText(response.get("instructor").toString());
                    if(response.has("course#"))
                        courseNoDisplayTV.setText(response.get("course#").toString());
                    if(response.has("units"))
                        unitDisplayTV.setText(response.get("units").toString());
                    if(response.has("seats"))
                        seatsDisplayTV.setText(response.get("seats").toString());
                    if(response.has("enrolled"))
                        enrolledDisplayTV.setText(response.get("enrolled").toString());
                    if(response.has("waitlist"))
                        waitlistedDisplayTV.setText(response.get("waitlist").toString());
                    if(response.has("prerequisite"))
                        prerequisiteDisplayTV.setText(response.get("prerequisite").toString());
                    if(response.has("description"))
                        descriptionDisplayTV.setText(response.get("description").toString());

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

    @Override
    public void onClick(View source) {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (!(networkInfo != null && networkInfo.isConnected())) {
            Toast.makeText(getApplicationContext(),getString(R.string.no_internet),Toast.LENGTH_SHORT).show();
            return;
        }
        if(source== addOrDropButton){
            if(alreadyRegistered.equals("yes") && inWaitList.equals("yes")){
                makeDialog(getString(R.string.drop_sure),getString(R.string.unwaitlist_class_get),redId,password,courseNo);
            }
            else if(alreadyRegistered.equals("yes") && inWaitList.equals("no")){
                makeDialog(getString(R.string.drop_sure),getString(R.string.unregister_class_get),redId,password,courseNo);
            }
            else{
                makeDialog(getString(R.string.add_sure),getString(R.string.register_class_get),redId,password,courseNo);
            }
        }
    }

    public void makeDialog(String question,final String url, final String redId, final String password, final String courseNo){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.confirm));
        builder.setMessage(question);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                addOrDropClass(url,redId,password,courseNo);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void addOrDropClass(String url, final String redId, final String password, final String courseId){
        url = String.format(url, redId,password,courseId);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(response.has("ok")){
                    Intent updateListIntent = getIntent();

                    if(alreadyRegistered.equals("yes"))
                    {
                        Toast.makeText(getApplicationContext(),getString(R.string.drop_successful),Toast.LENGTH_SHORT).show();
                        updateListIntent.putExtra("course add or drop","drop");
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),getString(R.string.add_successful),Toast.LENGTH_SHORT).show();
                        updateListIntent.putExtra("course add or drop","add");
                    }
                    updateListIntent.putExtra("course number",course);
                    setResult(RESULT_OK,updateListIntent);
                    finish();
                }
                if(response.has("error")){
                    try {
                        Toast.makeText(getApplicationContext(),response.get("error").toString(),Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(response.toString().contains("full")){
                        makeDialog(getString(R.string.course_full),getString(R.string.waitlist_class_get),redId,password,courseId);
                    }
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

