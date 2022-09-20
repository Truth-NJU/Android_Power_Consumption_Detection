package com.example.androidpowercomsumption.controller.servicecontroller;

import android.util.Log;
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
        Log.d(TAG, "BluetoothServiceController: scanTime: " + bluetoothServiceHooker.getScanTime());
        Log.d(TAG, "BluetoothServiceController: registerTime: " + bluetoothServiceHooker.getRegisterTime());
        Log.d(TAG, "BluetoothServiceController: discoveryTime: " + bluetoothServiceHooker.getDiscoveryTime());
    }
}
