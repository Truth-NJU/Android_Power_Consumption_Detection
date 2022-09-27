package com.example.androidpowercomsumption.controller.servicecontroller;

import android.util.Log;
import com.example.androidpowercomsumption.utils.LogFileWriter;
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
        alarmServiceHooker.sHookHelper.doUnHook();
        LogFileWriter.write("调用设置提醒服务的次数: " + alarmServiceHooker.getSetTime());
        Log.d(TAG, "AlarmServiceController: setTime: " + alarmServiceHooker.getSetTime());
    }
}
