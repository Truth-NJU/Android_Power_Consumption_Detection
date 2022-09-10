/*
 * Tencent is pleased to support the open source community by making wechat-matrix available.
 * Copyright (C) 2018 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the BSD 3-Clause License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.androidpowercomsumption.utils.hooker;

import android.content.Context;
import androidx.annotation.AnyThread;
import androidx.annotation.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class GPSServiceHooker {
    public interface ServiceListener {
        @AnyThread
        void onRequestLocationUpdates(long minTimeMillis, float minDistance);
    }

    private static List<ServiceListener> listeners = new ArrayList<>();
    private static boolean sTryHook;
    private static SystemServiceHooker.HookCallback sHookCallback = new SystemServiceHooker.HookCallback() {
        @Override
        public void serviceMethodInvoke(Method method, Object[] args) {
            if ("requestLocationUpdates".equals(method.getName())) {
                long minTime = -1;
                float minDistance = -1;
                if (args != null) {
                    for (Object item : args) {
                        if (item != null && "android.location.LocationRequest".equals(item.getClass().getName())) {
                            try {
                                Method mt = item.getClass().getDeclaredMethod("getFastestInterval");
                                mt.setAccessible(true);
                                minTime = (long) mt.invoke(item);
                                Method mSmallestDisplacement = item.getClass().getDeclaredMethod("getSmallestDisplacement");
                                mSmallestDisplacement.setAccessible(true);
                                minDistance = (float) mSmallestDisplacement.invoke(item);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    }
                }
                for (ServiceListener item : listeners) {
                    item.onRequestLocationUpdates(minTime, minDistance);
                }
            }
        }

        @Nullable
        @Override
        public Object serviceMethodIntercept(Object receiver, Method method, Object[] args) {
            return null;
        }
    };

    private static SystemServiceHooker sHookHelper = new SystemServiceHooker(Context.LOCATION_SERVICE, "android.location.ILocationManager", sHookCallback);

    public static void addListener(ServiceListener listener) {
        if (listeners.contains(listener)) {
            return;
        }

        listeners.add(listener);
        checkHook();
    }


    public static void removeListener(ServiceListener listener) {
        listeners.remove(listener);
        checkUnHook();
    }

    public static void release() {
        listeners.clear();
        checkUnHook();
    }

    private static void checkHook() {
        if (sTryHook) {
            return;
        }

        if (listeners.isEmpty()) {
            return;
        }

        boolean hookRet = sHookHelper.doHook();
        sTryHook = true;
    }

    private static void checkUnHook() {
        if (!sTryHook) {
            return;
        }

        if (!listeners.isEmpty()) {
            return;
        }

        boolean unHookRet = sHookHelper.doUnHook();
        sTryHook = false;
    }
}
