package com.example.androidpowercomsumption.utils.servicehook;

import android.content.Context;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class WifiService {
    private static final String TAG = "WifiService";

    public static void hookService(Context context) {
        IBinder wifiService = ServiceManager.getService(Context.WIFI_SERVICE);
        String IWifiManager = "android.net.wifi.IWifiManager";

        if (wifiService != null) {
            IBinder hookWifiService =
                    (IBinder) Proxy.newProxyInstance(wifiService.getClass().getClassLoader(),
                            wifiService.getClass().getInterfaces(),
                            new ServiceHook(wifiService, IWifiManager, true, new WifiHookHandler()));
            ServiceManager.setService(Context.WIFI_SERVICE, hookWifiService);
        } else {
            Log.e(TAG, "WifiService hook failed!");
        }
    }

    public static class WifiHookHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Log.e(TAG, "wifihookhandler invoke");
            String methodName = method.getName();
            //每次从本应用复制的文本，后面都加上分享的出处
            if ("startScan".equals(methodName)) {
                Log.d(TAG, "invoke: startScan");
            }else if("getScanResults".equals(methodName)){
                Log.d(TAG, "invoke: getScanResults");
            }
            return method.invoke(proxy, args);
        }
    }
}
