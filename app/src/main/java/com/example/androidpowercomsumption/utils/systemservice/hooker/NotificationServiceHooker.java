package com.example.androidpowercomsumption.utils.systemservice.hooker;

import android.content.Context;
import android.util.Log;
import androidx.annotation.Nullable;

import java.lang.reflect.Method;

public class NotificationServiceHooker {
    private static final String TAG = "NotificationService";

    private int createChannelTime;

    private int notifyTime;

    public int getCreateChannelTime() {
        return createChannelTime;
    }

    public void setCreateChannelTime(int createChannelTime) {
        this.createChannelTime = createChannelTime;
    }

    public int getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(int notifyTime) {
        this.notifyTime = notifyTime;
    }

    private ServiceHookCallback sHookCallback = new ServiceHookCallback() {
        @Override
        public void serviceMethodInvoke(Method method, Object[] args) {
            if ("createNotificationChannels".equals(method.getName())) {
                createChannelTime++;
                Log.d(TAG, "createChannelTime++");
            } else if ("enqueueNotificationWithTag".equals(method.getName())) {
                notifyTime++;
                Log.d(TAG, "notifyTime++;");
            }
        }

        @Nullable
        @Override
        public Object serviceMethodIntercept(Object receiver, Method method, Object[] args) throws Throwable {
            return null;
        }
    };

    public SystemServiceHooker sHookHelper = new SystemServiceHooker(Context.NOTIFICATION_SERVICE, "android.app.INotificationManager", sHookCallback);

}
