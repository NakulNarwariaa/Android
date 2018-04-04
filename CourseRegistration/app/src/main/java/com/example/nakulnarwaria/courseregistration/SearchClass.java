package com.example.nakulnarwaria.courseregistration;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SearchClass extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {

    private Spinner majorSpinner;
    private Spinner levelSpinner;
    private EditText startTimeET;
    private EditText endTimeET;
    private Button clearButton;
    private Button searchButton;
    private String redId;
    private String password;
    private ArrayList<String> majors = new ArrayList<String>();
    private ArrayList<String> levels = new ArrayList<String>();
    private ArrayList<String> courseIds = new ArrayList<String>();
    private String startTime="";
    private String endTime="";
    private String selectedMajor="";
    private String selectedLevel="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_class);
        getSupportActionBar().setTitle(getString(R.string.class_search));
        majorSpinner= (Spinner) findViewById(R.id.majorSpinner);
        majorSpinner.setOnItemSelectedListener(this);
        levelSpinner = (Spinner) findViewById(R.id.levelSpinner);
        levelSpinner.setOnItemSelectedListener(this);
        startTimeET =findViewById(R.id.startTimeET);
        endTimeET = findViewById(R.id.endTimeET);
        searchButton = findViewById(R.id.searchButton);
        clearButton = findViewById(R.id.clearButton);
        majors = getIntent().getStringArrayListExtra("majors");
        redId = getIntent().getStringExtra("redid");
        password = getIntent().getStringExtra("password");
        levels.add("");
        levels.add("graduate");
        levels.add("upper");
        levels.add("lower");

        searchButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        ArrayAdapter<String> majorAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        majors);
        majorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        majorSpinner.setAdapter(majorAdapter);
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        levels);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelSpinner.setAdapter(levelAdapter);
    }


    @Override
    public void onClick(View source) {
        if(source==clearButton){
            startTimeET.setText("");
            endTimeET.setText("");
            startTime="";
            endTime="";
            majorSpinner.setSelection(0);
            levelSpinner.setSelection(0);
            selectedMajor=majors.get(0).replaceAll("[^0-9]", "");
            selectedLevel="";


        }
        if(source == searchButton){
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (!(networkInfo != null && networkInfo.isConnected())) {
                Toast.makeText(getApplicationContext(),getString(R.string.no_internet),Toast.LENGTH_SHORT).show();
                return;
            }
            startTime = startTimeET.getText().toString();
            endTime = endTimeET.getText().toString();
            if(startTime.equals("") && endTime.equals("") && selectedLevel.equals("") && selectedMajor.equals("")){
                Toast.makeText(getApplicationContext(),getString(R.string.no_selections_made),Toast.LENGTH_SHORT).show();
            }
            else {
                if(startTime.length()==5)
                    startTime=startTime.substring(0,2)+startTime.substring(3);
                if(endTime.length()==5)
                    endTime=endTime.substring(0,2)+endTime.substring(3);
                String url = getString(R.string.course_id_list);
                int numberOfParameters = 0;

                url = url + "subjectid=" + selectedMajor;
                numberOfParameters++;

                if (!selectedLevel.equals("")) {
                    if (numberOfParameters > 0)
                        url = url + "&level=" + selectedLevel;
                    else
                        url = url + "level=" + selectedLevel;
                    numberOfParameters++;
                }
                if (!startTime.equals("")) {
                    if (numberOfParameters > 0)
                        url = url + "&starttime=" + startTime;
                    else
                        url = url + "starttime=" + startTime;
                        numberOfParameters++;
                }
                if (!endTime.equals("")) {
                    if (numberOfParameters > 0)
                        url = url + "&endtime=" + endTime;
                    else
                        url = url + "endtime=" + endTime;
                    numberOfParameters++;
                }

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {

                            String courseIdList = response.toString();
                            courseIdList = courseIdList.substring(1,courseIdList.length()-1);
                            courseIds.clear();

                            if(!courseIdList.replaceAll(" ","").equals("") )
                            {

                                try {
                                    JSONObject data = new JSONObject();
                                    data.put("classids", response);
                                    String classDetailsUrl = getString(R.string.class_details_post);

                                    CustomPostJsonArrayRequest customPostJsonArrayRequest = new CustomPostJsonArrayRequest(Request.Method.POST, classDetailsUrl, data,
                                            new Response.Listener<JSONArray>() {
                                                @Override
                                                public void onResponse(JSONArray response) {

                                                    try {
                                                        for (int i = 0; i < response.length(); i++) {
                                                            JSONObject classDetail = response.getJSONObject(i);
                                                            String classNameAndId = classDetail.getString("title");
                                                            classNameAndId = classDetail.getString("id") + " " + classNameAndId;
                                                            courseIds.add(classNameAndId);
                                                        }

                                                        Intent courseIdListDisplay = new Intent(getApplicationContext(),CourseListDisplay.class);
                                                        courseIdListDisplay.putExtra("already registered course","no");
                                                        courseIdListDisplay.putExtra("redid",redId);
                                                        courseIdListDisplay.putExtra("password",password);
                                                        courseIdListDisplay.putExtra("in waitlist","no");
                                                        courseIdListDisplay.putStringArrayListExtra("list",courseIds);
                                                        startActivity(courseIdListDisplay);
                                                    } catch (JSONException e) {
                                                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                                        e.printStackTrace();
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                                                    error.printStackTrace();
                                                }

                                            }
                                    );
                                    RequestVolleyQueue.getInstance(getApplicationContext()).add(customPostJsonArrayRequest);
                                }
                                catch(Exception e){
                                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(getApplicationContext(),getString(R.string.empty_list),Toast.LENGTH_SHORT).show();
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
            RequestVolleyQueue.getInstance(getApplicationContext()).add(jsonArrayRequest);

            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        if(adapterView==majorSpinner){
            selectedMajor = majors.get((int)id).toString();
            selectedMajor=selectedMajor.replaceAll("[^0-9]", "");
        }
        else if(adapterView == levelSpinner){
            selectedLevel = levels.get((int)id).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
