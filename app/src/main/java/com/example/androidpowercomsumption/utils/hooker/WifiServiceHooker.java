package com.example.androidpowercomsumption.utils.hooker;

import android.content.Context;
import androidx.annotation.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class WifiServiceHooker {

    public interface ServiceListener {
        void startScan();
        void getScanResults();
    }
    private static ServiceHookCallback serviceHookCallback=new ServiceHookCallback() {
        @Override
        public void serviceMethodInvoke(Method method, Object[] args) {
            if(method.getName().equals("startScan")){
                for(ServiceListener listener:listeners){
                    listener.startScan();
                }
            }else if(method.getName().equals("getScanResults")){
                for(ServiceListener listener:listeners){
                    listener.getScanResults();
                }
            }
        }

        @Nullable
        @org.jetbrains.annotations.Nullable
        @Override
        public Object serviceMethodIntercept(Object receiver, Method method, Object[] args) throws Throwable {
            return null;
        }
    };

    public static SystemServiceHooker systemServiceHooker=new SystemServiceHooker(Context.WIFI_SERVICE, "android.net.wifi.IWifiManager", serviceHookCallback);

    private static List<ServiceListener> listeners = new ArrayList<>();


    public static void addListener(ServiceListener serviceListener){
        if(listeners.contains(serviceListener)) return;
        listeners.add(serviceListener);
        systemServiceHooker.doHook();
    }

    // todo
    public static void removeListener(ServiceListener serviceListener){
        listeners.remove(serviceListener);
        systemServiceHooker.doUnHook();
    }
}
