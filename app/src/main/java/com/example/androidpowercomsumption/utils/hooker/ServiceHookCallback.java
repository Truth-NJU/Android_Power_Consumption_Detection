package com.example.androidpowercomsumption.utils.hooker;

import androidx.annotation.Nullable;

import java.lang.reflect.Method;

public interface ServiceHookCallback {
    void serviceMethodInvoke(Method method, Object[] args);

    @Nullable
    Object serviceMethodIntercept(Object receiver, Method method, Object[] args) throws Throwable;
}
