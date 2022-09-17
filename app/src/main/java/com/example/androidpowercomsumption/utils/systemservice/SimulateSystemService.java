package com.example.androidpowercomsumption.utils.systemservice;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Looper;
import androidx.core.content.ContextCompat;

import java.util.concurrent.atomic.AtomicReference;

public class SimulateSystemService {
    public static void wifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        wifiManager.getScanResults();
    }

    @SuppressLint({"NewApi", "MissingPermission"})
    public static void gps(Context mContext) {
        final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
        final long MIN_TIME_BW_UPDATES = 1000 * 60;      // 1 minute
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isLocationEnabled()) {
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                if (ContextCompat.checkSelfPermission(mContext, "android.permission.ACCESS_FINE_LOCATION") == 1) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {

                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    }, Looper.getMainLooper());
                }
            }
        }
    }

    public static void bluetooth(Context context){
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = bluetoothManager.getAdapter();

        adapter.startDiscovery();
        adapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            }
        });

        BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
        scanner.startScan(new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
            }
        });

    }


    public static void alarm(Context context){
        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setAction("ALARM_ACTION(" + 10000 + ")");
        final AtomicReference<PendingIntent> operationRef = new AtomicReference<>();
        intent.putExtra("extra_pid", 2233);
        @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 22, intent, 33);
        operationRef.set(pendingIntent);

        am.set(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);
    }
}