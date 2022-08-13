package com.example.androidpowercomsumption;

import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidpowercomsumption.utils.ProcState;
import com.example.androidpowercomsumption.utils.ProcStateUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG="ProcStateUtil";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProcStateUtil procStateUtil=new ProcStateUtil();
//        procStateUtil.splicePath(Process.myPid(),-1);
        List<ProcState> threadList = procStateUtil.getAllThreadInfo();
        Log.d(TAG, String.valueOf(threadList.size()));

    }


}