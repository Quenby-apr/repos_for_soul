package com.example.lab_1;

import android.app.Activity;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


public class fragment_storage extends Fragment {

    IListFunction listFunction;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_storage, null);
        Button btnDB = v.findViewById(R.id.buttonDB);
        btnDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    listFunction.SetDB();
                 }
        });
        Button btnFile = v.findViewById(R.id.buttonFile);
        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listFunction.SetFile();
            }
        });
        return v;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listFunction = (IListFunction) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}