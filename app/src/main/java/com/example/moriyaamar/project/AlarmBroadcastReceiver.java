package com.example.moriyaamar.project;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "myChannel";
    private long timeInMillis;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        createNotificationChannel(context);                                                                 //sending push notification when alarm reached wanted time

        Intent intent1 = new Intent(context, AlarmBroadcastReceiver.class);                                 //
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);                   //
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);    //

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)            //
                .setSmallIcon(R.drawable.icon)                                                              //icon to show with notification
                .setContentTitle("iList")                                                                   //notification title
                .setContentText("It's time for you to do some shopping")                                    //notification message
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)                                           //notification priority
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)                                                            //
                .setAutoCancel(true);                                                                       //


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);            //
        notificationManager.notify(1, builder.build());                                                 //

//               NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.icon).
//                setContentTitle("Shopping time alarm").setContentText("It's for you to do some shopping with iList").setPriority(Notification.PRIORITY_DEFAULT);
//        Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show();      // For example

    }

    private void createNotificationChannel(Context context) {                                   //function to initialize the notification
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "iListChannel";
            String description = "This is a description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public void setAlarmTime(long time){
        this.timeInMillis = time;
    }

    public void setAlarm(Context context) {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, i, 0);

//        Calendar cal = Calendar.getInstance();
//        int offset = cal.getTimeZone().getOffset(cal.getTimeInMillis());
//        cal.set(2019, 03, 11, 03, 22);

        am.setExact(AlarmManager.RTC_WAKEUP, /*cal.getTimeInMillis()+offset*/ timeInMillis, pendingIntent);           //How much time until you show the alarm
    }

    public void cancelAlarm(Context context){
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

}