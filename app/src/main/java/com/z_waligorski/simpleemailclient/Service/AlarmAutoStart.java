package com.z_waligorski.simpleemailclient.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// This class starts service after rebooting
public class AlarmAutoStart extends BroadcastReceiver {

    StartInboxService startInboxService = new StartInboxService();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            startInboxService.setAlarm(context);
        }
    }
}
