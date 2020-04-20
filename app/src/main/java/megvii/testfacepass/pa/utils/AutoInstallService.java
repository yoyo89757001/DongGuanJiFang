package megvii.testfacepass.pa.utils;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import megvii.testfacepass.pa.R;

/**
 * Created by asia on 10/12/16.
 */

public class AutoInstallService extends AccessibilityService {
    private static final String TAG = AutoInstallService.class.getSimpleName();

    private final Set<String> installViewSet = new HashSet<>(Arrays.asList("com.android.packageinstaller.PackageInstallerActivity",
            "com.android.packageinstaller.OppoPackageInstallerActivity","com.android.packageinstaller.InstallAppProgress",
            "com.lenovo.safecenter.install.InstallerActivity","com.lenovo.safecenter.defense.install.fragment.InstallInterceptActivity",
            "com.lenovo.safecenter.install.InstallProgress","com.lenovo.safecenter.install.InstallAppProgress",
            "com.lenovo.safecenter.defense.fragment.install.InstallInterceptActivity"));

    private final Set<String> installPkgSet = new HashSet<>(Arrays.asList("com.samsung.android.packageinstaller",
            "com.android.packageinstaller", "com.google.android.packageinstaller", "com.lenovo.safecenter", "com.lenovo.security"
            , "com.xiaomi.gamecenter"));

    private final Set<String> uninstallPkgSet = new HashSet<>(Arrays.asList("com.android.packageinstaller.UninstallAppProgress"
            , "android.app.AlertDialog"));

    boolean isInstallOrUninstall = true;

    private List<String> nodeContents;
    private List<String> completeTexts;
    private List<String> installTexts;
    private final Handler mHandler = new Handler();
    private static final int DELAY_PAGE = 320; // 页面切换时间

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onServiceConnected() {
        nodeContents = new ArrayList<>(Arrays.asList(getResources().getString(R.string.auto_service_install),
                getResources().getString(R.string.auto_service_ensure),
                getResources().getString(R.string.auto_service_next),
                getResources().getString(R.string.auto_service_exchange),
                getResources().getString(R.string.auto_service_install__material_),
                getResources().getString(R.string.auto_service_ensure__material_),
                getResources().getString(R.string.auto_service_next__material_),
                getResources().getString(R.string.auto_service_exchange__material_)));

//        completeTexts = new ArrayList<>(Arrays.asList(getResources().getString(R.string.auto_service_complete),
//                getResources().getString(R.string.auto_service_complete__material_)));
        completeTexts = new ArrayList<>(Collections.singletonList(getResources().getString(R.string.auto_service_open)));

        installTexts = new ArrayList<>(Arrays.asList(getResources().getString(R.string.auto_service_install),
                getResources().getString(R.string.auto_service_install__material_)));

        Toast.makeText(this,   "开启了安装服务", Toast.LENGTH_LONG).show();
        // 服务开启，模拟两次返回键，退出系统设置界面（实际上还应该检查当前UI是否为系统设置界面，但一想到有些厂商可能篡改设置界面，懒得适配了...）
        performGlobalAction(GLOBAL_ACTION_BACK);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        }, DELAY_PAGE);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d("ffffffffffff", event.toString());
        doAccessibilityEvent(event);
    }

    private void doAccessibilityEvent(AccessibilityEvent event) {

        String className = event.getClassName().toString();
        if (uninstallPkgSet.contains(className)) {
            isInstallOrUninstall = false;
        }

        if(installViewSet.contains(event.getClassName().toString())) {
            isInstallOrUninstall = true;
        }

        if (installViewSet.contains(event.getPackageName().toString())) {
            isInstallOrUninstall = true;
        }

        AccessibilityNodeInfo rootNodeInfo = getRootInActiveWindow();

        if (rootNodeInfo != null && isInstallOrUninstall) {
            String pkgName = (String) rootNodeInfo.getPackageName();

            if (installPkgSet.contains(pkgName)) {
                for (int i = 0; i < nodeContents.size(); i++) {
                    List<AccessibilityNodeInfo> textNodeInfo = new ArrayList<>();
                    for (int k = 0; k < completeTexts.size(); k++) {
                        textNodeInfo.addAll(rootNodeInfo.findAccessibilityNodeInfosByText(completeTexts.get(k)));
                    }

                    if (textNodeInfo.size() > 0) {
                        for (int j = 0; j < textNodeInfo.size(); j++) {
                            String text = textNodeInfo.get(j).getText().toString();
                            if (completeTexts.contains(text)) {
                                clickInstall(textNodeInfo.get(j));
                            }
                        }
                    }
                }
            }
        }

        AccessibilityNodeInfo nodeInfo = event.getSource();

        if (nodeInfo != null && isInstallOrUninstall) {
            for (int i = 0; i < nodeContents.size(); i++) {
                List<AccessibilityNodeInfo> textNodeInfo = nodeInfo.findAccessibilityNodeInfosByText(nodeContents.get(i));
                List<AccessibilityNodeInfo> installNodeInfo = new ArrayList<>();
                for (int k = 0; k < completeTexts.size(); k++) {
                    installNodeInfo.addAll(nodeInfo.findAccessibilityNodeInfosByText(installTexts.get(k)));
                }

                boolean isInstall = installNodeInfo.size() != 0;

                if (textNodeInfo != null && textNodeInfo.size() > 0) {
                    for (int j = 0; j < textNodeInfo.size(); j++) {
                        String text = textNodeInfo.get(j).getText().toString();
                        if (nodeContents.contains(text) && isInstall) {
                            clickInstall(textNodeInfo.get(j));
                        }
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void clickInstall(AccessibilityNodeInfo nodeInfo) {
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    @Override
    public void onInterrupt() {
    }


}