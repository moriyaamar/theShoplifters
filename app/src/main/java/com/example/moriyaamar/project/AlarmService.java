package com.example.moriyaamar.project;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.Serializable;
import java.util.Calendar;

public class AlarmService extends Service {
    private int year, day, month, hour, minute;

    AlarmBroadcastReceiver alarm = new AlarmBroadcastReceiver();
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.year = intent.getExtras().getInt("YEAR");
        this.month = intent.getExtras().getInt("MONTH");
        this.day = intent.getExtras().getInt("DAY");
        this.hour = intent.getExtras().getInt("HOUR");
        this.minute = intent.getExtras().getInt("MINUTE");

        Thread thread = new Thread(){
            @Override
            public void run() {
                try{
                    alarm.setAlarmTime(year, month, day, hour, minute);
                    alarm.setAlarm(getApplicationContext());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        thread.start();
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}