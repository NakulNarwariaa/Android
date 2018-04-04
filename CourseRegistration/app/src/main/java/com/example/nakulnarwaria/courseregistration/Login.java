package com.example.nakulnarwaria.courseregistration;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private Button existingUserButton;
    private Button newUserButton;
    private Button loginNewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        existingUserButton = findViewById(R.id.existingUserButton);
        newUserButton = findViewById(R.id.loginNewButton);
        existingUserButton.setOnClickListener(this);
        newUserButton.setOnClickListener(this);
        getSupportActionBar().setTitle(getString(R.string.sdsu_login));
    }

    public void onClick(View source) {
        if(source == newUserButton){
            Intent newUser = new Intent(this,StudentProfile.class);
            newUser.putExtra("New User","true");
            startActivity(newUser);
        }

        if(source == existingUserButton){
            Intent existingUser = new Intent(this,UserChange.class);
            existingUser.putExtra("New User","false");
            startActivity(existingUser);
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
