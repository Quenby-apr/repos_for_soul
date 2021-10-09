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
import android.widget.TextView;

import java.util.ArrayList;


public class ElementFragment extends Fragment {

    IListFunction listFunction;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_element, null);
        Commands command = (Commands) getArguments().getSerializable("command");
        Button btnOk = v.findViewById(R.id.buttonOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double number;
                try {
                    number = Double.parseDouble(((EditText) v.findViewById(R.id.elementTextFieldNumber)).getText().toString());
                }
                catch(Exception e) {
                    number = 0;
                }
                listFunction.Ok(command, ((EditText) v.findViewById(R.id.elementTextFieldName)).getText().toString(), number, ((CheckBox) v.findViewById(R.id.checkBox)).isChecked());
            }
        });
        Button btnCancel = v.findViewById(R.id.buttonCancel);
        btnCancel.setOnClickListener(view -> getActivity().getFragmentManager().popBackStack());
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