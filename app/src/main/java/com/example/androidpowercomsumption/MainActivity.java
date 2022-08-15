package com.example.androidpowercomsumption;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleObserver;
import com.example.androidpowercomsumption.controller.ThreadController;
import com.example.androidpowercomsumption.diff.ThreadConsumptionDiff;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LifecycleObserver {
    private final String TAG = "ProcStateUtil";

    private final ThreadController threadController = new ThreadController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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