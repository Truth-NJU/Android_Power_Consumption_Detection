package com.example.androidpowercomsumption.controller;

import android.util.Log;
import com.example.androidpowercomsumption.utils.systemservice.hooker.NotificationServiceHooker;

public class NotificationServiceController {
    private final String TAG = "NotificationService";

    private NotificationServiceHooker notificationServiceHooker;

    public NotificationServiceController(NotificationServiceHooker notificationServiceHooker) {
        this.notificationServiceHooker = notificationServiceHooker;
    }

    public void start() {
        notificationServiceHooker.sHookHelper.doHook();

    }

    public void finish() {
        Log.d(TAG, "createChannelTime: " + notificationServiceHooker.getCreateChannelTime());
        Log.d(TAG, "notifyTime: " + notificationServiceHooker.getNotifyTime());
    }
}
