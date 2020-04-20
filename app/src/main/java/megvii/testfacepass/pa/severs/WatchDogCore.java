package megvii.testfacepass.pa.severs;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;


import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.beans.models.AndroidAppProcess;
import megvii.testfacepass.pa.beans.models.ProcessManager;
import megvii.testfacepass.pa.utils.ApiCode;
import megvii.testfacepass.pa.utils.AppUtils;

/**
 * description ： 开门狗广播和定时任务类
 * email : yujiaqiang247@pingan.com.cn
 * created on : 2019-09-16 10:36
 */
public class WatchDogCore implements DogReceiver.ReceiveListener {

    private Context mContext;
    private ServiceCore mServiceCore;

    //region 和看门狗相关
    private TimerTask mBroadcastTimerTask;//定时任务用于发送广播
    private Timer mBroadcastTimer;//定时任务用于发送广播

    private TimerTask mReceiveTimerTask;//定时任务用于接收广播
    private Timer mReceiveTimer;//定时任务用于接收广播

    private DogReceiver mDogReceiver;

    //默认是正常的状态
    private int mReceiveDataState = 0;
    private long mThisTimestamp;
    //丢失心跳次数
    private int mLostBroadcastNum = 0;


    public void init(Context context, ServiceCore serviceCore) {
        if (context == null || serviceCore == null) {
            throw new IllegalArgumentException("context or serviceCore can not be null.");
        }
        mContext = context.getApplicationContext();
        mServiceCore = serviceCore;
    }

    /**
     * 发送广播给看门狗
     */
    private void sendBroadcastToDog() {
        Long timestamp = System.currentTimeMillis();
        Log.e(ApiCode.DOG_TAG, "sendBroadcastToDog=" + timestamp);
        Intent intent = new Intent(ApiCode.BROADCAST_GUARD2DOG);
        intent.putExtra("timestamp", timestamp);
        mContext.sendBroadcast(intent);
    }

