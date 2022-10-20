# AndroidPowerConsumption
安卓耗电检测

## 1. 背景

每一个稍微有点规模的 App，总会自带一些线下的测试功能代码，比如环境切换功能、帧率查看功能等等，这些功能的往往放在各式各样的入口中，每个App开发者都需要自己实现，会降低开发效率。DoKit 是一款面向移动端的研发测试效率平台，能够让每一个 App 快速接入一些常用的或者没有实现的一些辅助开发工具、测试效率工具、视觉辅助工具，可以帮助App开发者进行测试。

在 Android 应用开发中，我们需要考虑的是如何优化电量使用，让我们的 App 不会因为电量消耗过高被用户排斥，或者被其他安全应用报告，以此确保用户黏性。App耗电量的大小是App开发者们密切关注的方面，因此耗电检测功能是Dokit平台所必须具备的一项能力。目前关于Dokit宿主机耗电检测这一块的功能尚未开发完全。

## 2. 目标

检测DoKit宿主应用的耗电情况，能详细显示应用使用过程中的耗电分布情况。完成耗电检测功能，详细显示应用使用过程中的耗电分布情况，并产出检测报告。同时不能借助usb连接，只能在软件内部进行耗电情况的收集。

## 3. 调研

前期调研了一系列的实现方案

1. 通过java反射机制去调用android内部关于耗电检测的接口，但是android关于相关接口的权限验证极为严格，没有办法避开android的权限验证。
2. 通过在手机内搭建一个ADB服务器使得无需电脑也可以执行adb shell相关的命令来得到电量信息，为此调研了两个工具：ADBLib和LADB

1. 1. ADBLib：ADBLib在操作时仍然需要使用电脑通过usb与手机建立起连接后才能使用，不符合课题的要求
   2. LADB：了解了LADB的实现思路，他根据谷歌推出的无线调试功能，把手机本身当作电脑，让它自己连接自己。即把手机本身当作adb服务器，可以在手机上使用adb命令并获得相应的结果。核心思路在于LADB将编译后的ADB类库以.so文件的形式添加在最终的apk包内，每当需要执行adb命令时，就会调用该文件来进行adb命令的执行，得到最终的结果。尝试实现了demo，但是在demo的实现过程中，发现LADB必须基于无线调试功能，同时它只能适配arm64和armeabi设备，这不满足课题关于可靠性和兼容性的要求，于是舍弃该种做法。

1. 安卓的App导致耗电的原因更是多种多样，比如线程问题、WakeLock 问题、Wifi / 蓝牙 / GPS 扫描频繁问题等等。因此希望通过监控App的线程功耗、系统服务调用以及App和设备的状态等来反应App的耗电情况，对此调研了腾讯开源的matrix工具，了解了一些实现上的思路和方法。

## 4. 最终的解决方案

主要从**线程功耗异常**和**系统服务调用异常**两个方面来实现对异常耗电情况的监控。

