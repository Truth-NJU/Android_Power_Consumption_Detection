package com.example.androidpowercomsumption.controller;

import android.util.Log;
import com.example.androidpowercomsumption.utils.hooker.GPSServiceHooker;

public class GPSServiceController {
    public int scanTime = 0; // 请求扫描的次数

    private final String TAG = "GPSService";

    private GPSServiceHooker.ServiceListener listener;

    public void start() {
        listener = new GPSServiceHooker.ServiceListener() {
            @Override
            public void onRequestLocationUpdates(long minTimeMillis, float minDistance) {
                Log.d(TAG, "onRequestLocationUpdates: "+minTimeMillis+" "+minDistance+" 调用GPS服务");
                scanTime++;
            }
        };
        GPSServiceHooker.addListener(listener);
    }

    public void finish() {
        Log.d(TAG, "GPS请求扫描的次数:" + scanTime);
        GPSServiceHooker.removeListener(listener);
        this.scanTime = 0;
    }
}
