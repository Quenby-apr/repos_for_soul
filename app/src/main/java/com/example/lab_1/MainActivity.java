package com.example.lab_1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    Button btnAdd;
    public static final List<String> names = new ArrayList<String>(Arrays.asList("Курс по русскому", "Курс по математике", "Курс по физике", "Курс по английскому", "Курс по химии", "Курс по информатике"));;
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
        Button btnRemove = findViewById(R.id.buttonRemove);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

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
                for(int i = 0; i<size; i++)
                {
                    listView.setItemChecked(i, true);
                }
                adapter.notifyDataSetChanged();
            }
        };
        View.OnClickListener oclbtnCancel = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = adapter.getCount();
                for(int i = 0; i<size; i++)
                {
                    listView.setItemChecked(i, false);
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
        View.OnClickListener oclbtnRemove = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                int size = adapter.getCount();

                List<String> removeList = IntStream.range(0, size)
                        .filter(i -> listView.isItemChecked(i))
                        .mapToObj(adapter::getItem)
                        .collect(Collectors.toList());

                names.removeAll(removeList);
                for(int i = 0; i<size; i++)
                {
                    listView.setItemChecked(i, false);
                }
                adapter.notifyDataSetChanged();
            }
        };

        btnAdd.setOnClickListener(oclbtnAdd);
        btnSelect.setOnClickListener(oclbtnSelect);
        btnCancel.setOnClickListener(oclbtnCancel);
        btnToast.setOnClickListener(oclbtnToast);
        btnRemove.setOnClickListener(oclbtnRemove);
        listView.setAdapter(adapter);
    }
    /*
    View.OnClickListener oclbtnRemove = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                int size = adapter.getCount();

                List<String> removeList = IntStream.range(0, size)
                        .filter(i -> listView.isItemChecked(i))
                        .mapToObj(adapter::getItem)
                        .collect(Collectors.toList());

                names.removeAll(removeList);
                for(int i = 0; i<size; i++)
                {
                    listView.setItemChecked(i, false);
                }
                adapter.notifyDataSetChanged();
            }
        };
     */
}