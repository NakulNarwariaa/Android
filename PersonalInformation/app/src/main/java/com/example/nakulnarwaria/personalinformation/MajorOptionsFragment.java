package com.example.nakulnarwaria.personalinformation;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MajorOptionsFragment extends ListFragment implements AdapterView.OnItemClickListener {

    private ArrayList<String> content;
    private String result;

    public MajorOptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        content = getArguments() != null ? getArguments().getStringArrayList("content"):null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View majorOptions = inflater.inflate(R.layout.fragment_major_options, container, false);
        return majorOptions;
    }

    public interface ChoiceMadeForDegree{
        public void majorChosen(String major);
      }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,content );
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ChoiceMadeForDegree choiceListner = (ChoiceMadeForDegree) getActivity();
        choiceListner.majorChosen(content.get((int)id));
    }

}
