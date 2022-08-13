package com.example.androidpowercomsumption.utils;

import android.os.SystemClock;

/**
 * 用来开启和关闭监控
 */
public class Controller {

    public long startTime = SystemClock.uptimeMillis(); // 监控开始时间

    public void start() {
        startTime = SystemClock.uptimeMillis();
        // 对开始时间的系统状态做快照
    }

    public void finish(){
        // 对结束时间的系统状态做快照
    }
}
