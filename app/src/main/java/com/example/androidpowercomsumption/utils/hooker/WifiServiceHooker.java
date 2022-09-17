package com.example.androidpowercomsumption.utils.hooker;

import android.content.Context;
import android.util.Log;
import androidx.annotation.Nullable;

import java.lang.reflect.Method;

public final class WifiServiceHooker {
    private static final String TAG = "WifiService";

    private static int scanTime = 0;

    private static int getScanResultTime = 0;

    public static ServiceHookCallback sHookCallback = new ServiceHookCallback() {
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


    public static SystemServiceHooker sHookHelper = new SystemServiceHooker(Context.WIFI_SERVICE, "android.net.wifi.IWifiManager", sHookCallback);

    public static int getScanTime() {
        return scanTime;
    }

    public void setScanTime(int scanTime) {
        WifiServiceHooker.scanTime = scanTime;
    }

    public static int getGetScanResultTime() {
        return getScanResultTime;
    }

    public void setGetScanResultTime(int getScanResultTime) {
        WifiServiceHooker.getScanResultTime = getScanResultTime;
    }
}
