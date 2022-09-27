package com.example.androidpowercomsumption.controller.servicecontroller;


import android.util.Log;
import com.example.androidpowercomsumption.utils.LogFileWriter;
import com.example.androidpowercomsumption.utils.systemservice.hooker.WifiServiceHooker;

public class WifiServiceController {
    private final String TAG = "ServiceController";

    private WifiServiceHooker wifiServiceHooker;

    public WifiServiceController(WifiServiceHooker wifiServiceHooker) {
        this.wifiServiceHooker = wifiServiceHooker;
    }

    public void start() {
        wifiServiceHooker.sHookHelper.doHook();

    }

    public void finish() {
        wifiServiceHooker.sHookHelper.doUnHook();
        Log.d(TAG, "WifiServiceController: scanTime: " + wifiServiceHooker.getScanTime());
        LogFileWriter.write("搜索wifi的次数: " + wifiServiceHooker.getScanTime());
        Log.d(TAG, "WifiServiceController: getScanResultTime: " + wifiServiceHooker.getGetScanResultTime());
        LogFileWriter.write("查询搜索结果次数: " + wifiServiceHooker.getGetScanResultTime());
    }
}
