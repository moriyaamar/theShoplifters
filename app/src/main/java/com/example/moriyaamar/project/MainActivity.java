package com.example.moriyaamar.project;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements FragStartScreen.StartFragmentInteractionListener {
    private final String FRAG_START_TAG="FRAGSTART";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState!=null){


        }

        setContentView(R.layout.activity_main);

        FragStartScreen fragStrart = new FragStartScreen();

        FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction().
                add(R.id.startfragment, fragStrart,FRAG_START_TAG).addToBackStack(null);
        fragTrans.commit();

        getSupportFragmentManager().executePendingTransactions();


        fragTrans = getSupportFragmentManager().beginTransaction().hide(fragStrart);
        fragTrans.commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
