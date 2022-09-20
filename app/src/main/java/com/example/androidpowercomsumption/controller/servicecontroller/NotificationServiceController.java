package com.example.androidpowercomsumption.controller.servicecontroller;

import android.util.Log;
import com.example.androidpowercomsumption.utils.systemservice.hooker.NotificationServiceHooker;

public class NotificationServiceController {
    private final String TAG = "ServiceController";

    private NotificationServiceHooker notificationServiceHooker;

    public NotificationServiceController(NotificationServiceHooker notificationServiceHooker) {
        this.notificationServiceHooker = notificationServiceHooker;
    }

    public void start() {
        notificationServiceHooker.sHookHelper.doHook();

    }

    public void finish() {
        Log.d(TAG, "NotificationServiceController: createChannelTime: " + notificationServiceHooker.getCreateChannelTime());
        Log.d(TAG, "NotificationServiceController: notifyTime: " + notificationServiceHooker.getNotifyTime());
    }
}
