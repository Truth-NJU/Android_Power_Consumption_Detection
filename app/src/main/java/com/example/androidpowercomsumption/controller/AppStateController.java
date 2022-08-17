package com.example.androidpowercomsumption.controller;

import android.util.Log;

public class AppStateController {

    private static final String TAG = "AppStateApplication";
    public long startTime; // 监控开始时间

    public long endTime; // 监控结束时间

    public long foregroundTime; // 前台运行时长

    public long backgroundTime; // 后台运行时长

    public boolean status; // true 前台 false 后台
    public long curStatusStartTime; // 当前状态的开始时间

    public long curStatusEndTime; // 当前状态的结束时间

    public double foregroundRatio; //前台运行时间占比

    public double backgroundRatio; // 后台运行时间占比

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public void finish() {
        this.endTime = System.currentTimeMillis();
        this.foregroundRatio = foregroundTime * 1.0 / (this.endTime - this.startTime);
        this.backgroundRatio = backgroundTime * 1.0 / (this.endTime - this.startTime);
        Log.d(TAG, "前台运行时间:" + this.foregroundTime);
        Log.d(TAG, "后台运行时间:" + this.backgroundTime);
        Log.d(TAG, "总运行时间:" + (this.endTime - this.startTime));
        Log.d(TAG, String.valueOf(this.foregroundRatio));
        Log.d(TAG, String.valueOf(this.backgroundRatio));


    }
}
