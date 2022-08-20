package com.example.androidpowercomsumption.utils;

import android.os.IBinder;
import android.os.IInterface;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public class SystemServiceHooker {

    private static final String TAG = "SystemServiceHooker";

    private final String serviceName;
    private final String serviceClass;
    private final ServiceHookCallback hookCallback;

    @Nullable
    private IBinder baseServiceBinder;
    @Nullable
    private IBinder proxyServiceBinder;

    public SystemServiceHooker(String serviceName, String serviceClass, ServiceHookCallback hookCallback) {
        this.serviceName = serviceName;
        this.serviceClass = serviceClass;
        this.hookCallback = hookCallback;
    }

    public boolean doHook() {
        try {
            Class<?> serviceManager = Class.forName("android.os.ServiceManager");
            Method getService = serviceManager.getDeclaredMethod("getService", String.class);
            // hook服务的IBinder对象
            this.baseServiceBinder = (IBinder) getService.invoke(null, serviceName);

            this.proxyServiceBinder = (IBinder) Proxy.newProxyInstance(
                    serviceManager.getClassLoader(),
                    new Class<?>[]{IBinder.class},
                    new BinderProxyHandler(this.serviceClass, this.hookCallback, this.baseServiceBinder));

            Class<?> serviceManagerCls = Class.forName("android.os.ServiceManager");
            Field cacheField = serviceManagerCls.getDeclaredField("sCache");
            cacheField.setAccessible(true);
            Map<String, IBinder> cache = (Map) cacheField.get(null);
            cache.put(serviceName, this.proxyServiceBinder);

            return true;

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }


    static final class BinderProxyHandler implements InvocationHandler {

        private final IBinder baseServiceBinder;

        private final String serviceClassName;

        private final ServiceHookCallback callback;

        BinderProxyHandler(String serviceClassName, ServiceHookCallback callback, IBinder baseServiceBinder) throws Exception {
            this.baseServiceBinder = baseServiceBinder;
            this.serviceClassName = serviceClassName;
            this.callback = callback;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("queryLocalInterface".equals(method.getName())) {
                Class<?> serviceManagerStubCls = Class.forName(serviceClassName + "$Stub");
                ClassLoader classLoader = serviceManagerStubCls.getClassLoader();
                Method asInterfaceMethod = serviceManagerStubCls.getDeclaredMethod("asInterface", IBinder.class);

                Class<?> serviceManagerCls = Class.forName(serviceClassName);
                final Object originManagerService = asInterfaceMethod.invoke(null, baseServiceBinder);

                return Proxy.newProxyInstance(classLoader,
                        new Class[]{IBinder.class, IInterface.class, serviceManagerCls},
                        new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                // todo
                                return null;
                            }
                        }
                );
            }
            return method.invoke(baseServiceBinder, args);
        }

    }
}
