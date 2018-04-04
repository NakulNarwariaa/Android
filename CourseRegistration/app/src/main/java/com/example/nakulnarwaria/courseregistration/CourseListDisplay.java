package com.example.nakulnarwaria.courseregistration;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class CourseListDisplay extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ArrayList<String> content;
    private ListView courseList;
    private String alreadyRegistered;
    private String redId;
    private String password;
    private String inWaitList;
    private final int ADD_OR_DROP_CLASS=54321;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list_display);
        courseList = findViewById(R.id.courseList);
        content = getIntent().getStringArrayListExtra("list");
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,content );
        courseList.setAdapter(adapter);
        courseList.setOnItemClickListener(this);
        alreadyRegistered = getIntent().getStringExtra("already registered course");
        redId = getIntent().getStringExtra("redid");
        password = getIntent().getStringExtra("password");
        inWaitList = getIntent().getStringExtra("in waitlist");
        if(alreadyRegistered.equals("no")){
            getSupportActionBar().setTitle(getString(R.string.available_courses));
        }
        else if(alreadyRegistered.equals("yes") && inWaitList.equals("no"))
        {
            getSupportActionBar().setTitle(getString(R.string.my_classes));
        }
        else{
            getSupportActionBar().setTitle(getString(R.string.my_wait_list));
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.e("Chosen : ",content.get((int)id));
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (!(networkInfo != null && networkInfo.isConnected())) {
             Toast.makeText(getApplicationContext(),getString(R.string.no_internet),Toast.LENGTH_SHORT).show();
             return;
        }
        Intent courseDetails = new Intent(this,CourseDetails.class);
        courseDetails.putExtra("course number",content.get((int)id).toString());
        courseDetails.putExtra("redid",redId);
        courseDetails.putExtra("password",password);
        courseDetails.putExtra("in waitlist",inWaitList);
        courseDetails.putExtra("already registered course",alreadyRegistered);
        startActivityForResult(courseDetails,ADD_OR_DROP_CLASS);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode!=ADD_OR_DROP_CLASS){
            return;
        }
        switch(resultCode){
            case RESULT_OK:
                try{
                    String returnedCourse = data.getExtras().getString("course number");
                    if(!data.getExtras().getString("course add or drop").equals("add"))
                        content.remove(returnedCourse);

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,content );
                    courseList.setAdapter(adapter);
                    courseList.setOnItemClickListener(this);
                    if(content.isEmpty())
                        finish();
                    break;


                }
                catch(NullPointerException e){
                    Log.d("No List Found","List Missing");
                }
            case RESULT_CANCELED:
                break;
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
}
