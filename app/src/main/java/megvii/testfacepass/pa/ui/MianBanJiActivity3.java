package megvii.testfacepass.pa.ui;


import android.animation.ValueAnimator;
import android.app.Activity;


import android.app.ActivityManager;
import android.app.PendingIntent;


import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.ConnectivityManager;


import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import android.serialport.SerialPort;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;


import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.common.pos.api.util.TPS980PosUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pingan.ai.access.common.PaAccessControlMessage;
import com.pingan.ai.access.common.PaAccessDetectConfig;
import com.pingan.ai.access.entiry.PaAccessFaceInfo;
import com.pingan.ai.access.entiry.YuvInfo;
import com.pingan.ai.access.impl.OnPaAccessDetectListener;
import com.pingan.ai.access.manager.PaAccessControl;
import com.pingan.ai.access.result.PaAccessCompareFacesResult;
import com.pingan.ai.access.result.PaAccessDetectFaceResult;
import com.pingan.ai.access.result.PaAccessMultiFaceBaseInfo;
import com.sdsmdg.tastytoast.TastyToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import java.util.Timer;
import java.util.TimerTask;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import io.objectbox.query.LazyList;
import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.R;
import megvii.testfacepass.pa.beans.BaoCunBean;
import megvii.testfacepass.pa.beans.DaKaBean;
import megvii.testfacepass.pa.beans.HuiFuBean;
import megvii.testfacepass.pa.beans.Subject;
import megvii.testfacepass.pa.beans.Subject_;
import megvii.testfacepass.pa.beans.XGBean;
import megvii.testfacepass.pa.beans.ZhiLingBean;
import megvii.testfacepass.pa.camera.CameraManager;
import megvii.testfacepass.pa.camera.CameraManager2;
import megvii.testfacepass.pa.camera.CameraPreview;
import megvii.testfacepass.pa.camera.CameraPreview2;
import megvii.testfacepass.pa.camera.CameraPreviewData;
import megvii.testfacepass.pa.camera.CameraPreviewData2;
import megvii.testfacepass.pa.dialog.MiMaDialog4;
import megvii.testfacepass.pa.severs.ServiceCore;
import megvii.testfacepass.pa.severs.WatchDogCore;
import megvii.testfacepass.pa.tuisong_jg.MyServeInterface;
import megvii.testfacepass.pa.tuisong_jg.ServerManager;
import megvii.testfacepass.pa.tuisong_jg.TSXXChuLi;

import megvii.testfacepass.pa.utils.BitmapUtil;
import megvii.testfacepass.pa.utils.ByteUtil;
import megvii.testfacepass.pa.utils.DateUtils;
import megvii.testfacepass.pa.utils.DengUT;
import megvii.testfacepass.pa.utils.FileUtil;
import megvii.testfacepass.pa.utils.GsonUtil;
import megvii.testfacepass.pa.utils.NV21ToBitmap;
import megvii.testfacepass.pa.utils.SettingVar;
import megvii.testfacepass.pa.view.DongGuanView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import top.zibin.luban.Luban;


public class MianBanJiActivity3 extends Activity implements CameraManager.CameraListener,
        CameraManager2.CameraListener2, MyServeInterface, SensorEventListener {
    @BindView(R.id.tishibg)
    ImageView tishibg;
    @BindView(R.id.gongsi)
    TextView gongsi;
    @BindView(R.id.xiping)
    ImageView xiping;
    @BindView(R.id.tishiyu)
    TextView tishiyu;
    private NetWorkStateReceiver netWorkStateReceiver = null;
    // private Box<BenDiJiLuBean> benDiJiLuBeanBox = null;
    private static boolean isLink = true;
    // private ConcurrentHashMap map = new ConcurrentHashMap();
    private Box<Subject> subjectBox = null;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    //  private long lingshiId = -1;
    private static ServerManager serverManager;
    //  private static boolean isOne = true;
    //  private static Vector<Subject> vipList = new Vector<>();//vip的弹窗
    //   private static Vector<Subject> dibuList = new Vector<>();//下面的弹窗
    //   private static Vector<Subject> shuList = new Vector<>();//下面的弹窗
    private Bitmap msrBitmap = null;
    private ServiceCore serviceCore;
//    private RequestOptions myOptions = new RequestOptions()
//            .fitCenter()
//            .error(R.drawable.erroy_bg)
//            .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
//    // .transform(new GlideRoundTransform(MainActivity.this,10));

//    private RequestOptions myOptions2 = new RequestOptions()
//            .fitCenter()
//            .error(R.drawable.erroy_bg)
//            //   .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
//            .transform(new GlideCircleTransform270(MyApplication.myApplication, 2, Color.parseColor("#ffffffff"), 270));


    private OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .writeTimeout(20000, TimeUnit.MILLISECONDS)
            .connectTimeout(20000, TimeUnit.MILLISECONDS)
            .readTimeout(20000, TimeUnit.MILLISECONDS)
//				    .cookieJar(new CookiesManager())
            //        .retryOnConnectionFailure(true)
            .build();
    private final Timer timer = new Timer();
    private TimerTask task;
    //  private DBG_View dbg_view;
    //   private static FacePassSDKMode SDK_MODE = FacePassSDKMode.MODE_OFFLINE;
    private static final String DEBUG_TAG = "FacePassDemo";
    private boolean isOpenFace = false;
    private LinkedBlockingQueue<ZhiLingBean.ResultBean> linkedBlockingQueue;
    /* 人脸识别Group */
    // private static final String group_name = "facepasstestx";

    private MediaPlayer mMediaPlayer;
    //  private WindowManager wm;
    /* SDK 实例对象 */

    /* 相机实例 */
    private CameraManager manager;
    private CameraManager2 manager2;
    /* 显示人脸位置角度信息 */
    // private XiuGaiGaoKuanDialog dialog = null;
    /* 相机预览界面 */
    private CameraPreview cameraView;
    private CameraPreview2 cameraView2;
    //   private boolean isAnXia = true;
    /* 在预览界面圈出人脸 */
    private DongGuanView faceView;
    /* 相机是否使用前置摄像头 */
    // private static boolean cameraFacingFront = true;
    // private int cameraRotation;
    private static final int cameraWidth = 720;
    private static final int cameraHeight = 640;
    // private boolean isOP = true;
    private int heightPixels;
    private int widthPixels;
    int screenState = 0;// 0 横 1 竖
    TanChuangThread tanChuangThread;
    // private ConcurrentHashMap<Long, Integer> concurrentHashMap = new ConcurrentHashMap<Long, Integer>();
    private int dw, dh;
    private Box<BaoCunBean> baoCunBeanDao = null;
    // private Box<TodayBean> todayBeanBox = null;
    private BaoCunBean baoCunBean = null;
    // private TodayBean todayBean = null;
    private IntentFilter intentFilter;
    private TimeChangeReceiver timeChangeReceiver;
    private Handler mHandler;
    //  private ClockView clockView;
    // private DiBuAdapter diBuAdapter = null;
    // private GridLayoutManager gridLayoutManager = new GridLayoutManager(MianBanJiActivity.this, 2, LinearLayoutManager.HORIZONTAL, false);
    private TSXXChuLi tsxxChuLi = null;
    //  private static boolean isSC = true;
    // private RelativeLayout linearLayout;
    // private static boolean isDH = false;
    // private static boolean isShow = true;
    //private ImageView shezhi;
    private PaAccessControl paAccessControl;
    private Float mCompareThres;
    private static String faceId = "";
    private long feature2 = -1;
    private NV21ToBitmap nv21ToBitmap;
    private SoundPool soundPool;
    //定义一个HashMap用于存放音频流的ID
    private HashMap<Integer, Integer> musicId = new HashMap<>();
    private int pp = 0;
    private Subject subjectOnly;
    private ReadThread mReadThread;
    private SerialPort mSerialPort = null;
    private InputStream mInputStream;
    //private OutputStream mOutputStream;
    private Timer mTimer;//距离感应
    private TimerTask mTimerTask;//距离感应
    private int pm = 0;
    private boolean onP1 = true, onP2 = true;
    private boolean isPM = true;
    private boolean isPM2 = true;
    private Box<DaKaBean> daKaBeanBox = MyApplication.myApplication.getDaKaBeanBox();
    private float juli = 0;
    private SensorManager sm;
    private ValueAnimator anim;
    private boolean zhishuaka = false;
    private TextView kahaos;
    private boolean isGET = true;
    private Box<HuiFuBean> huiFuBeanBox = null;
    private String JHM = "";
    private WatchDogCore mWatchDogCore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // benDiJiLuBeanBox = MyApplication.myApplication.getBenDiJiLuBeanBox();
        baoCunBeanDao = MyApplication.myApplication.getBaoCunBeanBox();
        baoCunBean = baoCunBeanDao.get(123456L);
        huiFuBeanBox = MyApplication.myApplication.getHuiFuBeanBox();
        subjectBox = MyApplication.myApplication.getSubjectBox();
        mCompareThres = baoCunBean.getShibieFaZhi() + 0.05f;
        //每分钟的广播
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);//每分钟变化
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);//设置了系统时区
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);//设置了系统时间
        timeChangeReceiver = new TimeChangeReceiver();
        registerReceiver(timeChangeReceiver, intentFilter);
        linkedBlockingQueue = new LinkedBlockingQueue<>(1);
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        EventBus.getDefault().register(this);//订阅
        MyApplication.myApplication.addActivity(this);

        //启动监听服务
        mWatchDogCore = new WatchDogCore();
        serviceCore = new ServiceCore();
        serviceCore.init(MyApplication.myApplication);
        mWatchDogCore.init(MyApplication.myApplication, serviceCore);
        mWatchDogCore.initDogBroadcastReceiver();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        dw = dm.widthPixels;
        dh = dm.heightPixels;
        tsxxChuLi = new TSXXChuLi();
        JHM = FileUtil.getSerialNumber(this) == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber(this);
        nv21ToBitmap = new NV21ToBitmap(MianBanJiActivity3.this);
        /* 初始化界面 */
        //  Log.d("MianBanJiActivity3", "jh:" + baoCunBean);
        //初始化soundPool,设置可容纳12个音频流，音频流的质量为5，
        AudioAttributes abs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)   //设置允许同时播放的流的最大值
                .setAudioAttributes(abs)   //完全可以设置为null
                .build();
        //通过load方法加载指定音频流，并将返回的音频ID放入musicId中

        musicId.put(1, soundPool.load(this, R.raw.tongguo, 1));
        musicId.put(2, soundPool.load(this, R.raw.wuquanxian, 1));
        musicId.put(3, soundPool.load(this, R.raw.xinxibupipei, 1));
        musicId.put(4, soundPool.load(this, R.raw.xianshibie, 1));
        musicId.put(5, soundPool.load(this, R.raw.shuaka, 1));
        musicId.put(6, soundPool.load(this, R.raw.moshengren, 1));
        musicId.put(7, soundPool.load(this, R.raw.chaoshi, 1));
        musicId.put(8, soundPool.load(this, R.raw.chaoshi2, 1));
        musicId.put(9, soundPool.load(this, R.raw.guoqi, 1));
        initView();

        if (baoCunBean != null) {
            zhishuaka = baoCunBean.isTianQi();

            Log.d("MianBanJiActivity3", "onCreate:" + zhishuaka);
            try {
                //PaAccessControl.getInstance().getPaAccessDetectConfig();
                initFaceConfig();
                paAccessControl = PaAccessControl.getInstance();
                paAccessControl.setOnPaAccessDetectListener(onDetectListener);
                Log.d("MianBanJiActivity3", "paAccessControl:" + paAccessControl.getVersion());
            } catch (Exception e) {
                Log.d("MianBanJiActivity3", e.getMessage() + "初始化失败");
                return;
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (paAccessControl != null)
                    paAccessControl.startFrameDetect();

            }
        }).start();


        try {
            mSerialPort = MyApplication.myApplication.getSerialPort(baoCunBean.getDangqianChengShi2());
            //mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
        } catch (Exception e) {
            Log.d("MianBanJiActivity", e.getMessage() + "dddddddd");
        }


        mReadThread = new ReadThread();
        mReadThread.start();

        tanChuangThread = new TanChuangThread();
        tanChuangThread.start();


        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 111: {
                        if (zhishuaka)
                            break;
                        Subject subject = (Subject) msg.obj;
                        if (subject.getTeZhengMa() != null) {//在库人员
                            if (subject.getIsOpen() == 0) {//开门
                                faceView.setTC(BitmapFactory.decodeFile(MyApplication.SDPATH3 + File.separator + subject.getTeZhengMa() + ".png")
                                        , subject.getName(), true);
                                isOpenFace = true;
                                stopMedie();
                                soundPool.play(musicId.get(5), 1, 1, 0, 0, 1);
                            } else {
                                faceView.setTC(BitmapFactory.decodeFile(MyApplication.SDPATH3 + File.separator + subject.getTeZhengMa() + ".png")
                                        , subject.getName(), false);
                                isOpenFace = false;
                                stopMedie();
                                soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
                            }
                        } else {//陌生人
                            isOpenFace = false;
                            faceView.setTC(BitmapUtil.rotateBitmap(msrBitmap, SettingVar.msrBitmapRotation), subject.getName(), false);
                            stopMedie();
                            soundPool.play(musicId.get(6), 1, 1, 0, 0, 1);
                        }
//                        if (subject.getIsOpen() == 1) {//1是关
//                            if (subject.getTeZhengMa() != null) {
//
//                                // Log.d("MianBanJiActivity3", "ddd1");
//
//                            } else {
//
//                            }
//                            stopMedie();
//                            soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
//                        } else {
//                            if (subject.getTeZhengMa() != null) {
//                                //  Log.d("MianBanJiActivity3", "ddd3");
//                                faceView.setTC(BitmapFactory.decodeFile(MyApplication.SDPATH3 + File.separator + subject.getTeZhengMa() + ".png")
//                                        , subject.getName(), true);
//                            } else {
//                                //  Log.d("MianBanJiActivity3", "ddd4");
//                                faceView.setTC(BitmapUtil.rotateBitmap(msrBitmap, SettingVar.msrBitmapRotation), subject.getName(), false);
//                            }
//                            stopMedie();
//                            soundPool.play(musicId.get(5), 1, 1, 0, 0, 1);
//                        }
                        break;
                    }

                    case 222: {

                        DengUT.closeDool();

                        break;
                    }
                    case 333:
                        // Log.d("MianBanJiActivity3", "333");
                        onP1 = true;
                        onP2 = true;
                        if (paAccessControl != null)
                            paAccessControl.startFrameDetect();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                xiping.setVisibility(View.GONE);
                                tishiyu.setVisibility(View.GONE);
                            }
                        });
                        if (anim != null)
                            anim.cancel();

                        break;
                    case 444:
                        onP1 = false;
                        onP2 = false;
                        if (paAccessControl != null)
                            paAccessControl.stopFrameDetect();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                xiping.setVisibility(View.VISIBLE);
                                tishiyu.setVisibility(View.VISIBLE);
                            }
                        });
                        anim = ValueAnimator.ofFloat(0, 1.0f);
                        anim.setDuration(4000);
                        anim.setRepeatMode(ValueAnimator.REVERSE);
                        anim.setRepeatCount(-1);
                        Interpolator interpolator = new LinearInterpolator();
                        anim.setInterpolator(interpolator);
                        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float currentValue = (Float) animation.getAnimatedValue();
                                tishiyu.setAlpha(currentValue);
                                // 步骤5：刷新视图，即重新绘制，从而实现动画效果
                                tishiyu.requestLayout();
                            }
                        });
                        anim.start();
                        break;
                }
                return false;
            }
        });

        //isSC = true;
        NfcManager mNfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        mNfcAdapter = mNfcManager.getDefaultAdapter();
        if (mNfcAdapter == null) {
            Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "设备不支持NFC", TastyToast.LENGTH_LONG, TastyToast.INFO);
            tastyToast.setGravity(Gravity.CENTER, 0, 0);
            tastyToast.show();
        } else if (!mNfcAdapter.isEnabled()) {
            Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "请先去设置里面打开NFC开关", TastyToast.LENGTH_LONG, TastyToast.INFO);
            tastyToast.setGravity(Gravity.CENTER, 0, 0);
            tastyToast.show();
        } else if ((mNfcAdapter != null) && (mNfcAdapter.isEnabled())) {

        }
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
        init_NFC();

        //  kaiPing();

        guanPing();//关屏

        //  String sss = "ertyui1234567890";
        //  Log.d("MianBanJiActivity3", sss.substring(6, 14));

        serverManager = new ServerManager(FileUtil.getIPAddress(getApplicationContext()), 8090);
        serverManager.setMyServeInterface(MianBanJiActivity3.this);
        serverManager.startServer();

