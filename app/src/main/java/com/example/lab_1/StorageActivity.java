package com.example.lab_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class StorageActivity extends AppCompatActivity {

    public Intent chooseActivity (Class param) {
        Intent intent = new Intent(this, param);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        Button btnDB = findViewById(R.id.buttonDB);
        btnDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = chooseActivity(MainActivity.class);
                intent.putExtra("storage", "DB");
                startActivity(intent);
            }
        });
        Button btnFile = findViewById(R.id.buttonFile);
        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = chooseActivity(MainActivity.class);
                intent.putExtra("storage", "File");
                startActivity(intent);
            }
        });
    }
}