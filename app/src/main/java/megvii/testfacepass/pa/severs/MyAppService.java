package megvii.testfacepass.pa.severs;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import android.util.Log;


import androidx.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.ui.BaseActivity;

public class MyAppService extends Service {
    public static boolean isOnForeground = true;
    public static boolean isRuning = true;
    private LiveBinder liveBinder = new LiveBinder();
    //appService检测间隔
    public static final int APPSERVICE_SECOND = 50 * 1000;

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (isRuning) {
                    try {
                        Thread.sleep(APPSERVICE_SECOND);//50秒


                        Log.e("MyAppService", "MyAppService.服务运行isRuning=" + isRuning  );//返回当前线程的线程组中活动线程的数量Thread.activeCount()
                        if (isRuning) {
                            //判断是否在前台运行
                            boolean isOnForegroundNew = isRunningOnForeground(MyApplication.myApplication);
                            if (isOnForegroundNew != isOnForeground) {
                                Log.e("MyAppService", "开始启动到前台");
                                toFront();
                            }
                        } else {
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return liveBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        isRuning = false;
        super.onDestroy();
    }

    public class LiveBinder extends Binder {
        public void changeState(boolean myisRuning) {
            isRuning = myisRuning;
            Log.e("MyAppService", "MyAppService.changeState:isRuning=" + isRuning + ",myisRuning=" + myisRuning);
        }
    }

    public static boolean isRunningOnForeground(Context context) {
        try {
            ActivityManager acm = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (acm != null) {
                List<ActivityManager.RunningAppProcessInfo> runApps = acm.getRunningAppProcesses();
                if (runApps != null && !runApps.isEmpty()) {
                    for (ActivityManager.RunningAppProcessInfo app : runApps) {
                        if (app.processName.equals(context.getPackageName())) {
                            if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void toFront() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.AppTask> taskInfoList = activityManager.getAppTasks();
                for (ActivityManager.AppTask task:taskInfoList){
                    Log.d("MianBanJiActivity3", task.getTaskInfo().baseActivity.getPackageName());
                    // if (task.getTaskInfo().baseActivity)
                    if (task.getTaskInfo().baseActivity.getPackageName().equals(getPackageName())){
                        activityManager.moveTaskToFront(task.getTaskInfo().id, 0);
                        Log.d("MianBanJiActivity3", "启动app23");
                        break;
                    }
                }
            }else {
                //获取ActivityManager
                ActivityManager mAm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                //获得当前运行的task
                List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
                for (ActivityManager.RunningTaskInfo rti : taskList) {
                    //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
                    if (rti.topActivity.getPackageName().equals(getPackageName())) {
                        mAm.moveTaskToFront(rti.id, 0);
                        Log.d("MianBanJiActivity3", "启动app21");
                        break;
                    }
                }
                //若没有找到运行的task，用户结束了task或被系统释放，则重新启动SplashActivity
                Intent resultIntent = new Intent(getApplicationContext(), BaseActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(resultIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 高版本：获取顶层的activity的包名
     */
    private String getHigherPackageName() {
        String topPackageName = "";
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        //time - 1000 * 1000, time 开始时间和结束时间的设置，在这个时间范围内 获取栈顶Activity 有效
        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
        // Sort the stats by the last time used
        if (stats != null) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : stats) {
                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (!mySortedMap.isEmpty()) {
                topPackageName = Objects.requireNonNull(mySortedMap.get(mySortedMap.lastKey())).getPackageName();
                Log.e("MianBanJiActivity3Name", topPackageName);
            }
        }

        return topPackageName;
    }

}