//        boolean isOn = AppInstaller.isAccessibilitySettingsOn(MianBanJiActivity3.this);
//        if (!isOn) {
//            AppInstaller.toAccessibilityService(MianBanJiActivity3.this);
//        }

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//
//                while (ispp){
//                    SystemClock.sleep(2000);
//                    Log.d("MianBanJiActivity3", "isRunningAppFront():" + isRunningAppFront(MianBanJiActivity3.this,getPackageName()));
//                }
//            }
//        }).start();


    }

//    boolean ispp=true;


    /**
     * 根据包名判断app是否在前台运行
     * 只能判断自己
     *
     * @param packageName 包名
     * @return
     */
    public static boolean isRunningAppFront(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processes) {
            Log.d("isRunningAppFront", processInfo.processName);
            if (processInfo.processName.equals(packageName)) {
                Log.d("isRunningAppFront", "进来");
                if (processInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.d("isRunningAppFront", "启动");
                    return false;
                }
            }
        }
        return true;
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    final byte[] buffer = new byte[64];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        //  Log.d("ReadThread", "buffer.length:" + byteToString(buffer));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //   Log.d("ReadThread", "byte数组原始格式:" + Arrays.toString(buffer));
                                //   Log.d("ReadThread","byte数组转十六进制"+ bytesToHexString(buffer));
                                //48, 51, 48, 52, 53, 57, 49, 56, 52, 50, 13, 10
                                //48, 51, 48, 54, 52, 53, 52, 53, 54, 50, 13, 10
                                //48, 51, 48, 54, 52, 57, 49, 56, 52, 50, 13, 10,
                                //  Log.d("ReadThread", ByteUtil.getString(buffer, StandardCharsets.UTF_8.name()));
                                //303330343539313834320d0a
                                //303330363435343536320d0a
                                readdd(ByteUtil.getString(buffer, StandardCharsets.UTF_8.name()));
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }


    /**
     * bytes 转16进制字符串
     *
     * @param bArray
     * @return
     */
    public String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }


    private void readdd(String idid) {
        String sdfds = idid;

        Log.d("byte数组转String", sdfds);
        StringBuilder builder = new StringBuilder();
        if (sdfds != null) {
            sdfds = sdfds.substring(0, 10);
            long kk = 0;
            try {
                kk = Long.parseLong(sdfds);
            } catch (Exception e) {
                e.printStackTrace();
            }
            sdfds = Integer.toHexString((int) kk);

            if (sdfds.length() == 8) {
                for (int i = 0; i < 4; i++) {
                    String str = sdfds.substring(sdfds.length() - 2 * (i + 1), sdfds.length() - 2 * i);
                    builder.append(str);
                }
            } else {
                return;
            }
        } else {
            return;
        }
        sdfds = builder.toString();
        sdfds = sdfds.toUpperCase();
        Log.d("byte数组转String截取6到14位并大写", sdfds);
        final String finalSdfds = sdfds;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                kahaos.setText(finalSdfds + "");
            }
        });
        if (zhishuaka) {

            openCard(sdfds);

        } else {

            if (subjectOnly != null && subjectOnly.getWorkNumber() != null && !subjectOnly.getWorkNumber().equals("")) {
                //Log.d("MianBanJiActivity3", "getWorkNumber:"+subjectOnly.getWorkNumber());
                Subject subject = subjectOnly;
                if (sdfds.equals(subject.getWorkNumber())) {
                    String name = subject.getName();
                    if (isOpenFace) {//有权限
                        DengUT.openDool();
                        subjectOnly = null;
                        //启动定时器或重置定时器
                        if (task != null) {
                            task.cancel();
                            //timer.cancel();
                            task = new TimerTask() {
                                @Override
                                public void run() {

                                    Message message = new Message();
                                    message.what = 222;
                                    if (mHandler != null)
                                        mHandler.sendMessage(message);

                                }
                            };
                            timer.schedule(task, 3000);
                        } else {
                            task = new TimerTask() {
                                @Override
                                public void run() {
                                    Message message = new Message();
                                    message.what = 222;
                                    if (mHandler != null)
                                        mHandler.sendMessage(message);
                                }
                            };
                            timer.schedule(task, 3000);
                        }
                        // Log.d("MianBanJiActivity2", "开门");


                        faceView.changeTexts();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                kahaos.setText("");
                                stopMedie();
                                soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);
                            }
                        });

                        DaKaBean daKaBean = new DaKaBean();
                        daKaBean.setName(name);
                        daKaBean.setBumen(sdfds);
                        daKaBean.setDianhua(subject.getCompanyId());
                        daKaBean.setRenyuanleixing(subject.getPeopleType());
                        daKaBean.setB64(BitmapUtil.bitmapToBase64(BitmapUtil.rotateBitmap(msrBitmap, SettingVar.msrBitmapRotation)));
                        daKaBean.setTime(DateUtils.time22(System.currentTimeMillis() + ""));
                        daKaBeanBox.put(daKaBean);

                    }
                    //link_chick_IC(sdfds, name);

                } else {
                    Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "人脸信息与卡号不匹配!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    tastyToast.setGravity(Gravity.CENTER, 0, 0);
                    tastyToast.show();
                    stopMedie();
                    soundPool.play(musicId.get(3), 1, 1, 0, 0, 1);
                }
            } else {
                Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "请先刷人脸!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                tastyToast.setGravity(Gravity.CENTER, 0, 0);
                tastyToast.show();
                stopMedie();
                soundPool.play(musicId.get(4), 1, 1, 0, 0, 1);
            }
        }

    }


    public void stopMedie() {
        soundPool.stop(1);
        soundPool.stop(2);
        soundPool.stop(3);
        soundPool.stop(4);
        soundPool.stop(5);
        soundPool.stop(6);
        soundPool.stop(7);
        soundPool.stop(8);
    }


    public void openCard(String sdfds) {

        // link_chick_IC2(sdfds,"");
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    stopMedie();
//                    soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);
//                    DengUT.openDool();
//                    //启动定时器或重置定时器
//                    if (task != null) {
//                        task.cancel();
//                        //timer.cancel();
//                        task = new TimerTask() {
//                            @Override
//                            public void run() {
//
//                                Message message = new Message();
//                                message.what = 222;
//                                mHandler.sendMessage(message);
//
//                            }
//                        };
//                        timer.schedule(task, 5000);
//                    } else {
//                        task = new TimerTask() {
//                            @Override
//                            public void run() {
//                                Message message = new Message();
//                                message.what = 222;
//                                mHandler.sendMessage(message);
//                            }
//                        };
//                        timer.schedule(task, 5000);
//                    }
//
//                }
//            });


        List<Subject> subjectList = subjectBox.query().equal(Subject_.workNumber, sdfds.trim()).build().find();
        if (subjectList.size() > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stopMedie();
                    soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);
                    DengUT.openDool();
                    //启动定时器或重置定时器
                    if (task != null) {
                        task.cancel();
                        //timer.cancel();
                        task = new TimerTask() {
                            @Override
                            public void run() {

                                Message message = new Message();
                                message.what = 222;
                                if (mHandler != null)
                                    mHandler.sendMessage(message);

                            }
                        };
                        timer.schedule(task, 3000);
                    } else {
                        task = new TimerTask() {
                            @Override
                            public void run() {
                                Message message = new Message();
                                message.what = 222;
                                if (mHandler != null)
                                    mHandler.sendMessage(message);
                            }
                        };
                        timer.schedule(task, 3000);
                    }

                }
            });

        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stopMedie();
                    soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
                }
            });

        }
    }


    @Override
    protected void onResume() {
        Log.d("MianBanJiActivity3", "重新开始");
        super.onResume();

        manager.open(getWindowManager(), SettingVar.cameraId, cameraWidth, cameraHeight);//前置是1
        if (SettingVar.cameraId == 1) {
            manager2.open(getWindowManager(), 0, cameraWidth, cameraHeight, SettingVar.cameraPreviewRotation2);//最后一个参数是红外预览方向
        } else {
            if (baoCunBean.isHuoTi())
                manager2.open(getWindowManager(), 1, cameraWidth, cameraHeight, SettingVar.cameraPreviewRotation2);//最后一个参数是红外预览方向
        }


        mWatchDogCore.startBroadcastTask(); //启动开门狗定时广播任务
        mWatchDogCore.startReceiveTask(); //启动监听看门狗定时任务

        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(this.getIntent().getAction())) {
                try {
                    processIntent(this.getIntent());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(netWorkStateReceiver, filter);
        }

        if (baoCunBean.getDangqianChengShi2() != null && baoCunBean.getDangqianChengShi2().equals("天波")) {
            if (mTimerTask == null) {
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        int ret = TPS980PosUtil.getPriximitySensorStatus();
                        // Log.d("MianBanJiActivity3", "ret:" + ret);
                        if (ret == 1) {
                            isPM2 = true;
                            //有人
                            if (isPM) {
                                isPM = false;
                                onP1 = true;
                                onP2 = true;
                                pm = 0;
                                if (paAccessControl != null)
                                    paAccessControl.startFrameDetect();
                                Message message = new Message();
                                message.what = 333;
                                if (mHandler != null)
                                    mHandler.sendMessage(message);
                            }
                        } else {
                            isPM = true;
                            if (isPM2) {
                                pm++;
                                if (pm == 8) {
                                    if (paAccessControl != null)
                                        paAccessControl.stopFrameDetect();
                                    Message message = new Message();
                                    message.what = 444;
                                    if (mHandler != null)
                                        mHandler.sendMessage(message);
                                    isPM2 = false;
                                    onP1 = false;
                                    onP2 = false;
                                    pm = 0;
                                }
                            }
                        }
                    }
                };
            }
            if (mTimer == null) {
                mTimer = new Timer();
            }
            mTimer.schedule(mTimerTask, 0, 1000);
        }
        if (baoCunBean.getDangqianChengShi2() != null && baoCunBean.getDangqianChengShi2().equals("涂鸦")) {
            Sensor defaultSensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sm.registerListener(this, defaultSensor, SensorManager.SENSOR_DELAY_NORMAL);
            if (mTimerTask == null) {
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (juli > 0) {
                            isPM2 = true;
                            //有人
                            if (isPM) {
                                isPM = false;
                                onP1 = true;
                                onP2 = true;
                                pm = 0;
                                if (paAccessControl != null)
                                    paAccessControl.startFrameDetect();
                                Message message = new Message();
                                message.what = 333;
                                if (mHandler != null)
                                    mHandler.sendMessage(message);
                            }
                        } else {
                            isPM = true;
                            if (isPM2) {
                                pm++;
                                if (pm == 8) {
                                    if (paAccessControl != null)
                                        paAccessControl.stopFrameDetect();
                                    Message message = new Message();
                                    message.what = 444;
                                    if (mHandler != null)
                                        mHandler.sendMessage(message);
                                    isPM2 = false;
                                    onP1 = false;
                                    onP2 = false;
                                    pm = 0;

                                    if (DengUT.isOPENRed) {
                                        DengUT.isOPENRed = false;
                                        DengUT.closeRed();
                                    }
                                    if (DengUT.isOPENGreen) {
                                        DengUT.isOPENGreen = false;
                                        DengUT.closeGreen();
                                    }
                                    if (DengUT.isOPEN) {
                                        DengUT.isOPEN = false;
                                        DengUT.closeWrite();
                                    }
                                }
                            }
                        }
                    }
                };
            }
            if (mTimer == null) {
                mTimer = new Timer();
            }
            mTimer.schedule(mTimerTask, 0, 1000);
        }

    }

    @Override
    public void onNewIntent(Intent intent) {
        //  super.onNewIntent(intent);
        // Log.d("SheZhiActivity2", "intent:" + intent);
        try {
            processIntent(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //  Log.e("距离", "" + event.values[0]);
        juli = event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    private YuvInfo rgb, ir;

    /* 相机回调函数 */
    @Override
    public void onPictureTaken(CameraPreviewData cameraPreviewData) {
        /* 如果SDK实例还未创建，则跳过 */
        if (paAccessControl == null && onP1) {
            return;
        }
        rgb = new YuvInfo(cameraPreviewData.nv21Data, SettingVar.cameraId, SettingVar.faceRotation, cameraPreviewData.width, cameraPreviewData.height);
        if (!baoCunBean.isHuoTi()) {
            //  Log.d("MianBanJiActivity3", "进来");
            paAccessControl.offerFrameBuffer(cameraPreviewData.nv21Data, cameraPreviewData.width, cameraPreviewData.height, SettingVar.faceRotation, SettingVar.cameraId);
        }
        //  paAccessControl.offerFrameBuffer(cameraPreviewData.nv21Data, cameraPreviewData.width, cameraPreviewData.height,SettingVar.faceRotation, SettingVar.getCaneraID());
        //   Log.d("MianBanJiActivity3", "cameraPreviewData.rotation:" + cameraPreviewData.rotation+"   "+SettingVar.cameraId);
        /* 将相机预览帧转成SDK算法所需帧的格式 FacePassImage */
    }
//天波面板机 90  270

//深圳杨总面板机 270  270

//高通 90  90

    /* 相机回调函数 */
    @Override
    public void onPictureTaken2(CameraPreviewData2 cameraPreviewData) {
        /* 如果SDK实例还未创建，则跳过 */
        // Log.d("MianBanJiActivity3", "cameraPreviewData2.rotation:" + cameraPreviewData.front);
        if (paAccessControl == null && onP2) {
            return;
        }
        //  paAccessControl.offerFrameBuffer(cameraPreviewData.nv21Data, cameraPreviewData.width, cameraPreviewData.height,SettingVar.faceRotation, SettingVar.getCaneraID());
        try {
            ir = new YuvInfo(cameraPreviewData.nv21Data, cameraPreviewData.front, SettingVar.faceRotation2, cameraPreviewData.width, cameraPreviewData.height);
            if (rgb == null || !baoCunBean.isHuoTi())
                return;
            int result = paAccessControl.offerIrFrameBuffer(rgb, ir);//提供数据到队列
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Log.d("MianBanJiActivity3", "cameraPreviewData.result:" + result);
        /* 将相机预览帧转成SDK算法所需帧的格式 FacePassImage */
    }


    @Override
    public void onStarted(String ip) {
        Log.d("MianBanJiActivity3", "小服务器启动" + ip);

    }


    @Override
    public void onStopped() {
        Log.d("MianBanJiActivity3", "小服务器停止");
        serverManager = null;
    }

    @Override
    public void onException(Exception e) {
        Log.d("MianBanJiActivity3", "小服务器异常" + e);
        serverManager = null;
    }


    private class TanChuangThread extends Thread {
        boolean isRing;

        @Override
        public void run() {
            while (!isRing) {
                try {
                    //有动画 ，延迟到一秒一次
                    //if (linkedBlockingQueue.size()==0){
                    //    isGET=true;
                    //  }
                    ZhiLingBean.ResultBean commandsBean = linkedBlockingQueue.take();
                    // isLink = true;
                    switch (commandsBean.getInstructions()) {
                        case "1"://新增和修改
                        {
                            paAccessControl.stopFrameDetect();
                            Bitmap bitmap = null;
                            try {
                                bitmap = Glide.with(MyApplication.myApplication).asBitmap()
                                        .load(commandsBean.getImage())
                                        // .sizeMultiplier(0.9f)
                                        .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                        .get();
                            } catch (InterruptedException | ExecutionException e) {
                                Log.d("TanChuangThread", e.getMessage());
                            }
                            if (bitmap != null) {
                                BitmapUtil.saveBitmapToSD(bitmap, MyApplication.SDPATH3, commandsBean.getSubjectId() + ".png");
                                File filef = new File(MyApplication.SDPATH3 + File.separator + commandsBean.getSubjectId() + ".png");
                                Log.d("TanChuangThread", commandsBean.getSubjectId() + "未压缩前:filef.length():" + filef.length());
                                File file = Luban.with(MianBanJiActivity3.this).load(MyApplication.SDPATH3 + File.separator + commandsBean.getSubjectId() + ".png")
                                        .ignoreBy(300)
                                        .setTargetDir(MyApplication.SDPATH3 + File.separator)
                                        .get(MyApplication.SDPATH3 + File.separator + commandsBean.getSubjectId() + ".png");

                                if (file == null) {
                                    Log.d("TanChuangThread", "图片压缩失败");
                                    HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getSubjectId(), commandsBean.getPersonType(),
                                            "-1", "图片压缩失败", commandsBean.getId(), JHM);
                                    huiFuBeanBox.put(huiFuBean);
                                    paAccessControl.startFrameDetect();
                                    break;
                                }
                                Log.d("TanChuangThread", commandsBean.getSubjectId() + "压缩后:file.length():" + file.length());

                                //   Log.d("TanChuangThread", ""+bitmap);
                                PaAccessDetectFaceResult detectResult = paAccessControl.detectFaceByFile(file.getAbsolutePath());
                                if (detectResult != null && detectResult.message == PaAccessControlMessage.RESULT_OK) {
                                    //先查询有没有
                                    try {
//                                        PaAccessCompareFacesResult paFacePassCompareResult = paAccessControl.compareFaceToAll(detectResult.feature);
//                                        if (paFacePassCompareResult != null && paFacePassCompareResult.message == PaAccessControlMessage.RESULT_OK) {
//                                            if (paFacePassCompareResult.compareScore > 0.6f) {//有相似的人
//                                                String id = paFacePassCompareResult.id;
//                                                if (id.equals(commandsBean.getId())){
//
//                                                }
//
//                                            }
//
                                        PaAccessFaceInfo face = paAccessControl.queryFaceById(commandsBean.getSubjectId());
                                        if (face != null) {
                                            paAccessControl.deleteFaceById(face.faceId);
                                            Subject subject = subjectBox.query().equal(Subject_.teZhengMa, commandsBean.getSubjectId()).build().findUnique();
                                            if (subject != null)
                                                subjectBox.remove(subject);
                                        }
                                        paAccessControl.addFace(commandsBean.getSubjectId(), detectResult.feature, MyApplication.GROUP_IMAGE);
                                        Subject subject = new Subject();
                                        subject.setTeZhengMa(commandsBean.getSubjectId());
                                        subject.setId(System.currentTimeMillis());
                                        subject.setPeopleType(commandsBean.getPersonType() + "");//0是员工 1是访客
                                        subject.setName(commandsBean.getName());
                                        subject.setWorkNumber(commandsBean.getCardID());
                                        subject.setIsOpen(Integer.parseInt(commandsBean.getJurisdiction()));
                                        subject.setBirthday(commandsBean.getEndTime());
                                        subject.setCompanyId(commandsBean.getCompanyId());
                                        subjectBox.put(subject);

                                        Log.d("MyReceiver", "单个员工入库成功" + subject.toString());
                                        paAccessControl.startFrameDetect();
                                        HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getSubjectId(), commandsBean.getPersonType(),
                                                "0", "入库成功", commandsBean.getId(), JHM);
                                        huiFuBeanBox.put(huiFuBean);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        paAccessControl.startFrameDetect();
                                        HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getSubjectId(), commandsBean.getPersonType(),
                                                "-1", e.getMessage() + "", commandsBean.getId(), JHM);
                                        huiFuBeanBox.put(huiFuBean);

                                        Log.d("TanChuangThread", "异常" + e.getMessage());
                                    }
                                } else {
                                    paAccessControl.startFrameDetect();
                                    HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getSubjectId(), commandsBean.getPersonType(),
                                            "-1", "图片质量不合格", commandsBean.getId(), JHM);
                                    huiFuBeanBox.put(huiFuBean);
                                    Log.d("TanChuangThread", "图片质量不合格");
                                }
                            } else {
                                paAccessControl.startFrameDetect();
                                HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getSubjectId(), commandsBean.getPersonType(),
                                        "-1", "图片下载失败", commandsBean.getId(), JHM);
                                huiFuBeanBox.put(huiFuBean);

                                Log.d("TanChuangThread", "图片下载失败");
                            }
                            // isLink = false;
                            break;
                        }
                        case "2"://删除
                        {
                            paAccessControl.stopFrameDetect();
                            PaAccessFaceInfo face = paAccessControl.queryFaceById(commandsBean.getSubjectId());
                            if (face != null) {
                                paAccessControl.deleteFaceById(face.faceId);
                                Subject subject = subjectBox.query().equal(Subject_.teZhengMa, commandsBean.getSubjectId()).build().findUnique();
                                if (subject != null) {
                                    File file = new File(MyApplication.SDPATH3, subject.getTeZhengMa() + ".png");
                                    Log.d("MyService", "file删除():" + file.delete());
                                    subjectBox.remove(subject);
                                }
                                paAccessControl.startFrameDetect();
                                HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getSubjectId(),
                                        commandsBean.getPersonType(), "0", "删除成功", commandsBean.getId(), JHM);
                                huiFuBeanBox.put(huiFuBean);
                            } else {
                                paAccessControl.startFrameDetect();
                                HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getSubjectId(),
                                        commandsBean.getPersonType(), "-1", "未找到人员信息", commandsBean.getId(), JHM);
                                huiFuBeanBox.put(huiFuBean);
                            }
                            // isLink = false;
                        }
                        break;

                    }
                    // link_infoSync();
//                    while (isLink) {
//                        SystemClock.sleep(500);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // isLink = false;
                }
            }
        }

        @Override
        public void interrupt() {
            isRing = true;
            // Log.d("RecognizeThread", "中断了弹窗线程");
            super.interrupt();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MianBanJiActivity3", "暂停");
        if (mNfcAdapter != null) {
            stopNFC_Listener();
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (sm != null)
            sm.unregisterListener(this);
    }


    private void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        SharedPreferences preferences = getSharedPreferences(SettingVar.SharedPrefrence, Context.MODE_PRIVATE);
        SettingVar.isSettingAvailable = preferences.getBoolean("isSettingAvailable", SettingVar.isSettingAvailable);
        SettingVar.cameraId = preferences.getInt("cameraId", SettingVar.cameraId);
        SettingVar.faceRotation = preferences.getInt("faceRotation", SettingVar.faceRotation);
        SettingVar.cameraPreviewRotation = preferences.getInt("cameraPreviewRotation", SettingVar.cameraPreviewRotation);
        SettingVar.cameraFacingFront = preferences.getBoolean("cameraFacingFront", SettingVar.cameraFacingFront);
        SettingVar.cameraPreviewRotation2 = preferences.getInt("cameraPreviewRotation2", SettingVar.cameraPreviewRotation2);
        SettingVar.faceRotation2 = preferences.getInt("faceRotation2", SettingVar.faceRotation2);
        SettingVar.msrBitmapRotation = preferences.getInt("msrBitmapRotation", SettingVar.msrBitmapRotation);


        final int mCurrentOrientation = getResources().getConfiguration().orientation;

        if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            screenState = 1;
        } else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            screenState = 0;
        }
        setContentView(R.layout.activity_mianbanji3);

        ButterKnife.bind(this);

        ImageView shezhi = findViewById(R.id.shezhi);
        shezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MiMaDialog4 miMaDialog = new MiMaDialog4(MianBanJiActivity3.this, baoCunBean.getMima());
                WindowManager.LayoutParams params = miMaDialog.getWindow().getAttributes();
                params.width = dw;
                params.height = dh;
