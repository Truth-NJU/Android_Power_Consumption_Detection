package com.example.androidpowercomsumption.utils.systemservice.hooker;

import android.annotation.SuppressLint;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;
import androidx.annotation.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class BluetoothServiceHooker {
    private static final String TAG = "BleService";

    private int registerTime = 0;

    private int discoveryTime = 0;

    private int scanTime = 0;

    public int getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(int registerTime) {
        this.registerTime = registerTime;
    }

    public int getDiscoveryTime() {
        return discoveryTime;
    }

    public void setDiscoveryTime(int discoveryTime) {
        this.discoveryTime = discoveryTime;
    }

    public int getScanTime() {
        return scanTime;
    }

    public void setScanTime(int scanTime) {
        this.scanTime = scanTime;
    }

    private ServiceHookCallback sHookCallback = new ServiceHookCallback() {
        @Override
        public void serviceMethodInvoke(Method method, Object[] args) {
        }

        @Nullable
        @Override
        public Object serviceMethodIntercept(Object receiver, Method method, Object[] args) throws Throwable {
            if ("registerAdapter".equals(method.getName())) {
                Object blueTooth = method.invoke(receiver, args);
                Object proxy = proxyBluetooth(blueTooth);
                return proxy == null ? blueTooth : proxy;
            } else if ("getBluetoothGatt".equals(method.getName())) {
                Object blueToothGatt = method.invoke(receiver, args);
                Object proxy = proxyBluetoothGatt(blueToothGatt);
                return proxy == null ? blueToothGatt : proxy;
            }
            return null;
        }
    };

    public SystemServiceHooker sHookHelper = new SystemServiceHooker("bluetooth_manager", "android.bluetooth.IBluetoothManager", sHookCallback);

    private Object proxyBluetooth(final Object delegate) {
        try {
            @SuppressLint("PrivateApi") final Class<?> clazz = Class.forName("android.bluetooth.IBluetooth");
            final Class<?>[] interfaces = new Class<?>[]{IBinder.class, IInterface.class, clazz};
            final ClassLoader loader = delegate.getClass().getClassLoader();
            final InvocationHandler handler = new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
                    if ("startDiscovery".equals(method.getName())) {
                        discoveryTime++;
                        Log.d(TAG, "discoveryTime++");
                    }

                    return method.invoke(delegate, args);
                }
            };
            return Proxy.newProxyInstance(loader, interfaces, handler);
        } catch (Throwable e) {
            Log.d(TAG, "proxyBluetooth fail");
        }
        return null;
    }

    private Object proxyBluetoothGatt(final Object delegate) {
        try {
            @SuppressLint("PrivateApi") final Class<?> clazz = Class.forName("android.bluetooth.IBluetoothGatt");
            final Class<?>[] interfaces = new Class<?>[]{IBinder.class, IInterface.class, clazz};
            final ClassLoader loader = delegate.getClass().getClassLoader();
            final InvocationHandler handler = new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
                    if ("registerScanner".equals(method.getName())) {
                        registerTime++;
                        Log.d(TAG, "registerTime++");
                    } else if ("startScan".equals(method.getName()) || "startScanForIntent".equals(method.getName())) {
                        scanTime++;
                        Log.d(TAG, "scanTime++");
                    }
                    return method.invoke(delegate, args);
                }
            };
            return Proxy.newProxyInstance(loader, interfaces, handler);
        } catch (Throwable e) {
            Log.d(TAG, "proxyBluetoothGatt fail");
        }
        return null;
    }
}
