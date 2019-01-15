package com.example.moriyaamar.project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button newListBtn, editListBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newListBtn = (Button)findViewById(R.id.addListBtn);
        editListBtn = (Button)findViewById(R.id.editListBtn);
    }

}
