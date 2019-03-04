package com.example.moriyaamar.project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, FragNewListNameDialog.ListNameListener {
    private Button newListBtn, editListBtn;
    private String uniqueId;
    private final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE=1;
    private DatabaseReference appDatabase;
    private static final String FIREBASE_URL = "https://ilistproject.firebaseio.com/";              //??????

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appDatabase = FirebaseDatabase.getInstance().getReference();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            Context AppContext = getApplicationContext();
            TelephonyManager tMgr = (TelephonyManager) AppContext.getSystemService(Context.TELEPHONY_SERVICE);
            uniqueId = tMgr.getDeviceId();
        }
        else
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

        appDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //User already exits
                } else {
                    appDatabase.child("users").child(uniqueId).setValue("Grinch");      //Change Grinch to username requested from user
                    appDatabase.child("lists").setValue(uniqueId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        newListBtn = (Button)findViewById(R.id.addListBtn);
        editListBtn = (Button)findViewById(R.id.editListBtn);

        newListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new FragNewListNameDialog().show(getSupportFragmentManager(),null);
            }
        });

        editListBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent myIntent = new Intent(MainActivity.this, EditListActivity.class);
        MainActivity.this.startActivity(myIntent);              /*Load edit list activity here*/
    }

    @Override
    public void onListNameApproved(String name) {

        Intent myIntent = new Intent(MainActivity.this, NewListActivity.class);
        myIntent.putExtra("LIST_NAME", name);       //pass ListName
        myIntent.putExtra("FIREBASE_URL", FIREBASE_URL);
        myIntent.putExtra("UID", uniqueId);
        MainActivity.this.startActivity(myIntent);              /*Load new list activity here*/
    }

    /**Check permission in order to obtain unique user ID*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE:
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    Context AppContext = getApplicationContext();
                    TelephonyManager tMgr = (TelephonyManager)AppContext.getSystemService(Context.TELEPHONY_SERVICE);
                    uniqueId = tMgr.getDeviceId();
                }
        }
    }
}
