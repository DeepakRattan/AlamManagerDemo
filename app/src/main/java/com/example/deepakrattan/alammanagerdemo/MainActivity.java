package com.example.deepakrattan.alammanagerdemo;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    private ToggleButton toggleAlarm;
    private String toastMessage;
    private NotificationManager notificationManager;
    private Notification notification;
    private NotificationCompat.Builder builder;
    private static final int NOTIFICATION_ID = 0;
    private Intent notificationIntent;
    private PendingIntent notificationPendingIntent;
    private AlarmManager alarmManager;
    private static final String ACTION_NOTIFY = "com.example.deepakrattan.alammanagerdemo.ACTION_NOTIFY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        //Setup the notification Broadcast Intent
        //Intent notifyIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        Intent notifyIntent = new Intent(ACTION_NOTIFY);

        //Check if the Alarm is already set, and check the toggle accordingly
        boolean alarmUp = (PendingIntent.getBroadcast(this, 0, notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);
        toggleAlarm.setChecked(alarmUp);

        //Set up the PendingIntent for the AlarmManager
        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        toggleAlarm = (ToggleButton) findViewById(R.id.toggleAlarm);

        toggleAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //deliverNotification(MainActivity.this);

                    long triggerTime = SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES;
                    long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;

                    //If the Toggle is turned on, set the repeating alarm with a 15 minute interval
                    //alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 60 * 1000, notifyPendingIntent);
                    alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, repeatInterval, notifyPendingIntent);

                    toastMessage = getString(R.string.alarm_on_toast);
                } else {
                    //Cancel the alarm and notification if the alarm is turned off
                    alarmManager.cancel(notifyPendingIntent);
                    notificationManager.cancelAll();
                    toastMessage = getString(R.string.alarm_off_toast);
                }
                Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Creating a simple notification
    public void deliverNotification(Context context) {
        notificationIntent = new Intent(context, MainActivity.class);
        notificationPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Build the notification
        builder = new NotificationCompat.Builder(context)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setSmallIcon(R.mipmap.ic_alert)
                .setContentIntent(notificationPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        notification = builder.build();

        //Deliver the notification
        notificationManager.notify(NOTIFICATION_ID, notification);


    }
}
