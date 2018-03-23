package com.example.nakulnarwaria.personalinformation;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MajorSelection extends AppCompatActivity implements MajorOptionsFragment.ChoiceMadeForDegree{

    private String listName="Advanced_Degrees";
    private String selectedMajor="";
    private static int numberOfListOptionsChosen=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_major_selection);
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            MajorOptionsFragment advancedDegreeFragment= new MajorOptionsFragment();
            Bundle args=new Bundle();
            args.putStringArrayList("content",readFile(listName));
            advancedDegreeFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, advancedDegreeFragment);
            transaction.commit();
        }
    }

    public void majorChosen(String major){
        numberOfListOptionsChosen++;
        ArrayList<String> degreeList = readFile("Advanced_Degrees");
        String abbreviationOfDegree="";
        if(degreeList.contains(major)) {
            try {
                if (major.contains("(")) {
                    abbreviationOfDegree = major.split(" ")[major.split(" ").length - 1];
                    //Removing brackets
                    abbreviationOfDegree = abbreviationOfDegree.substring(1, abbreviationOfDegree.length() - 1);
                }
                listName = major;
                if (!abbreviationOfDegree.equals(""))
                    selectedMajor = abbreviationOfDegree;
                else
                    selectedMajor = major;
            }
            catch(Exception e){
                selectedMajor = major;
            }
                numberOfListOptionsChosen = 1;
        }
        else{
            selectedMajor=selectedMajor+" "+major+" ";
        }


        if(numberOfListOptionsChosen == 1)
        {
            Log.d("REACHED HERE","true");
            MajorOptionsFragment majorSelection = new MajorOptionsFragment();
            Bundle args=new Bundle();
            args.putStringArrayList("content",readFile(listName));
            majorSelection.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, majorSelection);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        if(numberOfListOptionsChosen==2)
        {
            Intent majorIntent = getIntent();
            majorIntent.putExtra("Major",selectedMajor);
            setResult(RESULT_OK,majorIntent);
            finish();
        }
    }

    public ArrayList<String> readFile(String fileName){
        ArrayList<String> listToDisplay = new ArrayList<String>();
        try {
            InputStream listToFetch = getAssets().open(fileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(listToFetch));
            String listItem;
            while ((listItem = in.readLine()) != null)
                listToDisplay.add(listItem);

        }
        catch(IOException e)
        {
            Log.d("Error in list passing",fileName);
        }
        return listToDisplay;
    }

}
