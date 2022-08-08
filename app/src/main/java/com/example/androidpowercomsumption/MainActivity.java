package com.example.androidpowercomsumption;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText editPort = null;

    private EditText editCode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String[] portArray = new String[1];
        final String[] codeArray = new String[1];
        editPort = findViewById(R.id.port);
        editCode = findViewById(R.id.code);
        Button button = findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                portArray[0] = editPort.getText().toString();
                codeArray[0] = editCode.getText().toString();
                String port = portArray[0];
                String code = codeArray[0];
                Log.d("Battery", "port:" + port);
                Log.d("Battery", "code:" + code);
                ADB adb = new ADB(getApplicationContext());
                // 配对
                boolean pairRes;
                try {
                    pairRes = adb.pair(port, code);
                    if (pairRes) Log.d("Battery", "success");
                    else Log.d("Battery", "fail");
                    adb.initADBServer();
                    adb.getCurRunAppBatteryInfo();
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


}