package com.example.lab_1;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MainFragment extends Fragment {
    IListFunction listFunction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View inf = inflater.inflate(R.layout.fragment_main, null);
        Button btnAdd = inf.findViewById(R.id.buttonAdd);
        btnAdd.setOnClickListener(view -> listFunction.Create());
        Button btnEdit = inf.findViewById(R.id.buttonEdit);
        btnEdit.setOnClickListener(view -> listFunction.Edit());
        ((Button) (inf.findViewById(R.id.buttonRemove))).setOnClickListener(view -> listFunction.Delete());
        Button btnSearch = inf.findViewById(R.id.buttonSearch);
        btnSearch.setOnClickListener(view -> listFunction.Search());
        //((Button) (inf.findViewById(R.id.buttonSearch))).setOnClickListener(view -> listFunction.Search( ((TextView) inf.findViewById(R.id.textField)).getText().toString()));
        return inf;
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