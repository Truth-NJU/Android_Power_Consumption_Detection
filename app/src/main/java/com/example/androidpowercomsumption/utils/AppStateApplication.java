package com.example.androidpowercomsumption.utils;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;


public class AppStateApplication extends Application {
    private static final String TAG = "AppStateApplication";
    /**
     * onCreate是一个回调接口，android系统会在应用程序启动的时候，在任何应用程序组件（activity、服务、
     * 广播接收器和内容提供者）被创建之前调用这个接口。
     * 需要注意的是，这个方法的执行效率会直接影响到启动Activity等的性能，因此此方法应尽快完成。
     * 最后在该方法中，一定要记得调用super.onCreate()，否则应用程序将会报错。
     */
    @Override
    public void onCreate() {
        super.onCreate();
        //注册自己的Activity的生命周期回调接口。
        registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());
    }

    //声明一个监听Activity们生命周期的接口
    static class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {
        /**
         * application下的每个Activity声明周期改变时，都会触发以下的函数。
         */
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.d(TAG, "APP进入前台");
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.d(TAG, "App进入后台");
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    };

}