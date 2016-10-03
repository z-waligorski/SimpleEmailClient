package com.z_waligorski.simpleemailclient.Service;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

public class StartInboxService extends BroadcastReceiver{

    private boolean refreshing = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, InboxService.class);
        context.startService(i);
    }

    // Setup AlarmManager
    public void setAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 1);

        // Get refreshing interval from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long refreshingTime = 0;
        String refreshingInterval = sharedPreferences.getString("INTERVAL", "");
        switch(refreshingInterval) {
            case "no_refreshing":
                refreshing = false;
                break;
            case "15":
                refreshingTime = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
                break;
            case "60":
                refreshingTime = AlarmManager.INTERVAL_HOUR;
                break;
            case "120":
                refreshingTime = AlarmManager.INTERVAL_HOUR * 2;
                break;
            case "360":
                refreshingTime = AlarmManager.INTERVAL_HALF_DAY / 2;
                break;
            default:
                refreshingTime = AlarmManager.INTERVAL_HOUR;
        }

        Intent intent = new Intent(context, StartInboxService.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if(refreshing == false) {
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT).cancel();
        } else {

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), refreshingTime,
                    pendingIntent);
        }
    }
}