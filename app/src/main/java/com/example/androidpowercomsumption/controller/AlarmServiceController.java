package com.example.androidpowercomsumption.controller;

import android.util.Log;
import com.example.androidpowercomsumption.utils.systemservice.hooker.AlarmServiceHooker;

public class AlarmServiceController {
    private final String TAG = "AlarmService";

    private AlarmServiceHooker alarmServiceHooker;

    public AlarmServiceController(AlarmServiceHooker alarmServiceHooker) {
        this.alarmServiceHooker = alarmServiceHooker;
    }

    public void start() {
        alarmServiceHooker.sHookHelper.doHook();

    }

    public void finish() {
        Log.d(TAG, "setTime: " + alarmServiceHooker.getSetTime());
    }
}
