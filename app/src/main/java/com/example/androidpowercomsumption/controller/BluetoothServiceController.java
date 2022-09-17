package com.example.androidpowercomsumption.controller;

import android.util.Log;
import com.example.androidpowercomsumption.utils.systemservice.hooker.BluetoothServiceHooker;

public class BluetoothServiceController {
    private final String TAG = "BleService";

    private BluetoothServiceHooker bluetoothServiceHooker;

    public BluetoothServiceController(BluetoothServiceHooker bluetoothServiceHooker) {
        this.bluetoothServiceHooker = bluetoothServiceHooker;
    }

    public void start() {
        bluetoothServiceHooker.sHookHelper.doHook();

    }

    public void finish() {
        Log.d(TAG, "scanTime: " + bluetoothServiceHooker.getScanTime());
        Log.d(TAG, "registerTime: " + bluetoothServiceHooker.getRegisterTime());
        Log.d(TAG, "discoveryTime: " + bluetoothServiceHooker.getDiscoveryTime());
    }
}