//              miMaDialog.getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                miMaDialog.getWindow().setAttributes(params);
                miMaDialog.show();
            }
        });


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        heightPixels = displayMetrics.heightPixels;
        widthPixels = displayMetrics.widthPixels;
        SettingVar.mHeight = heightPixels;
        SettingVar.mWidth = widthPixels;
        kahaos = findViewById(R.id.kahaos);

        /* 初始化界面 */
        faceView = findViewById(R.id.fcview);
        faceView.setwh(widthPixels, heightPixels);
        manager = new CameraManager();
        cameraView = (CameraPreview) findViewById(R.id.preview);
        manager.setPreviewDisplay(cameraView);
        /* 注册相机回调函数 */
        manager.setListener(this);

        manager2 = new CameraManager2();
        cameraView2 = findViewById(R.id.preview2);
        manager2.setPreviewDisplay(cameraView2);
        /* 注册相机回调函数 */
        manager2.setListener(this);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) tishibg.getLayoutParams();
        layoutParams.height = (int) (heightPixels * 0.2f);
        tishibg.setLayoutParams(layoutParams);
        tishibg.invalidate();


        if (baoCunBean.getWenzi1() == null) {
            gongsi.setText("请设置公司名称");
        } else {
            gongsi.setText(baoCunBean.getWenzi1());
        }

    }


    OnPaAccessDetectListener onDetectListener = new OnPaAccessDetectListener() {
        //每一帧数据的回调
        @Override
        public void onFaceDetectFrame(int message, PaAccessDetectFaceResult faceDetectFrame) {

            if (message == 1001) {

                faceId = "";
                feature2 = -1;
                //  tishi.setVisibility(View.GONE);
                if (DengUT.isOPEN || DengUT.isOPENRed || DengUT.isOPENGreen) {
                    // Log.d("MianBanJiActivity3", "进来");
                    DengUT.isOPEN = false;
                    DengUT.isOPENGreen = false;
                    DengUT.isOPENRed = false;
                    DengUT.isOpenDOR = false;
                    if (baoCunBean.getDangqianChengShi2() != null && baoCunBean.getDangqianChengShi2().equals("涂鸦"))
                        DengUT.closeWrite();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            faceView.clera();
                            kahaos.setText("");
                        }
                    });
                }
            }

        }

        @Override
        public void onFaceDetectResult(int var1, PaAccessDetectFaceResult detectResult) {
            // Log.d("Robin","detectResult : " + detectResult.facePassFrame.blurness);

            if (detectResult == null)
                return;
            // Log.d("MianBanJiActivity3", "detectResult.feature:" + detectResult.feature);
            PaAccessCompareFacesResult paFacePassCompareResult = paAccessControl.compareFaceToAll(detectResult.feature);
            if (paFacePassCompareResult == null || paFacePassCompareResult.message != PaAccessControlMessage.RESULT_OK) {
                // Log.d("MianBanJiActivity3", "没有人脸信息");
                return;
            }
            if (!DengUT.isOPEN) {
                DengUT.isOPEN = true;
                if (baoCunBean.getDangqianChengShi2() != null && baoCunBean.getDangqianChengShi2().equals("涂鸦"))
                    DengUT.openWrite();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        faceView.start();
                    }
                });
            }
            //人脸信息完整的
            String id = paFacePassCompareResult.id;
            //  Log.d("MianBanJiActivity3", "detectResult.frameHeight:" + detectResult.frameHeight);
            //  Log.d("MianBanJiActivity3", "detectResult.frameWidth:" + detectResult.frameWidth);
            //    faceView.setFace(detectResult.rectX,detectResult.rectY,detectResult.rectW,detectResult.rectH,detectResult.frameWidth,detectResult.frameHeight);
            // String gender = getGender(detectResult.gender);
            //   boolean attriButeEnable = PaAccessControl.getInstance().getPaAccessDetectConfig().isAttributeEnabled(); //Robin 是否检测了人脸属性
            //   Log.d("MianBanJiActivity3", "paFacePassCompareResult.compareScore:" + paFacePassCompareResult.compareScore);
            //百分之一误识为0.52；千分之一误识为0.56；万分之一误识为0.60 比对阈值可根据实际情况调整
            if (paFacePassCompareResult.compareScore > mCompareThres) {
                feature2 = detectResult.trackId;
                // 不相等 弹窗
                if (!id.equals(faceId)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            kahaos.setText("得分:"+paFacePassCompareResult.compareScore);
                        }
                    });
                    faceId = id;
                    Subject subject = null;
                    try {
                        subject = subjectBox.query().equal(Subject_.teZhengMa, id).build().findUnique();
                    } catch (Exception e) {
                        e.printStackTrace();
                        List<Subject> subjectList = subjectBox.query().equal(Subject_.teZhengMa, id).build().find();
                        for (int i = 0; i < subjectList.size(); i++) {
                            if (i != 0) {
                                subjectBox.remove(subjectList.get(i));
                            }
                        }
                        return;
                    }
                    if (subject != null) {
                        if (subject.getBirthday() != null && !subject.getBirthday().equals("")) {
                            if (DateUtils.date2TimeStamp(subject.getBirthday()) < System.currentTimeMillis()) {//过期了
                                //删除
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "进入时间过期!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                                        tastyToast.setGravity(Gravity.CENTER, 0, 0);
                                        tastyToast.show();
                                    }
                                });
                                stopMedie();
                                soundPool.play(musicId.get(9), 1, 1, 0, 0, 1);

