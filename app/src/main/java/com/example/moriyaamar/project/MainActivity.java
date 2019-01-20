package com.example.moriyaamar.project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button newListBtn, editListBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newListBtn = (Button)findViewById(R.id.addListBtn);
        editListBtn = (Button)findViewById(R.id.editListBtn);

        newListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.new_list_activity);        /*Load new list activity here*/
            }
        });

        editListBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        //setContentView(R.Layout. )                /*Load edit list activity here*/
    }

}
