package com.example.androidpowercomsumption.utils;

import androidx.annotation.Nullable;

import java.lang.reflect.Method;

public interface ServiceHookCallback {
    void onServiceMethodInvoke(Method method, Object[] args);

    @Nullable
    Object onServiceMethodIntercept(Object receiver, Method method, Object[] args) throws Throwable;
}