//                                paAccessControl.stopFrameDetect();
//                                paAccessControl.deleteFaceById(subject.getTeZhengMa());
//                                subjectBox.remove(subject);
//                                paAccessControl.startFrameDetect();

                            } else {
                                subjectOnly = subject;
                                msrBitmap = nv21ToBitmap.nv21ToBitmap(detectResult.rgbFrame, detectResult.frameWidth, detectResult.frameHeight);
                                Message message2 = Message.obtain();
                                message2.what = 111;
                                message2.obj = subject;
                                if (mHandler != null)
                                    mHandler.sendMessage(message2);
                                if (!DengUT.isOPENGreen) {
                                    DengUT.isOPENGreen = true;
                                    if (baoCunBean.getDangqianChengShi2() != null && baoCunBean.getDangqianChengShi2().equals("涂鸦"))
                                        DengUT.openGreen();
                                }
                            }
                        } else {
                            EventBus.getDefault().post("没有查询到人员有效期");
                        }
                    } else {
                        EventBus.getDefault().post("没有查询到人员信息");
                    }
                }

            } else {
                //陌生人
                //  Log.d("MianBanJiActivity3", "陌生人"+id);
//                if (isShow)
//                tishi.setVisibility(View.VISIBLE)
                pp++;
                if (pp > 30) {
                    faceId = "";
                    pp = 0;
                    if (feature2 == -1) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                kahaos.setText("得分:"+paFacePassCompareResult.compareScore);
                            }
                        });
                        feature2 = detectResult.trackId;
                        msrBitmap = nv21ToBitmap.nv21ToBitmap(detectResult.rgbFrame, detectResult.frameWidth, detectResult.frameHeight);
                        // Bitmap bitmap = BitmapUtil.getBitmap(facePassFrame.frame, facePassFrame.frmaeWidth, facePassFrame.frameHeight, facePassFrame.frameOri);
                        // bitmap = BitmapUtil.getCropBitmap(bitmap, facePassFrame.rectX, facePassFrame.rectY, facePassFrame.rectW, facePassFrame.rectH);
                        //  tianqi_im.setImageBitmap(msrBitmap);
                        // Log.d("MianBanJiActivity3", "msrBitmap:" + msrBitmap.getWidth());
                        //  BitmapUtil.saveBitmapToSD(msrBitmap,MyApplication.SDPATH,paFacePassCompareResult.compareScore+".png");
                        Subject subject1 = new Subject();
                        //subject1.setW(bitmap.getWidth());
                        //subject1.setH(bitmap.getHeight());
                        //图片在bitmabToBytes方法里面做了循转
                        // subject1.setTxBytes(BitmapUtil.bitmabToBytes(bitmap));
                        subject1.setId(System.currentTimeMillis());
                        subject1.setName("陌生人");
                        subject1.setTeZhengMa(null);
                        subject1.setPeopleType("1");
                        subject1.setIsOpen(1);
                        subject1.setDepartmentName("没有进入权限!");
                        // linkedBlockingQueue.offer(subject1);
                        Message message2 = Message.obtain();
                        message2.what = 111;
                        message2.obj = subject1;
                        if (mHandler != null)
                            mHandler.sendMessage(message2);

                        if (!DengUT.isOPENRed) {
                            DengUT.isOPENRed = true;
                            if (baoCunBean.getDangqianChengShi2() != null && baoCunBean.getDangqianChengShi2().equals("涂鸦"))
                                DengUT.openRed();
                        }
                    } else if (feature2 != detectResult.trackId) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                kahaos.setText("得分:"+paFacePassCompareResult.compareScore);
                            }
                        });
                        faceId = "";
                        msrBitmap = nv21ToBitmap.nv21ToBitmap(detectResult.rgbFrame, detectResult.frameWidth, detectResult.frameHeight);
                        // Bitmap bitmap = BitmapUtil.getBitmap(facePassFrame.frame, facePassFrame.frmaeWidth, facePassFrame.frameHeight, facePassFrame.frameOri);
                        //  bitmap = BitmapUtil.getCropBitmap(bitmap, facePassFrame.rectX, facePassFrame.rectY, facePassFrame.rectW, facePassFrame.rectH);
                        // BitmapUtil.saveBitmapToSD(msrBitmap,MyApplication.SDPATH,paFacePassCompareResult.compareScore+".png");
                        Subject subject1 = new Subject();
                        // subject1.setW(bitmap.getWidth());
                        // subject1.setH(bitmap.getHeight());
                        //图片在bitmabToBytes方法里面做了循转
                        // subject1.setTxBytes(BitmapUtil.bitmabToBytes(bitmap));
                        subject1.setId(System.currentTimeMillis());
                        subject1.setName("陌生人");
                        subject1.setTeZhengMa(null);
                        subject1.setPeopleType("1");
                        subject1.setIsOpen(1);
                        subject1.setDepartmentName("没有进入权限");
                        Message message2 = Message.obtain();
                        message2.what = 111;
                        message2.obj = subject1;
                        if (mHandler != null)
                            mHandler.sendMessage(message2);
                        if (!DengUT.isOPENRed) {
                            DengUT.isOPENRed = true;
                            if (baoCunBean.getDangqianChengShi2() != null && baoCunBean.getDangqianChengShi2().equals("涂鸦"))
                                DengUT.openRed();
                        }
                    }
                }
            }
        }

        @Override
        public void onMultiFacesDetectFrameBaseInfo(int i, List<PaAccessMultiFaceBaseInfo> list) {

            Log.d("MianBanJiActivity3", "list.size():" + list.size());

        }
    };


    @Override
    protected void onStop() {
        Log.d("MianBanJiActivity3", "停止");
        if (paAccessControl != null) {
            paAccessControl.stopFrameDetect();
        }

        if (manager != null) {
            manager.release();
        }
        if (manager2 != null) {
            manager2.release();
        }

        mWatchDogCore.cancelBroadcastTask();//取消开门狗定时广播任务
        mWatchDogCore.cancelReceiveTask();//监听看门狗定时任务

        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d("MianBanJiActivity3", "onRestart");
        super.onRestart();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("MianBanJiActivity3", "onStart");
    }


    @Override
    protected void onDestroy() {
        Log.d("MianBanJiActivity3", "onDestroy");
        if (serverManager != null) {
            serverManager.stopServer();
            serverManager = null;
        }
        if (tanChuangThread != null) {
            tanChuangThread.isRing = true;
            tanChuangThread.interrupt();
        }
        if (mReadThread != null) {
            mReadThread.interrupt();
        }
        unregisterReceiver(timeChangeReceiver);
        unregisterReceiver(netWorkStateReceiver);

        EventBus.getDefault().unregister(this);//解除订阅
        if (manager != null) {
            manager.release();
        }
        timer.cancel();
        if (task != null)
            task.cancel();

        DengUT.closeWrite();
        DengUT.closeGreen();
        DengUT.closeRed();
        //解绑看门狗
        mWatchDogCore.unregisterWatchDogReceiver();

        super.onDestroy();

        MyApplication.myApplication.removeActivity(this);

    }

    private static final int REQUEST_CODE_CHOOSE_PICK = 1;


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            if (keyCode == KeyEvent.KEYCODE_MENU) {
//              // startActivity(new Intent(MianBanJiActivity3.this, SheZhiActivity2.class));
//              //  finish();
//            }
//
//        }
//        return super.onKeyDown(keyCode, event);
//
//    }


    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(String event) {

        if (event.equals("ditu123")) {
            // if (baoCunBean.getTouxiangzhuji() != null)
            //    daBg.setImageBitmap(BitmapFactory.decodeFile(baoCunBean.getTouxiangzhuji()));
            baoCunBean = baoCunBeanDao.get(123456L);

            //   Log.d("MainActivity101", "dfgdsgfdgfdgfdg");
            return;
        }

        if (event.equals("kaimen")) {
            menjing1();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(8000);
                    menjing2();
                }
            }).start();
            return;
        }
        if (event.equals("guanbimain")) {
            finish();
            return;
        }
        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, event, TastyToast.LENGTH_LONG, TastyToast.INFO);
        tastyToast.setGravity(Gravity.CENTER, 0, 0);
        tastyToast.show();

    }


    class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case Intent.ACTION_TIME_TICK:
                    //mianBanJiView.setTime(DateUtils.time(System.currentTimeMillis()+""));
                    // String riqi11 = DateUtils.getWeek(System.currentTimeMillis()) + "   " + DateUtils.timesTwo(System.currentTimeMillis() + "");
                    //  riqi.setTypeface(tf);
                    String xiaoshiss = DateUtils.timeMinute(System.currentTimeMillis() + "");
                    if (xiaoshiss.split(":")[0].equals("03") && xiaoshiss.split(":")[1].equals("40")) {

//                        if (serverManager != null) {
//                            serverManager.stopServer();
//                            serverManager = null;
//                        }
//                        serverManager = new ServerManager(FileUtil.getIPAddress(getApplicationContext()), 8090);
//                        serverManager.setMyServeInterface(MianBanJiActivity3.this);
//                        serverManager.startServer();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                List<Subject> subjectList = new ArrayList<>();
                                LazyList<Subject> subjectLazyList = subjectBox.query().build().findLazy();
                                for (Subject subject : subjectLazyList) {
                                    try {
                                        if (DateUtils.date2TimeStamp(subject.getBirthday()) < System.currentTimeMillis()) {
                                            subjectList.add(subject);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                paAccessControl.stopFrameDetect();
                                for (Subject ss : subjectList) {
                                    paAccessControl.deleteFaceById(ss.getTeZhengMa());
                                    subjectBox.remove(ss);
                                }
                                paAccessControl.startFrameDetect();
                                Log.d("MianBanJiActivity3", "删除成功");
                                //Log.d("MianBanJiActivity3", "数据同步：" + object.toString());
                            }
                        }).start();


                    }

                    //1分钟一次指令获取
                    try {
                        if (baoCunBean.getHoutaiDiZhi() != null && !baoCunBean.getHoutaiDiZhi().equals("")) {
                            if (isGET) {
                                isGET = false;
                                link_get_zhiling();
                            }
                        }
                    } catch (Exception e) {
                        isGET = true;
                        Log.d("TimeChangeReceiver", e.getMessage());
                    }

                    link_infoSync();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SystemClock.sleep(15000);
                            final List<DaKaBean> huiFuBeanList = daKaBeanBox.getAll();
                            if (huiFuBeanList.size() > 0) {
                                generateXml(huiFuBeanList, JHM, baoCunBean.getName() + "", baoCunBean.getWeizhi() + "");
                            }
                        }
                    }).start();

                    //   link_chick_jilu();


                    break;
                case Intent.ACTION_TIME_CHANGED:
                    //设置了系统时间
                    // Toast.makeText(context, "system time changed", Toast.LENGTH_SHORT).show();
                    break;
                case Intent.ACTION_TIMEZONE_CHANGED:
                    //设置了系统时区的action
                    //  Toast.makeText(context, "system time zone changed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    //查询是否开门
    private void link_chick_IC2(final String icid, final String name) {
        // final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = null;
        body = new FormBody.Builder()
                .add("companyId", "DG001")
                .add("ic_card", icid + "")
                .add("machine_code", FileUtil.getSerialNumber(this) == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber(this))
                .build();

        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(baoCunBean.getHoutaiDiZhi() + "/front/wisdom/app/openDoorToCard");
        // step 3：创建 Call 对象
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .writeTimeout(5000, TimeUnit.MILLISECONDS)
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
//				    .cookieJar(new CookiesManager())
                //        .retryOnConnectionFailure(true)
                .build();
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败" + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "请求超时", TastyToast.LENGTH_LONG, TastyToast.INFO);
//                        tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                        tastyToast.show();
                        stopMedie();
                        soundPool.play(musicId.get(7), 1, 1, 0, 0, 1);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("AllConnects", "请求成功" + call.request().toString());
                //获得返回体
                try {
                    ResponseBody body = response.body();
                    final String ss = body.string().trim();

                    if (ss.equals("true")) {
                        // TPS980PosUtil.setJiaJiPower(1);
                        DengUT.openDool();
                        //启动定时器或重置定时器
                        if (task != null) {
                            task.cancel();
                            //timer.cancel();
                            task = new TimerTask() {
                                @Override
                                public void run() {

                                    Message message = new Message();
                                    message.what = 222;
                                    if (mHandler != null)
                                        mHandler.sendMessage(message);

                                }
                            };
                            if (timer != null)
                                timer.schedule(task, 5000);
                        } else {
                            task = new TimerTask() {
                                @Override
                                public void run() {
                                    Message message = new Message();
                                    message.what = 222;
                                    if (mHandler != null)
                                        mHandler.sendMessage(message);
                                }
                            };
                            if (timer != null)
                                timer.schedule(task, 5000);
                        }
                        // Log.d("MianBanJiActivity2", "开门");
                        // link_chick_jilu(icid, name);
                        subjectOnly = null;
                        faceView.changeTexts();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stopMedie();
                                soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "没有进入权限", TastyToast.LENGTH_LONG, TastyToast.INFO);
                                tastyToast.setGravity(Gravity.CENTER, 0, 0);
                                tastyToast.show();
                                //soundPool.stop();
                                stopMedie();
                                soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
                            }
                        });

                    }
                    Log.d("AllConnects", "查询开门:" + ss);

                } catch (Exception e) {
                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopMedie();
                            soundPool.play(musicId.get(7), 1, 1, 0, 0, 1);
                        }
                    });
                }
            }
        });
    }

    //查询是否开门
    private void link_chick_IC(final String icid, final String name) {
        // final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = null;
        body = new FormBody.Builder()
                .add("companyId", "DG001")
                .add("ic_card", icid + "")
                .add("machine_code", FileUtil.getSerialNumber(this) == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber(this))
                .build();

        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(baoCunBean.getHoutaiDiZhi() + "/front/wisdom/app/findIcToAccessRecord");
        // step 3：创建 Call 对象
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .writeTimeout(5000, TimeUnit.MILLISECONDS)
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
//				    .cookieJar(new CookiesManager())
                //        .retryOnConnectionFailure(true)
                .build();
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                isOpenFace = false;
                Log.d("AllConnects", "请求失败" + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "请求超时", TastyToast.LENGTH_LONG, TastyToast.INFO);
//                        tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                        tastyToast.show();
                        stopMedie();
                        soundPool.play(musicId.get(7), 1, 1, 0, 0, 1);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("AllConnects", "请求成功" + call.request().toString());
                //获得返回体
                try {
                    ResponseBody body = response.body();
                    final String ss = body.string().trim();
                    isOpenFace = false;
                    if (ss.equals("true")) {
                        // TPS980PosUtil.setJiaJiPower(1);
                        DengUT.openDool();
                        //启动定时器或重置定时器
                        if (task != null) {
                            task.cancel();
                            //timer.cancel();
                            task = new TimerTask() {
                                @Override
                                public void run() {

                                    Message message = new Message();
                                    message.what = 222;
                                    mHandler.sendMessage(message);

                                }
                            };
                            timer.schedule(task, 5000);
                        } else {
                            task = new TimerTask() {
                                @Override
                                public void run() {
                                    Message message = new Message();
                                    message.what = 222;
                                    mHandler.sendMessage(message);
                                }
                            };
                            timer.schedule(task, 5000);
                        }
                        // Log.d("MianBanJiActivity2", "开门");
                        // link_chick_jilu(icid, name);
                        subjectOnly = null;
                        faceView.changeTexts();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stopMedie();
                                soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "识别成功，但是没有进入权限", TastyToast.LENGTH_LONG, TastyToast.INFO);
                                tastyToast.setGravity(Gravity.CENTER, 0, 0);
                                tastyToast.show();
                                //soundPool.stop();
                                stopMedie();
                                soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
                            }
                        });

                    }
                    Log.d("AllConnects", "查询开门:" + ss);

                } catch (Exception e) {
                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopMedie();
                            soundPool.play(musicId.get(7), 1, 1, 0, 0, 1);
                        }
                    });
                }
            }
        });
    }

    public void generateXml(final List<DaKaBean> records, final String serialnumber, String machineName, String machineAddress) {
        File file = new File(MyApplication.SDPATH, "record.xml");
        if (file.exists()) {
            file.delete();
            file = new File(MyApplication.SDPATH, "record.xml");
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            XmlSerializer xs = Xml.newSerializer();
            xs.setOutput(fos, "utf-8");
            xs.startDocument("utf-8", true);
            xs.startTag(null, "Root");
            xs.attribute(null, "serialnumber", serialnumber);

            xs.startTag(null, "List");


            for (DaKaBean record : records) {

                xs.startTag(null, "history");

                xs.startTag(null, "machineName");
                xs.text(machineName);
                xs.endTag(null, "machineName");

                xs.startTag(null, "machineAddress");
                xs.text(machineAddress);
                xs.endTag(null, "machineAddress");

                xs.startTag(null, "personName");
                String department = record.getName();
                if (department == null) {
                    department = "";
                }
                xs.text(department);
                xs.endTag(null, "personName");

                xs.startTag(null, "iamge");
                xs.text(record.getB64() + "");
                //  xs.text("kuhyuytyg");
                xs.endTag(null, "iamge");

                xs.startTag(null, "pepopleType");
                String peopleType = record.getRenyuanleixing();
                if (peopleType == null) {
                    peopleType = "";
                }
                xs.text(peopleType);
                xs.endTag(null, "pepopleType");

                xs.startTag(null, "companyId");
                xs.text(record.getDianhua() + "");
                xs.endTag(null, "companyId");

                xs.startTag(null, "icCardNo");
                xs.text(record.getBumen() + "");
                xs.endTag(null, "icCardNo");

                xs.startTag(null, "machineCode");
                xs.text(serialnumber);
                xs.endTag(null, "machineCode");

                xs.startTag(null, "recognitionTime");
                String cardId = record.getTime();
                if (cardId == null) {
                    cardId = "";
                }
                xs.text(cardId);
                xs.endTag(null, "recognitionTime");

                xs.endTag(null, "history");

            }

            xs.endTag(null, "List");

            xs.endTag(null, "Root");
            //生成xml头
            xs.endDocument();

            xs.flush();
            fos.flush();
            fos.close();

            link_chick_jilu(records);

        } catch (Exception e) {
            Log.d("FileUtil", e.getMessage() + "");
        } finally {
            Log.d("FileUtil", "执行完成");
        }
    }

    //上传识别记录
    private void link_chick_jilu(List<DaKaBean> daKaBeanList) {

//        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//        JSONArray array = new JSONArray();
//        final List<DaKaBean> huiFuBeanList = daKaBeanBox.getAll();
//        if (huiFuBeanList.size()==0)
//            return;
        //   FileUtil.generateXml(huiFuBeanList,JHM,baoCunBean.getName() + "",baoCunBean.getWeizhi() + "");

//        for (DaKaBean bean : huiFuBeanList) {
//            try {
//                JSONObject object = new JSONObject();
//                object.put("machineName", baoCunBean.getName() + "");
//                object.put("machineAddress", baoCunBean.getWeizhi() + "");
//                object.put("personName", bean.getName() + "");
//                object.put("companyId", "DG001");
//                object.put("icCardNo", bean.getBumen() + "");
//                object.put("machineCode", JHM);
//                object.put("recognitionTime", bean.getTime());
//                array.put(object);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//        Log.d("MianBanJiActivity3", "上传记录：" + array.toString());
        RequestBody fileBody = RequestBody.create(new File(MyApplication.SDPATH + File.separator + "record.xml"), MediaType.parse("application/octet-stream"));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", System.currentTimeMillis() + ".xml", fileBody)
                .build();
        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(requestBody)
                .url(baoCunBean.getHoutaiDiZhi() + "/front/wisdom/app/save_access_records2");
        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败" + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopMedie();
                        soundPool.play(musicId.get(8), 1, 1, 0, 0, 1);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("AllConnects", "请求成功" + call.request().toString());
                //获得返回体
                try {
                    ResponseBody body = response.body();
                    String ss = body.string().trim();
                    Log.d("AllConnects", "上传开门记录:" + ss);
                    JsonObject jsonObject = GsonUtil.parse(ss).getAsJsonObject();

                    if (jsonObject.get("code").getAsInt() == 0) {
                        for (DaKaBean d : daKaBeanList) {
                            daKaBeanBox.remove(d);
                        }
                    }

                } catch (Exception e) {
                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
                }
            }
        });
    }

    //信鸽信息处理
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(XGBean xgBean) {
        if (paAccessControl != null) {
            try {
                paAccessControl.stopFrameDetect();
                tsxxChuLi.setData(xgBean, MianBanJiActivity3.this, paAccessControl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    private void bofang() {

        mMediaPlayer = MediaPlayer.create(MianBanJiActivity3.this, R.raw.deng2);

        mMediaPlayer.start();

    }


    private void kaiPing() {

        Intent intent = new Intent();
        intent.setAction("LYD_SHOW_NAVIGATION_BAR");
        intent.putExtra("type", 1);
        this.sendBroadcast(intent);
        sendBroadcast(new Intent("com.android.internal.policy.impl.showNavigationBar"));
        sendBroadcast(new Intent("com.android.systemui.statusbar.phone.statusopen"));
    }


    private void guanPing() {
        Intent intent = new Intent();
        intent.setAction("LYD_SHOW_NAVIGATION_BAR");
        intent.putExtra("type", 0);
        this.sendBroadcast(intent);
        sendBroadcast(new Intent("com.android.internal.policy.impl.hideNavigationBar"));
        sendBroadcast(new Intent("com.android.systemui.statusbar.phone.statusclose"));
    }

    private void menjing1() {
        // TPS980PosUtil.setJiaJiPower(1);
        TPS980PosUtil.setRelayPower(1);
        Log.d("MianBanJiActivity3", "打开");
    }

    private void menjing2() {
        //  TPS980PosUtil.setJiaJiPower(0);
        TPS980PosUtil.setRelayPower(0);
        Log.d("MianBanJiActivity3", "关闭");
    }


    /**
     * 获取本地化后的config
     * 注册和比对使用不同的设置
     */
    private void initFaceConfig() {
        //Robin 使用比对的设置

        PaAccessDetectConfig faceDetectConfig = PaAccessControl.getInstance().getPaAccessDetectConfig();

        faceDetectConfig.setFaceConfidenceThreshold(0.85f); //检测是不是人的脸。默认使用阀值 0.85f，阈值视具体 情况而定，最大为 1。
        faceDetectConfig.setYawThreshold(30);//人脸识别角度
        faceDetectConfig.setRollThreshold(30);
        faceDetectConfig.setPitchThreshold(30);
        // 注册图片模糊度可以设置0.9f（最大值1.0）这样能让底图更清晰。比对的模糊度可以调低一点，这样能加快识别速度，识别模糊度建议设置0.1f
        faceDetectConfig.setBlurnessThreshold(0.4f);
        faceDetectConfig.faceNums = 1;
        faceDetectConfig.setMinBrightnessThreshold(30); // 人脸图像最小亮度阀值，默认为 30，数值越小越 暗，太暗会影响人脸检测和活体识别，可以根据 需求调整。
        faceDetectConfig.setMaxBrightnessThreshold(240);// 人脸图像最大亮度阀值，默认为 240，数值越大 越亮，太亮会影响人脸检测和活体识别，可以根 据需求调整。
        faceDetectConfig.setAttributeEnabled(false);//人脸属性开关，默认关闭。会检测出人脸的性别 和年龄。人脸属性的检测会消耗运算资源，可视 情况开启，未开启性别和年龄都返回-1
        faceDetectConfig.setLivenessEnabled(baoCunBean.isHuoTi());//活体开关
        faceDetectConfig.setTrackingMode(true); //Robin 跟踪模式跟踪模式，开启后会提高检脸检出率，减小检脸耗时。门禁场景推荐开启。图片检测会强制关闭
        faceDetectConfig.setIrEnabled(baoCunBean.isHuoTi()); //非Ir模式，因为是单例模式，所以最好每个界面都设置是否开启Ir模式
        faceDetectConfig.setMinScaleThreshold(0.1f);//设置最小检脸尺寸，可以用这个来控制最远检脸距离。默认采用最小值 0.1，约 1.8 米，在 640*480 的预览分辨率下，最小人脸尺寸 为(240*0.1)*(240*0.1)即 24*24。 0.2 的最远 识别距离约 1.2 米;0.3 的最远识别距离约约 0.8 米。detectFaceMinScale 取值范围 [0.1,0.3]。门禁场景推荐 0.1;手机场景推荐 0.3。
        PaAccessControl.getInstance().setPaAccessDetectConfig(faceDetectConfig);
    }


    private void init_NFC() {
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
    }

    private void stopNFC_Listener() {
        mNfcAdapter.disableForegroundDispatch(this);
    }

    public void processIntent(Intent intent) {
        String data = null;
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag == null)
            return;
        String[] techList = tag.getTechList();
        // Log.d("Mian", "tag.describeContents():" + tag.describeContents());
        byte[] ID;
        data = tag.toString();
        ID = tag.getId();
        data += "\n\nUID:\n" + byteToString(ID);
        data += "\nData format:";
        for (String tech : techList) {
            data += "\n" + tech;
        }

        String sdfds = byteToString(ID);
        if (sdfds != null) {
            sdfds = sdfds.toUpperCase();
            Log.d("MianBanJiActivity3", sdfds + "卡号");
        } else {
            return;
        }
        final String finalSdfds = sdfds;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                kahaos.setText(finalSdfds + "");
            }
        });
        if (zhishuaka) {

            openCard(sdfds);

        } else {

            if (subjectOnly != null && subjectOnly.getWorkNumber() != null && !subjectOnly.getWorkNumber().equals("")) {
                //  Log.d("MianBanJiActivity3", "getWorkNumber:"+subjectOnly.getWorkNumber());
                Subject subject = subjectOnly;
                if (sdfds.equals(subject.getWorkNumber())) {
                    if (isOpenFace) {
                        DengUT.openDool();
                        subjectOnly = null;
                        //启动定时器或重置定时器
                        if (task != null) {
                            task.cancel();
                            //timer.cancel();
                            task = new TimerTask() {
                                @Override
                                public void run() {

                                    Message message = new Message();
                                    message.what = 222;
                                    mHandler.sendMessage(message);

                                }
                            };
                            timer.schedule(task, 3000);
                        } else {
                            task = new TimerTask() {
                                @Override
                                public void run() {
                                    Message message = new Message();
                                    message.what = 222;
                                    mHandler.sendMessage(message);
                                }
                            };
                            timer.schedule(task, 3000);
                        }
                        // Log.d("MianBanJiActivity2", "开门");


                        faceView.changeTexts();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                kahaos.setText("");
                                stopMedie();
                                soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);
                            }
                        });

                        DaKaBean daKaBean = new DaKaBean();
                        daKaBean.setName(subject.getName());
                        daKaBean.setBumen(sdfds);
                        daKaBean.setDianhua(subject.getCompanyId());
                        daKaBean.setRenyuanleixing(subject.getPeopleType());
                        daKaBean.setB64(BitmapUtil.bitmapToBase64(BitmapUtil.rotateBitmap(msrBitmap, SettingVar.msrBitmapRotation)));
                        daKaBean.setTime(DateUtils.time22(System.currentTimeMillis() + ""));
                        daKaBeanBox.put(daKaBean);

                    }

