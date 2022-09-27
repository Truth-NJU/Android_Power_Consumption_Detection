package com.example.androidpowercomsumption.controller.servicecontroller;

import android.util.Log;
import com.example.androidpowercomsumption.utils.monitor.LogFileWriter;
import com.example.androidpowercomsumption.utils.systemservice.hooker.BluetoothServiceHooker;

public class BluetoothServiceController {
    private final String TAG = "ServiceController";

    private BluetoothServiceHooker bluetoothServiceHooker;

    public BluetoothServiceController(BluetoothServiceHooker bluetoothServiceHooker) {
        this.bluetoothServiceHooker = bluetoothServiceHooker;
    }

    public void start() {
        bluetoothServiceHooker.sHookHelper.doHook();

    }

    public void finish() {
        bluetoothServiceHooker.sHookHelper.doUnHook();
        Log.d(TAG, "BluetoothServiceController: scanTime: " + bluetoothServiceHooker.getScanTime());
        LogFileWriter.write("搜索蓝牙的次数: " + bluetoothServiceHooker.getScanTime());
        Log.d(TAG, "BluetoothServiceController: registerTime: " + bluetoothServiceHooker.getRegisterTime());
        LogFileWriter.write("注册蓝牙的次数: " + bluetoothServiceHooker.getRegisterTime());
        Log.d(TAG, "BluetoothServiceController: discoveryTime: " + bluetoothServiceHooker.getDiscoveryTime());
        LogFileWriter.write("发现蓝牙的次数: " + bluetoothServiceHooker.getDiscoveryTime());
    }
}
