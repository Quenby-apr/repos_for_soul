package com.example.lab_1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    Button btnAdd;
    List<String> names;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        EditText textField = findViewById(R.id.textfield);
        btnAdd = findViewById(R.id.buttonAdd);
        Button btnSelect = findViewById(R.id.buttonSelect);
        Button btnCancel = findViewById(R.id.buttonCancel);
        Button btnToast = findViewById(R.id.buttonToast);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        names = new ArrayList<>();

        ArrayAdapter<String> adapter =new ArrayAdapter(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                names);

        View.OnClickListener oclbtnAdd = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String elem = textField.getText().toString();
                names.add(elem);
                adapter.notifyDataSetChanged();
            }
        };
        View.OnClickListener oclbtnSelect = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = adapter.getCount();
                if(!listView.isItemChecked(0))
                {
                    for(int i = 0; i<size; i++)
                    {
                        listView.setItemChecked(i, true);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        };
        View.OnClickListener oclbtnCancel = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = adapter.getCount();
                if(listView.isItemChecked(0))
                {
                    for(int i = 0; i<size; i++)
                    {
                        listView.setItemChecked(i, false);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        };
        View.OnClickListener oclbtnToast = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = adapter.getCount();
                StringBuilder res = new StringBuilder();
                for(int i = 0; i<size; i++)
                {
                    if (listView.isItemChecked(i)) {
                        res.append(adapter.getItem(i) + " ");
                    }
                }
                Toast.makeText(getApplicationContext(), res.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        btnAdd.setOnClickListener(oclbtnAdd);
        btnSelect.setOnClickListener(oclbtnSelect);
        btnCancel.setOnClickListener(oclbtnCancel);
        btnToast.setOnClickListener(oclbtnToast);
        listView.setAdapter(adapter);
    }
}