//                    link_chick_IC(sdfds, name);
                } else {
                    Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "人脸信息与卡号不匹配!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    tastyToast.setGravity(Gravity.CENTER, 0, 0);
                    tastyToast.show();
                    stopMedie();
                    soundPool.play(musicId.get(3), 1, 1, 0, 0, 1);
                }
            } else {
                Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "请先刷人脸!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                tastyToast.setGravity(Gravity.CENTER, 0, 0);
                tastyToast.show();
                stopMedie();
                soundPool.play(musicId.get(4), 1, 1, 0, 0, 1);
            }
        }
    }

    /**
     * 将byte数组转化为字符串
     *
     * @param src
     * @return
     */
    public static String byteToString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            // System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

    private String byteToHexString(byte mByte) {
        String hexStr;

        hexStr = Integer.toHexString(mByte & 0xff);
        if (hexStr.length() == 1)
            hexStr = '0' + hexStr;

        return hexStr;
    }


    public class NetWorkStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

//            //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
//
//            //获得ConnectivityManager对象
//            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            //获取所有网络连接的信息
//            Network[] networks = connMgr.getAllNetworks();
//            //用于存放网络连接信息
//            StringBuilder sb = new StringBuilder();
//            //通过循环将网络信息逐个取出来
//            Log.d("MianBanJiActivity3", "networks.length:" + networks.length);
//            if (networks.length == 0) {
//                //没网
//                Log.d("MianBanJiActivity3", "没网2");
//                if (serverManager != null) {
//                    serverManager.stopServer();
//                    serverManager = null;
//                }
//            }
//            for (Network network : networks) {
//                //获取ConnectivityManager对象对应的NetworkInfo对象
//                NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
//                if (networkInfo.isConnected()) {
//                    //连接上
//
//                    if (serverManager != null) {
//                        serverManager.stopServer();
//                        serverManager = null;
//                    }
//                    serverManager = new ServerManager(FileUtil.getIPAddress(getApplicationContext()), 8090);
//                    serverManager.setMyServeInterface(MianBanJiActivity3.this);
//                    serverManager.startServer();
//                    Log.d("MianBanJiActivity3", "有网2");
//                    break;
//                }
//            }

        }
    }


    //获取指令
    private void link_get_zhiling() {
        if (baoCunBean.getHoutaiDiZhi() == null || baoCunBean.getHoutaiDiZhi().equals("")) {
            isGET = true;
            return;
        }
        RequestBody body = null;
        body = new FormBody.Builder()
                .add("company_id", "DG001")
                .add("machine_code", JHM)
                .build();

        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(baoCunBean.getHoutaiDiZhi() + "/front/wisdom/app/getInstructions");
        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //  Log.d("MianBanJiActivity3", "请求失败"+baoCunBean.getHoutaiDiZhi());
                Log.d("AllConnects", "请求失败" + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "请求指令失败，请检查网络", TastyToast.LENGTH_LONG, TastyToast.INFO);
                        tastyToast.setGravity(Gravity.CENTER, 0, 0);
                        tastyToast.show();
                    }
                });
                // if (linkedBlockingQueue.size()==0){
                isGET = true;
                // }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("AllConnects", "请求成功" + call.request().toString());
                //获得返回体
                try {
                    ResponseBody body = response.body();
                    String ss = body.string().trim();
                    Log.d("AllConnects", "获取指令:" + ss);
                    JsonObject jsonObject = GsonUtil.parse(ss).getAsJsonObject();
                    Gson gson = new Gson();

                    ZhiLingBean commandsBean = gson.fromJson(jsonObject, ZhiLingBean.class);
                    if (commandsBean != null && commandsBean.getCode() == 200) {
                        //desc : “成功”
                        for (ZhiLingBean.ResultBean resultBean : commandsBean.getResult()) {
                            linkedBlockingQueue.put(resultBean);
                        }
//                        if (linkedBlockingQueue.size()==0){
//
//                        }
                        isGET = true;
                        Log.d("MianBanJiActivity3", "获取指令linkedBlockingQueue.size():" + linkedBlockingQueue.size());
                    } else {
                        isGET = true;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "请求指令失败Code错误", TastyToast.LENGTH_LONG, TastyToast.INFO);
                                tastyToast.setGravity(Gravity.CENTER, 0, 0);
                                tastyToast.show();
                            }
                        });
                    }

                } catch (final Exception e) {
                    // if (linkedBlockingQueue.size()==0){
                    isGET = true;
                    // }
                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "请求指令失败" + e.getMessage(), TastyToast.LENGTH_LONG, TastyToast.INFO);
                            tastyToast.setGravity(Gravity.CENTER, 0, 0);
                            tastyToast.show();
                        }
                    });

                }
            }
        });
    }

    //数据同步
    private void link_infoSync() {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        final List<HuiFuBean> huiFuBeanList = huiFuBeanBox.getAll();

        JSONArray array = new JSONArray();
        if (huiFuBeanList.size() != 0) {
            for (HuiFuBean bean : huiFuBeanList) {
                try {
                    JSONObject object = new JSONObject();
                    object.put("subjectId", bean.getPepopleId());
                    object.put("personType", bean.getPepopleType());
                    object.put("type", bean.getType());
                    object.put("msg", bean.getMsg());
                    object.put("id", bean.getShortId());
                    object.put("serialnumber", JHM);
                    array.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            return;
        }
        Log.d("MianBanJiActivity3", "数据同步：" + array.toString());
        RequestBody body = RequestBody.create(array.toString(), JSON);

        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(baoCunBean.getHoutaiDiZhi() + "/front/wisdom/app/delInstructions");
        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "数据同步请求失败" + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "数据同步请求失败,请检查网络", TastyToast.LENGTH_LONG, TastyToast.INFO);
                        tastyToast.setGravity(Gravity.CENTER, 0, 0);
                        tastyToast.show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("AllConnects", "请求成功" + call.request().toString());
                //获得返回体
                try {
                    ResponseBody body = response.body();
                    String ss = body.string().trim();
                    Log.d("AllConnects", "数据同步:" + ss);
                    if (ss.equals("true")) {
                        for (HuiFuBean bean : huiFuBeanList) {
                            huiFuBeanBox.remove(bean);
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "数据同步flase", TastyToast.LENGTH_LONG, TastyToast.INFO);
                                tastyToast.setGravity(Gravity.CENTER, 0, 0);
                                tastyToast.show();
                            }
                        });
                    }
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "数据同步" + e.getMessage(), TastyToast.LENGTH_LONG, TastyToast.INFO);
                            tastyToast.setGravity(Gravity.CENTER, 0, 0);
                            tastyToast.show();
                        }
                    });
                    Log.d("WebsocketPushMsg", e.getMessage() + "数据同步");
                }
            }
        });
    }

}
