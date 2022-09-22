package com.example.androidpowercomsumption.utils;

import java.io.FileWriter;

public class LogFileWriter {
    public static void write(String str) {
        try {
            FileWriter fileWriter = new FileWriter("/Users/taozehua/Downloads/开源项目/AndroidPowerConsumption/data.txt", true);
            fileWriter.write(str + "\n");
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
