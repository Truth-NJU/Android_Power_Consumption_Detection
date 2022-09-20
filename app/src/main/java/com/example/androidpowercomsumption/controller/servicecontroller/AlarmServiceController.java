package com.example.androidpowercomsumption.controller.servicecontroller;

import android.util.Log;
import com.example.androidpowercomsumption.utils.systemservice.hooker.AlarmServiceHooker;

public class AlarmServiceController {
    private final String TAG = "ServiceController";

    private AlarmServiceHooker alarmServiceHooker;

    public AlarmServiceController(AlarmServiceHooker alarmServiceHooker) {
        this.alarmServiceHooker = alarmServiceHooker;
    }

    public void start() {
        alarmServiceHooker.sHookHelper.doHook();

    }

    public void finish() {
        Log.d(TAG, "AlarmServiceController: setTime: " + alarmServiceHooker.getSetTime());
    }
}
