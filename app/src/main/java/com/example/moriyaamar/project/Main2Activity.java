package com.example.moriyaamar.project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener, FragNewListNameDialog.ListNameListener, FragStartScreen.StartFragmentInteractionListener, FragAbout.OnFragmentInteractionListener {
    private Button newListBtn, editListBtn;
    private String uniqueId;
    private final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE=1;
    private final int MY_PERMISSIONS_REQUEST_READ_SMS=2;
    private final int MY_PERMISSIONS_WAKE_LOCK=3;
    private DatabaseReference appDatabase;
    private static final String FIREBASE_URL = "https://ilistproject.firebaseio.com/", LISTS = "lists";     //necessary?
    private int state=1;
    private SmsBroadcastReceiver smsReceiver;
    private IntentFilter smsFilter;
    private final String FRAG_ABOUT="FRAG_ABOUT";
    private boolean clicked=false, permissions=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        TextView learnTextView = findViewById(R.id.learnTextView);
        learnTextView.setPaintFlags(learnTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        learnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!clicked) {
                    FragAbout fragAbout = new FragAbout();
                    FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction().
                            add(R.id.aboutFrameLayoutContainer, fragAbout, FRAG_ABOUT).addToBackStack(null);
                    fragTrans.commit();
                    clicked = true;
                } else {
                    clicked = false;
                    FragAbout fragAbout = new FragAbout();
                    FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction();
                    fragTrans.hide(getSupportFragmentManager().findFragmentByTag(FRAG_ABOUT));
                    fragTrans.commit();
                }
            }
        });

        /*Show application start for 3 seconds*/
        newListBtn = (Button) findViewById(R.id.addListBtn);
        editListBtn = (Button) findViewById(R.id.editListBtn);


        smsFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);              //Prepare IntentFilter for the SMS BroadcastReceiver
        smsFilter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);                     //
        smsFilter.setPriority(999);                                                         //

        appDatabase = FirebaseDatabase.getInstance().getReference();

        if (ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {       //Check app permissions
            Context AppContext = getApplicationContext();//
            permissions=true;
            TelephonyManager tMgr = (TelephonyManager) AppContext.getSystemService(Context.TELEPHONY_SERVICE);                                              //
            uniqueId = tMgr.getDeviceId();
            smsReceiver = new SmsBroadcastReceiver(uniqueId);                                           //Creating new BroadcastReceiver
        } else
            ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);      //No permissions found, request permissions

 /*       if (ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_DENIED)                     //Check other app permissions
            this.registerReceiver(smsReceiver, smsFilter);                                                                                                   //Register the BroadcastReceiver if permissions granted
        else
            ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.READ_SMS}, MY_PERMISSIONS_REQUEST_READ_SMS);     */   //No permissions found, request permissions

        if(permissions)
            startApp();
          /*  appDatabase.child("users").child(uniqueId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //User already exits
                    } else {
                        appDatabase.child("users").child(uniqueId).setValue("Grinch");      //Change Grinch to username requested from user - Optional, not critical
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            newListBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new FragNewListNameDialog().show(getSupportFragmentManager(), null);
                }
            });

            editListBtn.setOnClickListener(this);*/
        }

    private void startApp() {

        if (ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_DENIED)                     //Check other app permissions
            this.registerReceiver(smsReceiver, smsFilter);                                                                                                   //Register the BroadcastReceiver if permissions granted
        else
            ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.READ_SMS}, MY_PERMISSIONS_REQUEST_READ_SMS);        //No permissions found, request permissions


        appDatabase.child("users").child(uniqueId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //User already exits
                } else {
                    appDatabase.child("users").child(uniqueId).setValue("Grinch");      //Change Grinch to username requested from user - Optional, not critical
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        newListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new FragNewListNameDialog().show(getSupportFragmentManager(), null);
            }
        });

        editListBtn.setOnClickListener(this);
    }

    @Override
        public void onClick (View v){
            state = 2;
            Intent myIntent = new Intent(Main2Activity.this, EditListActivity.class);
            myIntent.putExtra("UID", uniqueId);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Main2Activity.this.startActivity(myIntent);                                 //Load edit list activity here*/
        }

        @Override
        public void onListNameApproved ( final String name){

            appDatabase.child(LISTS).child(uniqueId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(name)) {
                        Intent myIntent = new Intent(Main2Activity.this, NewListActivity.class);
                        myIntent.putExtra("LIST_NAME", name);                       //pass parameters for the new activity
                        myIntent.putExtra("FIREBASE_URL", FIREBASE_URL);            //
                        myIntent.putExtra("UID", uniqueId);                         //
                        myIntent.putExtra("STATE", state);                          //
                        Main2Activity.this.startActivity(myIntent);                       //Load new list activity here*/
                    } else
                        Toast.makeText(getApplicationContext(), "Another list with the same name already exists", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    /**Check permission in order to obtain unique user ID*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE:
                if (ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    Context AppContext = getApplicationContext();
                    this.permissions=true;
                    TelephonyManager tMgr = (TelephonyManager)AppContext.getSystemService(Context.TELEPHONY_SERVICE);
                    uniqueId = tMgr.getDeviceId();
                    smsReceiver = new SmsBroadcastReceiver(uniqueId);                                           //Creating new BroadcastReceiver
                    startApp();
                }
                break;
            case MY_PERMISSIONS_REQUEST_READ_SMS:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    this.registerReceiver(smsReceiver, smsFilter);
                }
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
