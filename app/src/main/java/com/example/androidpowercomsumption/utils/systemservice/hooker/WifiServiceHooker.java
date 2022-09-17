package com.example.androidpowercomsumption.utils.systemservice.hooker;

import android.content.Context;
import android.util.Log;
import androidx.annotation.Nullable;

import java.lang.reflect.Method;

public final class WifiServiceHooker {
    private static final String TAG = "WifiService";

    private int scanTime = 0;

    private int getScanResultTime = 0;

    public ServiceHookCallback sHookCallback = new ServiceHookCallback() {
        @Override
        public void serviceMethodInvoke(Method method, Object[] args) {
            if ("startScan".equals(method.getName())) {
                scanTime++;
                Log.d(TAG, "scan++");
            } else if ("getScanResults".equals(method.getName())) {
                getScanResultTime++;
                Log.d(TAG, "getScanResults++");
            }
        }

        @Nullable
        @Override
        public Object serviceMethodIntercept(Object receiver, Method method, Object[] args) throws Throwable {
            return null;
        }
    };


    public SystemServiceHooker sHookHelper = new SystemServiceHooker(Context.WIFI_SERVICE, "android.net.wifi.IWifiManager", sHookCallback);

    public int getScanTime() {
        return scanTime;
    }

    public void setScanTime(int scanTime) {
        this.scanTime = scanTime;
    }

    public int getGetScanResultTime() {
        return getScanResultTime;
    }

    public void setGetScanResultTime(int getScanResultTime) {
        this.getScanResultTime = getScanResultTime;
    }
}
