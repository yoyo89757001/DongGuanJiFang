package megvii.testfacepass.pa.ui;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.megvii.AuthApi.AuthApi;
import com.megvii.AuthApi.AuthCallback;

import org.greenrobot.eventbus.EventBus;
import java.io.File;
import java.io.IOException;
import java.util.List;
import io.objectbox.Box;
import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.R;
import megvii.testfacepass.pa.beans.BaoCunBean;
import megvii.testfacepass.pa.utils.DateUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;



public class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private static final String APP_ID = "cURlaUppZEhDZEZoaW1KQmpScXJTUjA5YzlLTmVZeWZXSmd0eFJUVG10VT0=";
    private static final String APP_KEY = "21332D4DFCD12784";
    private static final String AUTH_PRODUCT_URL = "https://biap-dev-auth.pingan.com/dev-auth-web/biap/device/v3/activeDeviceAuthInfo";
//    private static final String AUTH_METHOD = "/dev-auth-web/biap/device/v2/activeDeviceAuthInfo"; //Robin URL变为v2
    private ProgressDialog mProgressDialog;
    private BaoCunBean baoCunBean;
    private Box<BaoCunBean> baoCunBeanBox=MyApplication.myApplication.getBaoCunBeanBox();
    private static boolean isL=true;
    private AuthApi obj;
    private SharedPreferences mSharedPreferences;
    private TextView textView;


    static {
        try {
                //Robin pace_face_detect.so需要在授权之前加载
              System.loadLibrary("pace_face_detect");
        }
        catch (UnsatisfiedLinkError var1) {
            Log.e("Robin", "detection" + var1.toString());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        textView=findViewById(R.id.ttt);
        baoCunBean=baoCunBeanBox.get(123456L);
        obj = new AuthApi();
        MyApplication.myApplication.addActivity(this);
        mSharedPreferences = getSharedPreferences("SP", Context.MODE_PRIVATE);
        methodRequiresTwoPermission();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.myApplication.removeActivity(this);
    }

    private final int RC_CAMERA_AND_LOCATION=10000;

    @AfterPermissionGranted(RC_CAMERA_AND_LOCATION)
    private void methodRequiresTwoPermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.EXPAND_STATUS_BAR,
                Manifest.permission.RECEIVE_BOOT_COMPLETED, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.INTERNET};

        if (EasyPermissions.hasPermissions(this, perms)) {
            // 已经得到许可，就去做吧 //第一次授权成功也会走这个方法
//            Log.d("BaseActivity", "成功获得权限");
//            boolean bb =hasPermission();
//            if (!bb){
//                startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
//            }else {
//String deviceId = Build.SERIAL;
//            }
            start();

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "需要授予app权限,请点击确定",
                    RC_CAMERA_AND_LOCATION, perms);
        }
    }


    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1101;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS) {
            if (!hasPermission()) {
                //若用户未开启权限，则引导用户开启“Apps with usage access”权限
                startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }else {

                start();
            }
        }
    }


    //检测用户是否对本app开启了“Apps with usage access”权限
    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
        Log.d("BaseActivity", "list.size():" + list.size());

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        // ...
        for (String s:list){
            Log.d("BaseActivity", s);
        }
        Log.d("BaseActivity", "list.size():" + list.size());
        Toast.makeText(BaseActivity.this,"权限被拒绝无法正常使用app",Toast.LENGTH_LONG).show();
        finish();

    }


    private void initPaAccessControl() {

        mProgressDialog = new ProgressDialog(this);
        showDialog(true);

        boolean token;
        token = mSharedPreferences.getBoolean("token", false);
        Log.d("BaseActivity", "token:" + token);
        if (token){
            showDialog(false);
            startActivity(new Intent(BaseActivity.this,MianBanJiActivity3.class));
            finish();
        }else {
            try {

                singleCertification();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private  void singleCertification() throws IOException {
        //  String cert = readExternal(CERT_PATH).trim();
        String active="v76a87c365b73592cc15a6c532dce3d4c000036";
      String  cert="\"{\"\"serial\"\":\"\"m00420764abde7861a9499f135641501301d9\"\",\"\"key\"\":\"\"d773e499d7987dee4a3b9fe607853b364bf09504e148787d4c51e762bb029e165137f5a1b2533768ad64cde31cfc20fc0f818ac120de0cdfba564b6c052244e392c424d99ab6dd6028cc3f6a8ce8d21b7344bc1d6d4bf9e470dca6120fc3ced81b90c7899cc761b4daf30b181fb69881559f0c7689d37c8f575483ea5b353787502c2528f43b8f04181237dc9a88ddc97e4cb1ad4e56d77726a7efba9b57f07da19531c47609de8c017edac474a31e6b066f3fbc99fd01589257592ad5223ddb\"\",\"\"counting_logic\"\":\"\"\"\"}\"\n";
      if (TextUtils.isEmpty(cert)|| TextUtils.isEmpty(active)){
            EventBus.getDefault().post("授权文件为空");
            return;
        }
        Log.d("FaceInit", "cert:"+cert);
        Log.d("FaceInit","active:"+active );
        obj.authDevice(cert,active, new AuthCallback() {
            @Override
            public void onAuthResponse(final int i, final String s) {
                if(i == 0){
                    EventBus.getDefault().post("激活成功");
                    if (mSharedPreferences != null) {
                        mSharedPreferences.edit().putBoolean("token", true).apply();
                    }
                    showDialog(false);
                    startActivity(new Intent(BaseActivity.this,MianBanJiActivity3.class));
                    finish();
                }else {
                    showDialog(false);
                    Log.d("FaceInit", s);
                    Log.d("FaceInit", "i:" + i);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText("激活失败:"+i+"\n"+s);
                        }
                    });
                  //  Toast.makeText(BaseActivity.this,"激活失败:"+i+s,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showDialog(final boolean value) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(BaseActivity.this);
                }
                if (value) {
                    mProgressDialog.setCancelable(false);
                    if (!BaseActivity.this.isFinishing())
                    mProgressDialog.show();
                } else {
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    private void start(){
        //初始化
        File file = new File(MyApplication.SDPATH);
        if (!file.exists()) {
            Log.d("ggg", "file.mkdirs():" + file.mkdirs());
        }
        File file2 = new File(MyApplication.SDPATH2);
        if (!file2.exists()) {
            Log.d("ggg", "file.mkdirs():" + file2.mkdirs());
        }
        File file3 = new File(MyApplication.SDPATH3);
        if (!file3.exists()) {
            Log.d("ggg", "file.mkdirs():" + file3.mkdirs());
        }

        //开启信鸽的日志输出，线上版本不建议调用
      //  XGPushConfig.enableDebug(getApplicationContext(), true);
        //ed02bf3dc1780d644f0797a9153963b37ed570a5

 /*
        注册信鸽服务的接口
        如果仅仅需要发推送消息调用这段代码即可
        */
//        XGPushManager.registerPush(getApplicationContext(),
//                new XGIOperateCallback() {
//                    @Override
//                    public void onSuccess(Object data, int flag) {
//                        isL=false;
//                        String deviceId=null;
//                        baoCunBean.setXgToken(data+"");
//                        Log.w("MainActivity", "+++ register push sucess. token:" + data + "flag" + flag);
//
//                        if (baoCunBean.getTuisongDiZhi()==null || baoCunBean.getTuisongDiZhi().equals("")) {
//                             deviceId = GetDeviceId.getDeviceId(BaseActivity.this);
//                            if (deviceId==null){
//                                ToastUtils.show2(BaseActivity.this,"获取设备唯一标识失败");
//
//                            }else {
//                                Log.d("BaseActivity", deviceId+"设备唯一标识");
//                                baoCunBean.setTuisongDiZhi(deviceId);
//                                baoCunBeanBox.put(baoCunBean);
//                            }
//                        }else {
//                            Log.d("BaseActivity", baoCunBean.getTuisongDiZhi()+"设备唯一标识");
//                        }
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                initPaAccessControl();
//                            }
//                        });
//
//                        link_uplod(baoCunBean.getTuisongDiZhi(),data+"");
//
//                    }
//                    @Override
//                    public void onFail(Object data, int errCode, String msg) {
//                        isL=false;
//                        Log.w("MainActivity",
//                                "+++ register push fail. token:" + data
//                                        + ", errCode:" + errCode + ",msg:"
//                                        + msg);
//
//                       // ToastUtils.show2(BaseActivity.this,"注册推送失败"+msg);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                initPaAccessControl();
//                            }
//                        });
//
//                    }
//
//                });



        initPaAccessControl();
    }


    //更新信鸽token
    private void link_uplod(String deviceId,  String token){
        //	final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
        OkHttpClient okHttpClient= new OkHttpClient();
        //RequestBody requestBody = RequestBody.create(JSON, json);
        RequestBody body = new FormBody.Builder()
                .add("machineCode", deviceId+"")
                .add("machineToken",token+"")
                .build();
        Request.Builder requestBuilder = new Request.Builder()
//				.header("Content-Type", "application/json")
//				.header("user-agent","Koala Admin")
                //.post(requestBody)
                //.get()
                .post(body)
                .url(baoCunBean.getHoutaiDiZhi()+"/app/updateToken");

        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败"+e.getMessage());
                EventBus.getDefault().post("网络请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) {
                //  Log.d("AllConnects", "请求成功"+call.request().toString());
                //获得返回体
                String ss=null;
                try{
                    ResponseBody body = response.body();
                    ss=body.string().trim();
                    Log.d("AllConnects", "更新信鸽token:"+ss);


                }catch (Exception e){

                    Log.d("WebsocketPushMsg", e.getMessage()+"ttttt");
                }

            }
        });
    }

    public static class MyReceiver extends BroadcastReceiver {
        public MyReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                Intent i = new Intent(context, BaseActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
    }
}
