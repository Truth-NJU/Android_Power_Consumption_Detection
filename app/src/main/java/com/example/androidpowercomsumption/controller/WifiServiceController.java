package com.example.androidpowercomsumption.controller;

import android.util.Log;
import com.example.androidpowercomsumption.utils.hooker.WifiServiceHooker;

public class WifiServiceController {

    public int queryTime = 0; // 查询扫描结果的次数

    public int scanTime = 0; // 请求扫描的次数

    private final String TAG = "WifiService";

    private WifiServiceHooker.ServiceListener listener;

    public void start() {
        listener = new WifiServiceHooker.ServiceListener() {
            @Override
            public void startScan() {
                scanTime++;
            }

            @Override
            public void getScanResults() {
                queryTime++;
            }
        };
        WifiServiceHooker.addListener(listener);
    }

    public void finish() {
        Log.d(TAG, "wifi请求扫描的次数:" + scanTime);
        Log.d(TAG, "wifi查询扫描结果的次数:" + queryTime);
        WifiServiceHooker.removeListener(listener);
        this.queryTime = 0;
        this.scanTime = 0;
    }
}
