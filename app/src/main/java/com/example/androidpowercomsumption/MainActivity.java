package com.example.androidpowercomsumption;

import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleObserver;
import com.example.androidpowercomsumption.utils.ProcState;
import com.example.androidpowercomsumption.utils.ProcStateUtil;
import com.example.androidpowercomsumption.utils.controller.ThreadController;
import com.example.androidpowercomsumption.utils.diff.ThreadConsumptionDiff;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LifecycleObserver {
    private final String TAG = "ProcStateUtil";

    private ThreadController threadController = new ThreadController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ProcStateUtil procStateUtil = new ProcStateUtil();
//        procStateUtil.splicePath(Process.myPid(),-1);
//        List<ProcState> threadList = procStateUtil.getAllThreadInfo();
        threadController.start();

    }

    @Override
    protected void onStop() {
        super.onStop();
        threadController.finish();
        List<ThreadConsumptionDiff.ThreadDiff> threadDiffList = threadController.threadDiffList;
        for(ThreadConsumptionDiff.ThreadDiff threadDiff:threadDiffList){
            Log.d(TAG, threadDiff.toString());
        }
    }


}