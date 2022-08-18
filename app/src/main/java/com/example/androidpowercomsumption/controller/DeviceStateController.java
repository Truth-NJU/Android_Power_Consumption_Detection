package com.example.androidpowercomsumption.controller;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DeviceStateController {
    private static final String TAG = "AppStateApplication";
    public long startTime; // 监控开始时间

    public long endTime; // 监控结束时间

    public long screenOffTime; // 息屏时长

    public long screenOnTime; // 亮屏时长

    public long chargeTime; // 充电时长

    public long noChargeTime; // 未充电时长

    public long curStatusStartTimeCharge; // 当前状态的开始时间

    public long curStatusEndTimeCharge; // 当前状态的结束时间

    public double chargeRatio; // 充电时长占比

    public boolean status; // true 亮屏 false 息屏

    public long curStatusStartTime; // 当前状态的开始时间

    public long curStatusEndTime; // 当前状态的结束时间

    public double screenOffRatio; // 息屏时间占比

    public double screenOnRatio; // 亮屏时间占比

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public void finish() {
        this.endTime = System.currentTimeMillis();
        this.screenOffRatio = screenOffTime * 1.0 / (this.screenOffTime + this.screenOnTime);
        this.screenOnRatio = screenOnTime * 1.0 / (this.screenOffTime + this.screenOnTime);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日-HH时mm分ss秒");
        Date startDate = new Date(this.startTime);
        Date endDate = new Date(this.endTime);
        Log.d(TAG + "Device", "息屏时间:" + this.screenOffTime);
        Log.d(TAG + "Device", "亮屏时间:" + this.screenOnTime);
        Log.d(TAG + "Device", "总运行时间:" + format.format(startDate) + "~" + format.format(endDate));
        Log.d(TAG + "Device", "息屏时间占比:" + String.valueOf(this.screenOffRatio));
        Log.d(TAG + "Device", "亮屏时间占比:" + String.valueOf(this.screenOnRatio));

//        this.chargeRatio = this.chargeTime * 1.0 / (this.chargeTime + this.noChargeTime);
//        Log.d(TAG + "Device", "充电时间占比:" + String.valueOf(this.chargeRatio));


    }
}
