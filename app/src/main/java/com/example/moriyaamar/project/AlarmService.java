package com.example.moriyaamar.project;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Calendar;

public class AlarmService extends Service {
    private long alarmTime;

    AlarmBroadcastReceiver alarm = new AlarmBroadcastReceiver();
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        alarmTime = intent.getExtras().getLong("CAL");

        Thread thread = new Thread(){
            @Override
            public void run() {
                try{
                    alarm.setAlarmTime(alarmTime);
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