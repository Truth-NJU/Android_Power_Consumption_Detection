package com.example.androidpowercomsumption.controller;


import android.util.Log;
import com.example.androidpowercomsumption.utils.systemservice.hooker.WifiServiceHooker;

public class WifiServiceController {
    private final String TAG = "WifiService";

    private WifiServiceHooker wifiServiceHooker;

    public WifiServiceController(WifiServiceHooker wifiServiceHooker) {
        this.wifiServiceHooker = wifiServiceHooker;
    }

    public void start() {
        wifiServiceHooker.sHookHelper.doHook();

    }

    public void finish() {
        Log.d(TAG, "scanTime: " + wifiServiceHooker.getScanTime());
        Log.d(TAG, "getScanResultTime: " + wifiServiceHooker.getGetScanResultTime());
    }
}
