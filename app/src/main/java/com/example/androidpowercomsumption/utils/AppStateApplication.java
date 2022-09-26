package com.example.androidpowercomsumption.utils;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import com.example.androidpowercomsumption.controller.*;
import com.example.androidpowercomsumption.controller.servicecontroller.*;
import com.example.androidpowercomsumption.diff.ThreadConsumptionDiff;
import com.example.androidpowercomsumption.utils.systemservice.SimulateSystemService;
import com.example.androidpowercomsumption.utils.systemservice.hooker.*;

import java.util.List;


public class AppStateApplication extends Application {
    private static final String TAG = "AppStateController";

    private final DeviceStateListener listener = new DeviceStateListener(this);

    private final DeviceStateController deviceStateController = new DeviceStateController();

    @Override
    public void onCreate() {
        super.onCreate();
        //注册自己的Activity的生命周期回调接口。
        registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks(this.deviceStateController, this));
        /**
         * 注册监听设备状态
         */
        listener.register(new DeviceStateListener.ScreenStateListener() {

            boolean isFirst = true; // 第一次启动

            // 设备状态监控
            @Override
            public void onScreenOn() {
                Log.d(TAG + "Device", "屏幕点亮");
                if (isFirst) {
                    isFirst = false;
                    deviceStateController.start();
                    deviceStateController.status = true; // 亮屏状态
                    deviceStateController.curStatusStartTime = deviceStateController.startTime;
                } else {
                    if (!deviceStateController.status) { // 息屏进入亮屏
                        deviceStateController.status = true;
                        deviceStateController.curStatusEndTime = System.currentTimeMillis(); // 息屏状态的结束时间
                        deviceStateController.screenOffTime += (deviceStateController.curStatusEndTime - deviceStateController.curStatusStartTime);
                        deviceStateController.curStatusStartTime = System.currentTimeMillis(); // 息屏进入亮屏，亮屏状态的开始时间
                    }
                }
            }

            @Override
            public void onScreenOff() {
                Log.d(TAG + "Device", "屏幕熄灭");
                if (deviceStateController.status) { // 亮屏进入息屏
                    deviceStateController.status = false;
                    deviceStateController.curStatusEndTime = System.currentTimeMillis(); // 亮屏状态的结束时间
                    deviceStateController.screenOnTime += (deviceStateController.curStatusEndTime - deviceStateController.curStatusStartTime);
                    deviceStateController.curStatusStartTime = System.currentTimeMillis(); // 亮屏进入息屏，息屏状态的开始时间

                }
            }

            @Override
            public void onUserPresent() {

            }

            boolean isFirstCharge = true; // 第一次充电

            boolean isCharge = false;

            @Override
            public void onPowerConnected() {
                Log.d(TAG + "Device", "开始充电");
                if (isFirstCharge) { // 第一次充电
                    isFirstCharge = false;
                    isCharge = true;
                    deviceStateController.curStatusStartTimeCharge = System.currentTimeMillis();
                } else {
                    if (!isCharge) { // 从不充电变为充电状态
                        isCharge = true;
                        deviceStateController.curStatusEndTimeCharge = System.currentTimeMillis(); // 不充电状态的结束时间
                        deviceStateController.noChargeTime += (deviceStateController.curStatusEndTimeCharge - deviceStateController.curStatusStartTimeCharge);
                        deviceStateController.curStatusStartTimeCharge = System.currentTimeMillis();// 充电状态的开始时间

                    }
                }
            }

            @Override
            public void onPowerDisconnected() {
                Log.d(TAG + "Device", "停止充电");
                if (isCharge) { // 从充电状态变为不充电状态
                    isCharge = false;
                    deviceStateController.curStatusEndTimeCharge = System.currentTimeMillis(); // 充电状态的结束时间
                    deviceStateController.chargeTime += (deviceStateController.curStatusEndTimeCharge - deviceStateController.curStatusStartTimeCharge);
                    deviceStateController.curStatusStartTimeCharge = System.currentTimeMillis(); // 不充电状态的开始时间

                }

            }
        });
    }


    //声明一个监听Activity们生命周期的接口
    static class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {
        private Context context;

//        private final AppStateController appStateController = new AppStateController();
//
//        private ThreadController threadController = new ThreadController();

//        private final DeviceStateController deviceStateController;

        private final TimeMonitor timeMonitor;

//        private final WifiServiceController wifiServiceController = new WifiServiceController(new WifiServiceHooker());
//
//        private final GPSServiceController gpsServiceController = new GPSServiceController(new GPSServiceHooker());
//
//        private final BluetoothServiceController bluetoothServiceController = new BluetoothServiceController(new BluetoothServiceHooker());
//
//        private final AlarmServiceController alarmServiceController = new AlarmServiceController(new AlarmServiceHooker());
//
//        private final NotificationServiceController notificationServiceController = new NotificationServiceController(new NotificationServiceHooker());
//

        public MyActivityLifecycleCallbacks(DeviceStateController deviceStateController, Context context) {
//            this.deviceStateController = deviceStateController;
            this.context = context;
            this.timeMonitor=new TimeMonitor(deviceStateController);
        }

        /**
         * application下的每个Activity声明周期改变时，都会触发以下的函数。
         */
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            timeMonitor.startMonitor();
//            // 只会在启动的时候触发一次
//            appStateController.start();
//            appStateController.status = true; // 前台状态
//            appStateController.curStatusStartTime = appStateController.startTime; // 当前状态的开始时间
//
//            // service hook
//            wifiServiceController.start();
//            gpsServiceController.start();
//            bluetoothServiceController.start();
//            alarmServiceController.start();
//            notificationServiceController.start();
        }

        @Override
        public void onActivityStarted(Activity activity) {
            timeMonitor.onActivityStarted();
            // todo
            SimulateSystemService.wifi(context);
            SimulateSystemService.gps(context);
            SimulateSystemService.bluetooth(context);
            SimulateSystemService.alarm(context);
            SimulateSystemService.notify(context);
//            // 对于线程功耗监控，每次app状态切换都要进行监控
//            threadController = new ThreadController();
//            threadController.start();
//
//            // 前后台运行监控
//            Log.d(TAG, "APP进入前台");
//            if (!appStateController.status) { // 后台进入前台
//                appStateController.status = true;
//                appStateController.curStatusEndTime = System.currentTimeMillis();// 后台状态的结束时间
//                appStateController.backgroundTime += (appStateController.curStatusEndTime - appStateController.curStatusStartTime);
//                appStateController.curStatusStartTime = System.currentTimeMillis();// 后台进入前台，前台状态的开始时间
//
//            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            timeMonitor.onActivityStopped();
            // todo
            SimulateSystemService.wifi(context);
            SimulateSystemService.gps(context);
            SimulateSystemService.bluetooth(context);
            SimulateSystemService.alarm(context);
            SimulateSystemService.notify(context);
//            Log.d(TAG, "App进入后台");
//            if (appStateController.status) { // 由前台进入后台
//                appStateController.status = false;
//                appStateController.curStatusEndTime = System.currentTimeMillis(); // 前台状态的结束时间
//                appStateController.foregroundTime += (appStateController.curStatusEndTime - appStateController.curStatusStartTime);
//                appStateController.curStatusStartTime = System.currentTimeMillis(); // 前台进入后台，后台状态的开始时间
//            }
//
//            threadController.finish();
//            List<ThreadConsumptionDiff.ThreadDiff> threadDiffList = threadController.threadDiffList;
//            for (ThreadConsumptionDiff.ThreadDiff threadDiff : threadDiffList) {
//                Log.d(TAG, threadDiff.toString());
//            }


        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            timeMonitor.onActivityDestroyed();
//            appStateController.finish();
//            deviceStateController.finish();
//
//            // service
//            wifiServiceController.finish();
//            gpsServiceController.finish();
//            bluetoothServiceController.finish();
//            alarmServiceController.finish();
//            notificationServiceController.finish();
        }
    }

    ;

}