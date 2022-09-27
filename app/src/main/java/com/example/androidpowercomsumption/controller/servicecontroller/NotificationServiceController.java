package com.example.androidpowercomsumption.controller.servicecontroller;

import android.util.Log;
import com.example.androidpowercomsumption.utils.monitor.LogFileWriter;
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
        notificationServiceHooker.sHookHelper.doUnHook();
        Log.d(TAG, "NotificationServiceController: createChannelTime: " + notificationServiceHooker.getCreateChannelTime());
        LogFileWriter.write("创建通知的次数:" + notificationServiceHooker.getCreateChannelTime());
        Log.d(TAG, "NotificationServiceController: notifyTime: " + notificationServiceHooker.getNotifyTime());
        LogFileWriter.write("通知的次数:" + notificationServiceHooker.getNotifyTime());
    }
}
