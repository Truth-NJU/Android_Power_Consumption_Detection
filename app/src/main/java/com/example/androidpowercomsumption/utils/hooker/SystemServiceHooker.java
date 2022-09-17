package com.example.androidpowercomsumption.utils.hooker;

import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;
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
            // hook服务的原始IBinder对象
            this.baseServiceBinder = (IBinder) getService.invoke(null, serviceName);

            this.proxyServiceBinder = (IBinder) Proxy.newProxyInstance(
                    serviceManager.getClassLoader(),
                    new Class<?>[]{IBinder.class},
                    new BinderProxyHandler(this.serviceClass, this.hookCallback, this.baseServiceBinder));


            // 获取缓存池
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

    public boolean doUnHook() {
        try {
            Class<?> serviceManager = Class.forName("android.os.ServiceManager");

            // 判断当前IBinder是否为嗲里嗲气IBinder
            Method method = serviceManager.getDeclaredMethod("getService", String.class);
            IBinder currentBinder = (IBinder) method.invoke(null, serviceName);
            if (currentBinder != proxyServiceBinder) return false;



            Field cacheField = serviceManager.getDeclaredField("sCache");
            cacheField.setAccessible(true);
            Map<String, IBinder> cache = (Map) cacheField.get(null);
            cache.put(serviceName, baseServiceBinder);
            return true;
        } catch (Exception e) {
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
                                if (callback != null) {
                                    callback.serviceMethodInvoke(method, args);
                                    Object result = callback.serviceMethodIntercept(originManagerService, method, args);
                                    if (result != null) {
                                        return result;
                                    }
                                }
                                return method.invoke(originManagerService, args);
                            }
                        }
                );
            }
            return method.invoke(baseServiceBinder, args);
        }

    }

    /*public interface HookCallback {
        void serviceMethodInvoke(Method method, Object[] args);

        @Nullable
        Object serviceMethodIntercept(Object receiver, Method method, Object[] args) throws Throwable;
    }

    private final String mServiceName;
    private final String mServiceClass;
    private final HookCallback mHookCallback;

    @Nullable
    private IBinder mOriginServiceBinder;
    @Nullable
    private IBinder mDelegateServiceBinder;

    public SystemServiceHooker(final String serviceName, final String serviceClass, final HookCallback hookCallback) {
        mServiceName = serviceName;
        mServiceClass = serviceClass;
        mHookCallback = hookCallback;
    }

    @SuppressWarnings({"PrivateApi", "unchecked", "rawtypes"})
    public boolean doHook() {
        Log.d(TAG, "doHook:" + mServiceName + " " + mServiceClass);
        try {
            BinderProxyHandler binderProxyHandler = new BinderProxyHandler(mServiceName, mServiceClass, mHookCallback);
            IBinder delegateBinder = binderProxyHandler.createProxyBinder();

            Class<?> serviceManagerCls = Class.forName("android.os.ServiceManager");
            Field cacheField = serviceManagerCls.getDeclaredField("sCache");
            cacheField.setAccessible(true);
            Map<String, IBinder> cache = (Map) cacheField.get(null);
            cache.put(mServiceName, delegateBinder);

            mDelegateServiceBinder = delegateBinder;
            mOriginServiceBinder = binderProxyHandler.getOriginBinder();
            return true;

        } catch (Throwable e) {
            Log.d(TAG, "#doHook exp: " + e.getLocalizedMessage());
        }
        return false;
    }

    @SuppressWarnings({"PrivateApi", "unchecked", "rawtypes"})
    public boolean doUnHook() {
        if (mOriginServiceBinder == null) {
            Log.d(TAG, "#doUnHook mOriginServiceBinder null");
            return false;
        }
        if (mDelegateServiceBinder == null) {
            Log.d(TAG, "#doUnHook mDelegateServiceBinder null");
            return false;
        }

        try {
            IBinder currentBinder = BinderProxyHandler.getCurrentBinder(mServiceName);
            if (mDelegateServiceBinder != currentBinder) {
                Log.d(TAG, "#doUnHook mDelegateServiceBinder != currentBinder");
                return false;
            }

            Class<?> serviceManagerCls = Class.forName("android.os.ServiceManager");
            Field cacheField = serviceManagerCls.getDeclaredField("sCache");
            cacheField.setAccessible(true);
            Map<String, IBinder> cache = (Map) cacheField.get(null);
            cache.put(mServiceName, mOriginServiceBinder);
            return true;
        } catch (Throwable e) {
            Log.d(TAG, "#doUnHook exp: " + e.getLocalizedMessage());
        }
        return false;
    }


    static final class BinderProxyHandler implements InvocationHandler {
        private final IBinder mOriginBinder;
        private final Object mServiceManagerProxy;

        BinderProxyHandler(String serviceName, String serviceClass, HookCallback callback) throws Exception {
            mOriginBinder = getCurrentBinder(serviceName);
            mServiceManagerProxy = createServiceManagerProxy(serviceClass, mOriginBinder, callback);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("queryLocalInterface".equals(method.getName())) {
                return mServiceManagerProxy;
            }
            return method.invoke(mOriginBinder, args);
        }

        public IBinder getOriginBinder() {
            return mOriginBinder;
        }

        @SuppressWarnings({"PrivateApi"})
        public IBinder createProxyBinder() throws Exception {
            Class<?> serviceManagerCls = Class.forName("android.os.ServiceManager");
            ClassLoader classLoader = serviceManagerCls.getClassLoader();
            if (classLoader == null) {
                throw new IllegalStateException("Can not get ClassLoader of " + serviceManagerCls.getName());
            }
            return (IBinder) Proxy.newProxyInstance(
                    classLoader,
                    new Class<?>[]{IBinder.class},
                    this
            );
        }

        @SuppressWarnings({"PrivateApi"})
        static IBinder getCurrentBinder(String serviceName) throws Exception {
            Class<?> serviceManagerCls = Class.forName("android.os.ServiceManager");
            Method getService = serviceManagerCls.getDeclaredMethod("getService", String.class);
            return (IBinder) getService.invoke(null, serviceName);
        }

        @SuppressWarnings({"PrivateApi"})
        private static Object createServiceManagerProxy(String serviceClassName, IBinder originBinder, final HookCallback callback) throws Exception {
            Class<?> serviceManagerCls = Class.forName(serviceClassName);
            Class<?> serviceManagerStubCls = Class.forName(serviceClassName + "$Stub");
            ClassLoader classLoader = serviceManagerStubCls.getClassLoader();
            if (classLoader == null) {
                throw new IllegalStateException("get service manager ClassLoader fail!");
            }
            Method asInterfaceMethod = serviceManagerStubCls.getDeclaredMethod("asInterface", IBinder.class);
            final Object originManagerService = asInterfaceMethod.invoke(null, originBinder);
            return Proxy.newProxyInstance(classLoader,
                    new Class[]{IBinder.class, IInterface.class, serviceManagerCls},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            try {
                                if (callback != null) {
                                    callback.serviceMethodInvoke(method, args);
                                    Object result = callback.serviceMethodIntercept(originManagerService, method, args);
                                    if (result != null) {
                                        return result;
                                    }
                                }
                                return method.invoke(originManagerService, args);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }
            );
        }
    }*/
}