    /**
     * 初始化看门狗广播接收器
     */
    public void initDogBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ApiCode.BROADCAST_DOG2GUARD);
        mDogReceiver = new DogReceiver();
        mContext.registerReceiver(mDogReceiver, intentFilter);
        mDogReceiver.setReceiveListener(this);
    }

    /**
     * 解绑看门狗广播服务
     */
    public void unregisterWatchDogReceiver() {
        mContext.unregisterReceiver(mDogReceiver);
    }

    @Override
    public void setReceiveData(Long timestamp, int dataState) {
        Log.e(ApiCode.DOG_TAG, "setReceiveData: timestamp=" + timestamp + ",dataState=" + dataState);
        if (!TextUtils.isEmpty(String.valueOf(timestamp)) && !TextUtils.isEmpty(String.valueOf(dataState))) {
            mReceiveDataState = dataState;
            mThisTimestamp = timestamp;
            boolean isRun = AppUtils.isServiceRunning(mContext, ApiCode.MYAPPSERVICENAME);
            Log.e(ApiCode.DOG_TAG, "setReceiveData: isRun=" + isRun);
            switch (dataState) {
                case 0:
                    if (mServiceCore == null) {
                        return;
                    }

                    if (mServiceCore.getMyAppServiceBinder() != null) {
                        mServiceCore.getMyAppServiceBinder().changeState(true);
                    }
                    if (!isRun) {
                        mServiceCore.startAppService();
                        mServiceCore.bindAppService();
                    }
                    //startBroadcastTask();
                    break;
                case 1:
                    if (mServiceCore == null) {
                        return;
                    }

                    if (mServiceCore.getMyAppServiceBinder() != null) {
                        mServiceCore.getMyAppServiceBinder().changeState(false);
                    }
                    if (isRun) {
                        mServiceCore.stopAppService();
                        mServiceCore.unbindAppService();
                    }
                    //cancelBroadcastTask();
                    break;
            }
        }
    }

    /**
     * 拉起看门狗
     */
    private void pullWatchDog(String packageName) {
        PackageManager packageManager = mContext.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            Log.e(ApiCode.DOG_TAG, "openDog: ");
            mContext.startActivity(intent);//启动App
        }
    }

    /**
     * 启动监听看门狗定时任务
     */
    public void startReceiveTask() {
        try {
            if (mReceiveTimerTask == null || mReceiveTimer == null) {
                Log.e(ApiCode.DOG_TAG, "startReceiveTask");
                mReceiveTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (mReceiveDataState != 1) {
                            long nowTime = System.currentTimeMillis();
                            //因为全局广播发送需要耗时，所以这里增加了1秒作为延时
                            if (nowTime - mThisTimestamp > (ApiCode.BROADCAST_SECOND + 1000)) {
                                mLostBroadcastNum++;
                            } else {
                                mLostBroadcastNum = 0;
                            }
                            Log.e(ApiCode.DOG_TAG, "mReceiveTimerTask nowTime=" + nowTime + ",mThisTimestamp=" + mThisTimestamp + ",mLostBroadcastNum=" + mLostBroadcastNum);
                            if (mLostBroadcastNum > ApiCode.PUSH_GUARD_NUM) {
                                mLostBroadcastNum = 0;
                                //拉起应用
                                long start1 = System.currentTimeMillis();
                                if (AppUtils.isApkInstalled(MyApplication.myApplication, ApiCode.WATCHDOG_PACKAGENAME)) {
                                    long start2 = System.currentTimeMillis();
                                    boolean isRunningDog = isRunningAppProcesses(ApiCode.WATCHDOG_PACKAGENAME);
                                    Log.e(ApiCode.DOG_TAG, "isRunningDog=" + ApiCode.WATCHDOG_PACKAGENAME + ",costTime=" + (System.currentTimeMillis() - start2));
                                    if (!isRunningDog) {
                                        pullWatchDog(ApiCode.WATCHDOG_PACKAGENAME);
                                    }
                                } else {
                                    Log.e(ApiCode.DOG_TAG, "not installed:" + ApiCode.WATCHDOG_PACKAGENAME + ",costTime=" + (System.currentTimeMillis() - start1));
                                }
                            }
                        }
                    }
                };
                mReceiveTimer = new Timer();
                mReceiveTimer.schedule(mReceiveTimerTask, ApiCode.BROADCAST_SECOND, ApiCode.BROADCAST_SECOND);//执行定时任务
            }
        } catch (
                Exception e) {
            Log.e(ApiCode.DOG_TAG, "startTask: ", e);
        }

    }

    /**
     * 根据包名判断app是否在后台运行
     *
     * @param packageName 包名
     * @return
     */
    public  boolean isRunningAppProcesses(String packageName) {
        List<AndroidAppProcess> processes = ProcessManager.getRunningAppProcesses();
        for (AndroidAppProcess appProcess : processes) {
            if (appProcess.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据包名判断app是否在前台运行
     *
     * @param packageName 包名
     * @return
     */
    public  boolean isRunningAppFront(Context context,String packageName) {
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo: processes) {
            if (processInfo.processName.equals("com.ruitongai.mechanical")) {
                if (processInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.d("在后台", "启动");
                    return false;
                }
            }
        }
        return true;
    }




    /**
     * 取消监听看门狗定时任务
     */
    public void cancelReceiveTask() {
        try {
            if (mReceiveTimerTask != null) {
                Log.e(ApiCode.DOG_TAG, "cancelReceiveTask");
                mReceiveTimerTask.cancel();
                mReceiveTimerTask = null;
            }
            if (mReceiveTimer != null) {
                mReceiveTimer.cancel();
                mReceiveTimer = null;
            }
        } catch (Exception e) {
            Log.e(ApiCode.DOG_TAG, "cancelReceiveTask: ", e);
        }
    }

    /**
     * 启动开门狗定时广播任务
     */
    public void startBroadcastTask() {
        try {
            if (mBroadcastTimerTask == null || mBroadcastTimer == null) {
                Log.e(ApiCode.DOG_TAG, "startBoradcastTask");
                mBroadcastTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        sendBroadcastToDog();
                    }
                };
                mBroadcastTimer = new Timer();
                mBroadcastTimer.schedule(mBroadcastTimerTask, ApiCode.BROADCAST_SECOND, ApiCode.BROADCAST_SECOND);//执行定时任务
            }
        } catch (Exception e) {
            Log.e(ApiCode.DOG_TAG, "startBoradcastTask: ", e);
        }
    }

    /**
     * 取消开门狗定时广播任务
     */
    public void cancelBroadcastTask() {
        try {
            if (mBroadcastTimerTask != null) {
                Log.e(ApiCode.DOG_TAG, "cancelBoradcastTask");
                mBroadcastTimerTask.cancel();
                mBroadcastTimerTask = null;
            }
            if (mBroadcastTimer != null) {
                mBroadcastTimer.cancel();
                mBroadcastTimer = null;
            }
        } catch (Exception e) {
            Log.e(ApiCode.DOG_TAG, "cancelBoradcastTask: ", e);
        }
    }
    //endregion

}
