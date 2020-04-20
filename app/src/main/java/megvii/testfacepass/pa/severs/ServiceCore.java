package megvii.testfacepass.pa.severs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;




/**
 * description ： 所有服务启动和关闭管理类
 * email : yujiaqiang247@pingan.com.cn
 * created on : 2019-09-15 10:17
 */
public class ServiceCore {
    private static final String TAG = "ServiceCore";

    private Context mAppContext;

    public void init(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context can not be null.");
        }
        mAppContext = context.getApplicationContext();
    }

    //region APP监听服务启动和停止
    private MyAppService.LiveBinder mMyBinder;
    private boolean mIsUnbindService = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("ServiceCore", "onServiceConnected: ");
            mMyBinder = (MyAppService.LiveBinder) iBinder;
            if (mMyBinder != null) {
                mMyBinder.changeState(true);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("ServiceCore", "onServiceDisconnected: ");
            if (mMyBinder != null) {
                mMyBinder.changeState(false);
            }
        }
    };

    public MyAppService.LiveBinder getMyAppServiceBinder() {
        return mMyBinder;
    }

    /**
     * 启动APP监听服务
     */
    public void startAppService() {
        try {
            Intent intent = new Intent(mAppContext, MyAppService.class);
            mAppContext.startService(intent);
        } catch (Exception e) {
            //Toast.makeText(mAppContext, ApiCode.APPSERVICSTARTEERROR, Toast.LENGTH_SHORT).show();
            Log.e("ServiceCore", "启动ServiceCore服务异常: ", e);
        }
    }

    /**
     * 停止APP监听服务
     */
    public void stopAppService() {
        try {
            Intent intent = new Intent(mAppContext, MyAppService.class);
            mAppContext.stopService(intent);
        } catch (Exception e) {
           // Toast.makeText(mAppContext, ApiCode.APPSERVICESTOPERROR, Toast.LENGTH_SHORT).show();
            Log.e("ServiceCore", "停止ServiceCore异常: ", e);
        }
    }

    /**
     * 绑定APP监听服务
     */
    public void bindAppService() {
        try {
            mIsUnbindService = false;
            Intent intent = new Intent(mAppContext, MyAppService.class);
            mAppContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            //Toast.makeText(mAppContext, ApiCode.APPSERVICEERROR, Toast.LENGTH_SHORT).show();
            Log.e("ServiceCore", "绑定ServiceCore异常: ", e);
        }
    }

    /**
     * 解绑APP监听服务
     */
    public void unbindAppService() {
        try {
            if (!mIsUnbindService) {
                mAppContext.unbindService(connection);
                mIsUnbindService = true;
            }
        } catch (Exception e) {
            //Toast.makeText(mAppContext, ApiCode.APPSERVICEERROR, Toast.LENGTH_SHORT).show();
            Log.e("ServiceCore", "解绑ServiceCore异常: ", e);
        }
    }
    //endregion

    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////



}
