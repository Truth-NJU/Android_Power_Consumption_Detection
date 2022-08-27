package com.example.androidpowercomsumption.utils.hooker;

import android.content.Context;
import androidx.annotation.Nullable;

import java.lang.reflect.Method;

public class WifiServiceHooker implements ServiceHookCallback {

    public interface ServiceListener {
        void onStartScan();
        void onGetScanResults();
    }


    public SystemServiceHooker systemServiceHooker=new SystemServiceHooker(Context.WIFI_SERVICE, "android.net.wifi.IWifiManager", this);


    @Override
    public void serviceMethodInvoke(Method method, Object[] args) {

    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public Object serviceMethodIntercept(Object receiver, Method method, Object[] args) throws Throwable {
        return null;
    }
}