![img](https://cdn.nlark.com/yuque/0/2022/png/25666990/1660964760533-ab4875a2-4597-48d9-92df-9602c6cf95c6.png)

### 4.1 线程功耗异常

首先通过Linux 命令 proc/[pid]/stat 和 proc/[pid]/task/[tid]/stat 可以 Dump 当前 App 进程和线程的统计信息。（信息中会包含线程运行的用户时间、系统时间等等，根据时间可以转化成线程的jiffy消耗）。根据每个线程在执行前后的Jiffy消耗和线程的运行状态，可以得到当前线程在这段时间内的运行状况，是否发生异常。

### 4.2 检测系统服务调用异常

通过Hook SystemService或者ASM方案来检测系统服务调用异常

每次调用系统服务的流程如下：
先通过 ServiceManager 的 getService 方法获取远端服务的 IBinder 对象，然后在通过指定服务的 Stub 类的 asInterface 方法转化成本地可使用对象，再去调用对应的系统服务的方法。

通过上述方法，我们可以获得在app运行时间内所调用的系统服务以及检测异常的系统服务调用。（比如在短时间内大量重复的调用相同服务，或者调用某个服务响应时间很长等等）

### 4.3 App和设备状态统计

![img](https://cdn.nlark.com/yuque/0/2022/png/25666990/1660045422782-931a1e91-5f69-45f8-85f7-b8532b0dec94.png)

在设备和App状态发生改变的时候，记录下每一种状态的起始时间和结束时间。在监控的时间段内，根据监控窗口的起始时间和结束时间以及记录的这段时间内的app和设备状态，就可以计算出这段时长内 App 每个事件状态的占比。

### 4.4 生成耗电检测报告

对宿主App从create到destory的生命周期进行实时监控，在App状态发生改变时（也就是App进行前后台切换时）综合上述检测到的信息，生成该状态时间段的耗电检测报告。最终生成在App的整个生命周期内的电量报告。重点展示app的线程开销和系统服务调用，同时也会展示一些设备和app的状态。

## 5. 最终实现

### 5.1 线程功耗计算

**目前实现的核心逻辑**如下。会在开始监控的时间和结束监控的时间点对系统做一次快照，在`getAllThreadInfo()`方法中通过Linux 命令 proc/[pid]/stat 和 proc/[pid]/task/[tid]/stat 输出并记录系统当前时间点App的线程的状态和jiffy消耗。然后当监控结束时会触发`calculateDiff`方法通过对比开始和结束时线程状态的差异来计算这一时间段内线程的消耗。
![img](https://cdn.nlark.com/yuque/0/2022/png/25666990/1660457710853-afb9c8db-4bd1-4943-ae7b-c8986a05bcc5.png)

输出效果如下。可以输出线程在监控时间段内的jiffy消耗。![img](https://cdn.nlark.com/yuque/0/2022/png/25666990/1660739190852-8b3695fa-0271-47c2-acbb-e89b4b3e373c.png)

### 5.2 App状态统计

关于统计 App 状态，可以通过ActivityLifecycleCallbacks拿到App所有Activity的生命周期回调。通过重写ActivityLifecycleCallbacks的onActivityStarted、onActivityStopped、onActivityDestroyed方法来监听App的运行状态。实现如下。appStateController是一个控制类，用来计算在监控的时间段内，app的前台运行时间和后台运行时间的占比。

```java
@Override
public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    // 只会在启动的时候触发一次
    appStateController.start();
    appStateController.status = true; // 前台状态
    appStateController.curStatusStartTime = appStateController.startTime; // 当前状态的开始时间
}

@Override
public void onActivityStarted(Activity activity) {
    Log.d(TAG, "APP进入前台");
    if (!appStateController.status) { // 后台进入前台
        appStateController.status = true;
        appStateController.curStatusEndTime = System.currentTimeMillis();// 后台状态的结束时间
        appStateController.backgroundTime += (appStateController.curStatusEndTime - appStateController.curStatusStartTime);
        appStateController.curStatusStartTime = System.currentTimeMillis();// 后台进入前台，前台状态的开始时间

    }
}
@Override
public void onActivityStopped(Activity activity) {
    Log.d(TAG, "App进入后台");
    if (appStateController.status) { // 由前台进入后台
        appStateController.status = false;
        appStateController.curStatusEndTime = System.currentTimeMillis(); // 前台状态的结束时间
        appStateController.foregroundTime += (appStateController.curStatusEndTime - appStateController.curStatusStartTime);
        appStateController.curStatusStartTime = System.currentTimeMillis(); // 前台进入后台，后台状态的开始时间
    }
}

@Override
public void onActivityDestroyed(Activity activity) {
    appStateController.finish();
}
```

输出效果如下，成功的计算出了app的前后台运行时间和占比。

![img](https://cdn.nlark.com/yuque/0/2022/png/25666990/1660738855391-a3adb3ac-f611-4b6b-968c-19a8ea36dbab.png)

### 5.3 设备状态统计

核心的逻辑如下。通过在`onCreate`生命周期方法中注册一个自己实现的DeviceStateListener监听器（它会接收设备屏幕状态以及设备充电状态的广播并调用相应的方法）用来监听屏幕点亮、屏幕熄灭和设备充电的事件。DeviceStateController是一个控制类，它持有设备的息屏时间、亮屏时间、充电时间和总的运行时间，根据这些时间可以计算出在监控的时间段内设备的息屏时间、亮屏时间、充电时间的占比。

```java
listener.register(new DeviceStateListener.ScreenStateListener() {

    boolean isFirst = true; // 第一次启动
    // 设备状态监控
    @Override
    public void onScreenOn() {
        Log.d(TAG + "Device", "屏幕点亮");
        if (isFirst) {
            isFirst = false;
            deviceStateController.start();
            deviceStateController.status = true; // 亮屏状态
            deviceStateController.curStatusStartTime = deviceStateController.startTime;
        } else {
            if (!deviceStateController.status) { // 息屏进入亮屏
                deviceStateController.status = true;
                deviceStateController.curStatusEndTime = System.currentTimeMillis(); // 息屏状态的结束时间
                deviceStateController.screenOffTime += (deviceStateController.curStatusEndTime - deviceStateController.curStatusStartTime);
                deviceStateController.curStatusStartTime = System.currentTimeMillis(); // 息屏进入亮屏，亮屏状态的开始时间
            }
        }
    }
    @Override
    public void onScreenOff() {
        Log.d(TAG + "Device", "屏幕熄灭");
        if (deviceStateController.status) { // 亮屏进入息屏
            deviceStateController.status = false;
            deviceStateController.curStatusEndTime = System.currentTimeMillis(); // 亮屏状态的结束时间
            deviceStateController.screenOnTime += (deviceStateController.curStatusEndTime - deviceStateController.curStatusStartTime);
            deviceStateController.curStatusStartTime = System.currentTimeMillis(); // 亮屏进入息屏，息屏状态的开始时间

        }
    }
    @Override
    public void onUserPresent() {

    }
    boolean isFirstCharge = true; // 第一次充电

    boolean isCharge = false;

    @Override
    public void onPowerConnected() {
        Log.d(TAG + "Device", "开始充电");
        if (isFirstCharge) { // 第一次充电
            isFirstCharge = false;
            isCharge = true;
            deviceStateController.curStatusStartTimeCharge = System.currentTimeMillis();
        } else {
            if (!isCharge) { // 从不充电变为充电状态
                isCharge = true;
                deviceStateController.curStatusEndTimeCharge = System.currentTimeMillis(); // 不充电状态的结束时间
                deviceStateController.noChargeTime += (deviceStateController.curStatusEndTimeCharge - deviceStateController.curStatusStartTimeCharge);
                deviceStateController.curStatusStartTimeCharge = System.currentTimeMillis();// 充电状态的开始时间

            }
        }
    }

    @Override
    public void onPowerDisconnected() {
        Log.d(TAG + "Device", "停止充电");
        if (isCharge) { // 从充电状态变为不充电状态
            isCharge = false;
            deviceStateController.curStatusEndTimeCharge = System.currentTimeMillis(); // 充电状态的结束时间
            deviceStateController.chargeTime += (deviceStateController.curStatusEndTimeCharge - deviceStateController.curStatusStartTimeCharge);
            deviceStateController.curStatusStartTimeCharge = System.currentTimeMillis(); // 不充电状态的开始时间

        }

    }
});
```

输出效果如下。成功的计算出了在App运行阶段设备的息、亮屏时间和占比。

![img](https://cdn.nlark.com/yuque/0/2022/png/25666990/1660826253799-d459b329-daad-49ab-90f8-d94867d2f10f.png)

### 5.4 检测系统服务调用

通过Hook SystemService方案来检测系统服务调用异常系统服务调用监控

1. 实现了一个基础的SystemServiceHooker类，用来对指定的系统服务进行hook，并抽象出ServiceHook Callback接口。
2. 各个具体的ServiceHooker可以通过实现ServiceHookCallback中对应的方法来对特定系统服务的方法上增加自己的实现，以此来实现对系统服务调用次数的监控。

![img](https://cdn.nlark.com/yuque/0/2022/png/25666990/1665044139435-ab03180d-e7ef-41d2-8696-90d6d1d7f43c.png)

### 5.5 整体效果

1. 以App前后台状态切换为监控的时间片
2. 每次App前后台切换时结束上一时间片的监控、开始一次新的监控，并输出上一时间段内的监控数据，监控数据包括线程功耗和系统服务调用的次数，线程功耗可以看作线程在该时间段内占用CPU运行的时间，也从侧面反映了cpu的占用率。若耗电量发生异常可以较为直观的看出哪个线程发生异常，从而方便开发人员进行排查。
3. 在App整个生命周期结束退出后，会输出App整个生命周期内前后台运行时间的占比、设备息屏亮屏时间的占比、充电时间的占比。
