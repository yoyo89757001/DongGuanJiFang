package megvii.testfacepass.pa.ui;


import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
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
import android.net.Network;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;

import android.serialport.SerialPort;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;


import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import com.common.pos.api.util.posutil.TPS980PosUtil;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import com.sdsmdg.tastytoast.TastyToast;
import com.telpo.nfcpacemaker.aidl.INfcPacemaker;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import io.objectbox.query.LazyList;
import mcv.facepass.FacePassException;
import mcv.facepass.FacePassHandler;
import mcv.facepass.types.FacePassAddFaceResult;
import mcv.facepass.types.FacePassDetectionResult;
import mcv.facepass.types.FacePassImage;
import mcv.facepass.types.FacePassImageType;
import mcv.facepass.types.FacePassRecognitionResult;
import mcv.facepass.types.FacePassRecognitionResultType;
import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.R;
import megvii.testfacepass.pa.beans.BaoCunBean;
import megvii.testfacepass.pa.beans.MQPepepole;
import megvii.testfacepass.pa.beans.ResultFaceMessage;
import megvii.testfacepass.pa.beans.Subject;
import megvii.testfacepass.pa.beans.Subject_;
import megvii.testfacepass.pa.beans.TimePeriod;
import megvii.testfacepass.pa.beans.TimePeriod_;
import megvii.testfacepass.pa.camera.CameraManager;
import megvii.testfacepass.pa.camera.CameraPreview;
import megvii.testfacepass.pa.camera.CameraPreviewData;
import megvii.testfacepass.pa.dialog.MiMaDialog4;
import megvii.testfacepass.pa.tuisong_jg.MyServeInterface;
import megvii.testfacepass.pa.utils.AppUtils;
import megvii.testfacepass.pa.utils.BitmapUtil;
import megvii.testfacepass.pa.utils.ByteUtil;
import megvii.testfacepass.pa.utils.DateUtils;
import megvii.testfacepass.pa.utils.DengUT;
import megvii.testfacepass.pa.utils.FacePassUtil;
import megvii.testfacepass.pa.utils.FileUtil;
import megvii.testfacepass.pa.utils.NV21ToBitmap;
import megvii.testfacepass.pa.utils.RabbitMQUtil;
import megvii.testfacepass.pa.utils.SettingVar;
import megvii.testfacepass.pa.view.DongGuanView;

import okhttp3.OkHttpClient;

import top.zibin.luban.Luban;


public class MianBanJiActivity3 extends Activity implements CameraManager.CameraListener,
        MyServeInterface, SensorEventListener {
    @BindView(R.id.tishibg)
    ImageView tishibg;
    @BindView(R.id.gongsi)
    TextView gongsi;
    @BindView(R.id.xiping)
    ImageView xiping;
    @BindView(R.id.tishiyu)
    TextView tishiyu;
    private TextView mqshow;
    private NetWorkStateReceiver netWorkStateReceiver = null;
    private Box<Subject> subjectBox = null;
    private NfcAdapter mNfcAdapter;

    private Bitmap msrBitmap = null;

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
   // private static final String DEBUG_TAG = "FacePassDemo";
    private boolean isOpenFace = false;
    private LinkedBlockingQueue<MQPepepole> linkedBlockingQueue;
    /* 人脸识别Group */
    // private static final String group_name = "facepasstestx";
    private INfcPacemaker binder;
    //  private WindowManager wm;
    /* SDK 实例对象 */

    /* 相机实例 */
    private CameraManager manager;
   // private CameraManager2 manager2;
    /* 显示人脸位置角度信息 */
    // private XiuGaiGaoKuanDialog dialog = null;
    /* 相机预览界面 */
    private CameraPreview cameraView;
  //  private CameraPreview2 cameraView2;
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
    private TimeChangeReceiver timeChangeReceiver;
    private Handler mHandler;
    private FacePassHandler paAccessControl;
    private NV21ToBitmap nv21ToBitmap;
    private SoundPool soundPool;
    //定义一个HashMap用于存放音频流的ID
    private HashMap<Integer, Integer> musicId = new HashMap<>();
    //private int pp = 0;
    private Subject subjectOnly;
    private ReadThread mReadThread;
    private InputStream mInputStream;
    //private OutputStream mOutputStream;
    private Timer mTimer;//距离感应
    private TimerTask mTimerTask;//距离感应
    private int pm = 0;
    //private boolean onP1 = true, onP2 = true;
    private boolean isPM = true;
    private boolean isPM2 = true;
    //private Box<DaKaBean> daKaBeanBox = MyApplication.myApplication.getDaKaBeanBox();
    private float juli = 0;
    private SensorManager sm;
    private ValueAnimator anim;
    private boolean zhishuaka = false;
    private TextView kahaos;
    //private boolean isGET = true;
    //private Box<HuiFuBean> huiFuBeanBox = null;
    private Box<TimePeriod> timePeriodBox = null;
    private String JHM = "";
//    private WatchDogCore mWatchDogCore;
    private ArrayBlockingQueue<FacePassDetectionResult> mDetectResultQueue;
    private ArrayBlockingQueue<FacePassImage> mFeedFrameQueue;
    RecognizeThread mRecognizeThread;
    FeedFrameThread mFeedFrameThread;
    private ConcurrentHashMap<Long, Integer> concurrentHashMap = new ConcurrentHashMap<Long, Integer>();
   // private ImageView imageView;
   // private TimeThread timeThread;
    private int isnfc=0;
    private RabbitMQUtil rabbitMQUtil=null;
    private Channel channel_xt = null;//心跳
    private Channel channel_yw=null;//收业务信息
    private Channel channel_up = null;//上传识别结果
    private Channel channel_res = null;//执行结果回调
    private Connection mConnection;


    private  String QUEUE_NAME_YW = "que.panel.auth."+(FileUtil.getSerialNumber() == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber());
    private  String EXCHANG_NAME_YW = "ex.panel.auth."+(FileUtil.getSerialNumber() == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber());
    private  String KEY_NAME_YW = "key.panel.auth."+(FileUtil.getSerialNumber() == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber());

    //private final static String EXCHANG_NAME_XT = "ex.panel.heart."+(FileUtil.getSerialNumber() == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber());



    private  String EXCHANG_NAME_RES = "ex.panel.callback."+(FileUtil.getSerialNumber() == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber());//执行结果
    private  String KEY_NAME_RES = "key.panel.callback."+(FileUtil.getSerialNumber() == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber());//执行结果
    private  String QUEUE_NAME_RES = "que.panel.callback."+(FileUtil.getSerialNumber() == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber());//执行结果

   // private final static String EXCHANG_NAME_UP = "ex.panel.compare."+(FileUtil.getSerialNumber() == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber());//识别结果
   // private final static String KEY_NAME_UP = "key.panel.compare."+(FileUtil.getSerialNumber() == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber());//识别结果推送
   // private final static String KEY_NAME_XT = "key.panel.heart."+(FileUtil.getSerialNumber() == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber());

    private final String[][] mTechList = new String[][] { { NfcA.class.getName() }, { IsoDep.class.getName() }
            , { NfcB.class.getName() }, { NfcF.class.getName() }
    };


    @Subscribe(threadMode = ThreadMode.POSTING) //在ui线程执行
    public void onDataSynEventmq(Connection event) {
        mConnection=event;
        Log.d("MianBanJiActivity3", "MQ初始化");
        try {
            channel_xt=event.createChannel();
            channel_yw=event.createChannel();
            channel_up=event.createChannel();
            channel_res=event.createChannel();
           // channel_xt.exchangeDeclare(QUEUE_NAME_XT, "direct");//只能一方创建
           // channel_yw.exchangeDeclare(EXCHANG_NAME_YW, "direct");//只能一方创建
            Callback();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Callback(){
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            Log.d("MianBanJiActivity3", "收到业务消息:"+message);
            try {
                MQPepepole pepepole= com.alibaba.fastjson.JSONObject.parseObject(message,MQPepepole.class);
              //  Log.d("MianBanJiActivity3", pepepole.toString());
                SystemClock.sleep(1000);
                linkedBlockingQueue.put(pepepole);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("MianBanJiActivity3", e.getMessage()+"mq解析异常");
            }
        };
        try {
            /* 3.消费者关联队列 */
            channel_yw.queueDeclare(QUEUE_NAME_YW, true, false, false, null);
            /* 4.消费者绑定交换机 参数1 队列 参数2交换机 参数3 routingKey */
            channel_yw.queueBind(QUEUE_NAME_YW, EXCHANG_NAME_YW, KEY_NAME_YW);
            channel_yw.basicConsume(QUEUE_NAME_YW, true, deliverCallback, consumerTag -> {
                Log.d("MianBanJiActivity3", "取消业务消息Tag"+consumerTag);
                //  mqCaback.cancelMessage(consumerTag);
            });
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("MianBanJiActivity3", e.getMessage()+"接收异常");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // benDiJiLuBeanBox = MyApplication.myApplication.getBenDiJiLuBeanBox();
        baoCunBeanDao = MyApplication.myApplication.getBaoCunBeanBox();
        baoCunBean = baoCunBeanDao.get(123456L);
       // huiFuBeanBox = MyApplication.myApplication.getHuiFuBeanBox();
        subjectBox = MyApplication.myApplication.getSubjectBox();
        timePeriodBox=MyApplication.myApplication.getTimePeriodBox();
      //  mCompareThres = baoCunBean.getShibieFaZhi() + 0.05f;
        //每分钟的广播
        // private TodayBean todayBean = null;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);//每分钟变化
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);//设置了系统时区
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);//设置了系统时间
        timeChangeReceiver = new TimeChangeReceiver();
        registerReceiver(timeChangeReceiver, intentFilter);
        linkedBlockingQueue = new LinkedBlockingQueue<>(1);
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        EventBus.getDefault().register(this);//订阅
        MyApplication.myApplication.addActivity(this);
        mDetectResultQueue = new ArrayBlockingQueue<FacePassDetectionResult>(5);
        mFeedFrameQueue = new ArrayBlockingQueue<FacePassImage>(1);

//        String deviceId = Build.SERIAL;
//        Log.d("MianBanJiActivity3", "deviceId"+deviceId);

      //  timePeriodBox.removeAll();

        //启动监听服务
//        mWatchDogCore = new WatchDogCore();
//        serviceCore = new ServiceCore();
//        serviceCore.init(MyApplication.myApplication);
//        mWatchDogCore.init(MyApplication.myApplication, serviceCore);
//        mWatchDogCore.initDogBroadcastReceiver();

        try {
            if (baoCunBean.getDangqianChengShi2().equals("涂鸦")){
                try {
                    BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream("/sys/class/net/eth0/address")));

                    QUEUE_NAME_YW = "que.panel.auth."+input.readLine();
                    EXCHANG_NAME_YW = "ex.panel.auth."+input.readLine();
                    KEY_NAME_YW = "key.panel.auth."+input.readLine();
                    EXCHANG_NAME_RES = "ex.panel.callback."+input.readLine();//执行结果
                    KEY_NAME_RES = "key.panel.callback."+input.readLine();//执行结果
                    QUEUE_NAME_RES = "que.panel.callback."+input.readLine();//执行结果

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            SerialPort mSerialPort = MyApplication.myApplication.getSerialPort(baoCunBean.getDangqianChengShi2());
            //mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
        } catch (Exception e) {
            Log.d("MianBanJiActivity", e.getMessage() + "dddddddd");
        }
       // Log.d("MianBanJiActivity3", AppUtils.queryStorage());

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        dw = dm.widthPixels;
        dh = dm.heightPixels;
        //tsxxChuLi = new TSXXChuLi();
        JHM = FileUtil.getSerialNumber() == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber();
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
        musicId.put(2, soundPool.load(this, R.raw.meiyouquanxian, 1));
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

            //初始化sdk
            try {
                FacePassHandler.initSDK(getApplicationContext());
                FacePassUtil util = new FacePassUtil();
                util.init(MianBanJiActivity3.this, getApplicationContext(), SettingVar.faceRotation, baoCunBean);
            } catch (Exception e) {
                Log.d("MianBanJiActivity3", e.getMessage() + "初始化失败");
                return;
            }
        }




      //  timeThread =new TimeThread();
     //   timeThread.start();


        tanChuangThread = new TanChuangThread();
        tanChuangThread.start();

        mRecognizeThread = new RecognizeThread();
        mRecognizeThread.start();
        mFeedFrameThread = new FeedFrameThread();
        mFeedFrameThread.start();

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
                                try {
                                    faceView.setTC(paAccessControl.getFaceImage(subject.getTeZhengMa().getBytes()), subject.getName(), true);
                                } catch (FacePassException e) {
                                    e.printStackTrace();
                                    Log.d("MianBanJiActivity3", e.getMessage()+"弹窗异常1");
                                }
                                isOpenFace = true;
                                stopMedie();
                                soundPool.play(musicId.get(5), 1, 1, 0, 0, 1);
                            } else {
                                try {
                                    faceView.setTC(paAccessControl.getFaceImage(subject.getTeZhengMa().getBytes())
                                            , subject.getName(), false);
                                } catch (FacePassException e) {
                                    e.printStackTrace();
                                    Log.d("MianBanJiActivity3", e.getMessage()+"弹窗异常2");
                                }
                                isOpenFace = false;
                                stopMedie();
                                soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
                            }
                        } else {//陌生人
                            isOpenFace = false;
                            faceView.setTC(BitmapUtil.rotateBitmap(msrBitmap, SettingVar.msrBitmapRotation), subject.getName(), false);
                            stopMedie();
                            soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
                        }
                        break;
                    }

                    case 222: {

                        DengUT.closeDool();

                        break;
                    }
                    case 333:
                        // Log.d("MianBanJiActivity3", "333");
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
        //  kaiPing();

        guanPing();//关屏

        Intent intent = new Intent();
        intent.setAction("com.telpo.nfcpacemaker.impl.nfcservice");
        intent.setPackage("com.telpo.nfcpacemaker");
        bindService(intent, connection, BIND_AUTO_CREATE);

       // IntentFilter filter = new IntentFilter();
        //filter.addAction(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
      //  registerReceiver(mNfcBR, filter);

        NfcManager nfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        mNfcAdapter = nfcManager.getDefaultAdapter();

        manager.open(getWindowManager(), SettingVar.cameraId, cameraWidth, cameraHeight);//前置是1

        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
            IntentFilter filter2 = new IntentFilter();
            filter2.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(netWorkStateReceiver, filter2);
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
                                pm = 0;
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

                                    Message message = new Message();
                                    message.what = 444;
                                    if (mHandler != null)
                                        mHandler.sendMessage(message);
                                    isPM2 = false;
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
            mReadThread = new ReadThread();
            mReadThread.start();

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
                                pm = 0;

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

                                    Message message = new Message();
                                    message.what = 444;
                                    if (mHandler != null)
                                        mHandler.sendMessage(message);
                                    isPM2 = false;

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

        mqshow=findViewById(R.id.mqshow);


    }



//    /**
//     * 监听NFC状态
//     */
//    private BroadcastReceiver mNfcBR = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (NfcAdapter.ACTION_ADAPTER_STATE_CHANGED.equals(action)) {
//                showNfcStatus(intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, NfcAdapter.STATE_OFF));
//            }
//        }
//
//        private void showNfcStatus(int status) {
//            switch (status) {
//                case NfcAdapter.STATE_OFF:
//                   // Log.d("MianBanJiActivity3", "NFC适配器 STATE_OFF");
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            SystemClock.sleep(2500);
//                            enable();
//                        }
//                    }).start();
//                    break;
//                case NfcAdapter.STATE_TURNING_OFF:
//                    Log.d("MianBanJiActivity3", "NFC适配器 STATE_TURNING_OFF");
//                    break;
//                case NfcAdapter.STATE_TURNING_ON:
//                   Log.d("MianBanJiActivity3", "NFC适配器 STATE_TURNING_ON");
//                    break;
//                case NfcAdapter.STATE_ON:
//                    Log.d("MianBanJiActivity3", "NFC适配器 STATE_ON");
//                    break;
//            }
//        }
//    };


    /**
     * 开启NFC
     * @return
     */
    private boolean enable() {
        if (binder != null) {
            try {
                boolean isA = binder.enableNfc();
                Log.d("MianBanJiActivity3", "isA:"+isA);
              //  Log.d("TimeChangeReceiver", "binder.getNfcState()开启:" + binder.getNfcState());
                return isA;
            } catch (RemoteException var) {
                var.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 关闭NFC
     * @return
     */
    private boolean disable() {
      //  Log.d("MianBanJiActivity3", "2顶顶顶顶");
        if (binder != null) {
            try {
             //   Log.d("MianBanJiActivity3", "1顶顶顶顶");
                //  Log.d("MianBanJiActivity3", "6顶顶顶顶"+isA);
                boolean isA = binder.disableNfc();
                Log.d("TimeChangeReceiver", "binder.getNfcState():关闭" + binder.getNfcState());
                return isA;
            } catch (RemoteException var) {
                var.printStackTrace();
              //  Log.d("MianBanJiActivity3", var.getMessage()+"3顶顶顶顶");
            }
        }
        return false;
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
                            timer.schedule(task, 3200);
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
                            timer.schedule(task, 3200);
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
                        //上传识别记录
//                        DaKaBean daKaBean = new DaKaBean();
//                        daKaBean.setName(name);
//                        daKaBean.setBumen(sdfds);
//                        daKaBean.setDianhua(subject.getCompanyId());
//                        daKaBean.setRenyuanleixing(subject.getPeopleType());
//                        daKaBean.setB64(BitmapUtil.bitmapToBase64(BitmapUtil.rotateBitmap(msrBitmap, SettingVar.msrBitmapRotation)));
//                        daKaBean.setTime(DateUtils.time22(System.currentTimeMillis() + ""));
//                        daKaBeanBox.put(daKaBean);

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
        soundPool.stop(9);
    }


    public void openCard(String sdfds) {

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
                        timer.schedule(task, 3200);
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
                        timer.schedule(task, 3200);
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

            initNFC2();

//        if (SettingVar.cameraId == 1) {
//            manager2.open(getWindowManager(), 0, cameraWidth, cameraHeight, SettingVar.cameraPreviewRotation2);//最后一个参数是红外预览方向
//        } else {
//            if (baoCunBean.isHuoTi())
//                manager2.open(getWindowManager(), 1, cameraWidth, cameraHeight, SettingVar.cameraPreviewRotation2);//最后一个参数是红外预览方向
//        }
        //mWatchDogCore.startBroadcastTask(); //启动开门狗定时广播任务
      //  mWatchDogCore.startReceiveTask(); //启动监听看门狗定时任务


    }

    @Override
    public void onNewIntent(Intent intent) {
        //  super.onNewIntent(intent);
        // Log.d("SheZhiActivity2", "intent:" + intent);
        try {
            processIntent(intent);
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "NFC异常"+e.getMessage(), TastyToast.LENGTH_LONG, TastyToast.INFO);
                    tastyToast.setGravity(Gravity.CENTER, 0, 0);
                    tastyToast.show();
                }
            });
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




    /* 相机回调函数 */
    @Override
    public void onPictureTaken(CameraPreviewData cameraPreviewData) {
        /* 如果SDK实例还未创建，则跳过 */
        if (paAccessControl == null) {
            return;
        }
        /* 将相机预览帧转成SDK算法所需帧的格式 FacePassImage */
        FacePassImage image;
        try {
            image = new FacePassImage(cameraPreviewData.nv21Data, cameraPreviewData.width, cameraPreviewData.height, SettingVar.faceRotation, FacePassImageType.NV21);
        } catch (FacePassException e) {
            e.printStackTrace();
            return;
        }
        mFeedFrameQueue.offer(image);
    }


//    private class TimeThread extends Thread {
//        boolean isIterrupt;
//
//        @Override
//        public void run() {
//            while (!isIterrupt) {
//                try {
//                    //1分钟一次指令获取
//                    SystemClock.sleep(20000);
//                    try {
//                        if (baoCunBean.getHoutaiDiZhi() != null && !baoCunBean.getHoutaiDiZhi().equals("")) {
//                            if (isGET) {
//                                isGET = false;
//                                link_get_zhiling();
//                            }
//                        }
//                    } catch (Exception e) {
//                        isGET = true;
//                        Log.d("TimeChangeReceiver", e.getMessage());
//                    }
//
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            SystemClock.sleep(12000);
//                            link_infoSync();
//                            SystemClock.sleep(5000);
//                            final List<DaKaBean> huiFuBeanList = daKaBeanBox.getAll();
//                            if (huiFuBeanList.size() > 0) {
//                                generateXml(huiFuBeanList, JHM, baoCunBean.getName() + "", baoCunBean.getWeizhi() + "");
//                            }
//                        }
//                    }).start();
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        @Override
//        public void interrupt() {
//            isIterrupt = true;
//            super.interrupt();
//        }
//    }

    private class FeedFrameThread extends Thread {
        boolean isIterrupt;

        @Override
        public void run() {
            while (!isIterrupt) {
                try {
                    FacePassImage image = mFeedFrameQueue.take();
                    /* 将每一帧FacePassImage 送入SDK算法， 并得到返回结果 */
                    FacePassDetectionResult detectionResult = null;
                    detectionResult = paAccessControl.feedFrame(image);
                    // Log.d("FeedFrameThread", "1 mDetectResultQueue.size = " + mDetectResultQueue.size());
                    // Log.d("FeedFrameThread", "detectionResult:" + detectionResult);
                    if (detectionResult == null || detectionResult.faceList.length <= 0) {
                        // Log.d("FeedFrameThread", "没人"+DengUT.isOPEN);
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
                    }else {
                        /*离线模式，将识别到人脸的，message不为空的result添加到处理队列中*/
                        if (detectionResult.message.length != 0) {
                            //  Log.d("FeedFrameThread", "插入");
                            mDetectResultQueue.offer(detectionResult);

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
                    }
                    //     }

                } catch (InterruptedException | FacePassException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void interrupt() {
            isIterrupt = true;
            super.interrupt();
        }
    }

    private class RecognizeThread extends Thread {

        boolean isInterrupt;

        @Override
        public void run() {
            while (!isInterrupt) {
                try {
                    FacePassDetectionResult detectionResult = mDetectResultQueue.take();
                    // byte[] detectionResult = mDetectResultQueue.take();
                    FacePassRecognitionResult[] recognizeResult = paAccessControl.recognize("facepasstestx", detectionResult.message);
                    //   Log.d("RecognizeThread", "识别线程");
                    if (recognizeResult != null && recognizeResult.length > 0) {
                        // Log.d("RecognizeThread", "recognizeResult.length:" + recognizeResult.length);
                        for (FacePassRecognitionResult result : recognizeResult) {
                            //  Log.d("RecognizeThread", "result.trackId:" + result.trackId);
                            //String faceToken = new String(result.faceToken);
                            //   Log.d("RecognizeThread", "paAccessControl.getConfig().searchThreshold:" + paAccessControl.getConfig().searchThreshold);
                            if (FacePassRecognitionResultType.RECOG_OK == result.facePassRecognitionResultType) {
                                //识别的

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        kahaos.setText("得分:"+result.detail.searchScore);
                                    }
                                });
                                Subject subject = null;
                                try {
                                    subject = subjectBox.query().equal(Subject_.teZhengMa, new String(result.faceToken)).build().findUnique();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.d("RecognizeThread", e.getMessage()+"识别异常");
                                    List<Subject> subjectList = subjectBox.query().equal(Subject_.teZhengMa, new String(result.faceToken)).build().find();
                                    for (int i = 0; i < subjectList.size(); i++) {
                                        if (i != 0) {
                                            subjectBox.remove(subjectList.get(i));
                                        }
                                    }
                                    paAccessControl.reset();
                                    return;
                                }
                             //   Log.d("RecognizeThread", "subject:" + subject);
                                if (subject != null) {
                                    List<TimePeriod> timePeriodList=timePeriodBox.query().equal(TimePeriod_.sid,subject.getSid()).build().find();
                                    if (timePeriodList.size()<=0){
                                       // removePepole(subject);
                                        stopMedie();
                                        soundPool.play(musicId.get(9), 1, 1, 0, 0, 1);
                                        return;
                                    }else {
                                        boolean isGQ=false,isSC=true;
                                        long time=System.currentTimeMillis();
                                        for (TimePeriod timePeriod : timePeriodList) {
                                            long minTime= Long.parseLong(timePeriod.getTime().split("T")[0]);
                                            long maxTime= Long.parseLong(timePeriod.getTime().split("T")[1]);
                                            if (time>=minTime && time<=maxTime){//在范围内
                                                isGQ=true;
                                                subjectOnly = subject;
                                                for (int i = 0; i < detectionResult.faceList.length; i++) {
                                                    FacePassImage images = detectionResult.images[i];
                                                    if (images.trackId == result.trackId) {
                                                        msrBitmap = nv21ToBitmap.nv21ToBitmap(images.image, images.width, images.height);
                                                        break;
                                                    }
                                                }
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
                                                break;
                                            }else {
                                                if (minTime>time){
                                                    isSC=false;
                                                }
                                            }
                                        }
                                        if (!isGQ){
                                            if (isSC){
                                                removePepole(subject);
                                            }
                                            stopMedie();
                                            soundPool.play(musicId.get(9), 1, 1, 0, 0, 1);
                                            //删掉该人员
                                        }
                                    }

                                } else {
                                    EventBus.getDefault().post("没有查询到人员信息");
                                }
                            } else {
                                // Log.d("RecognizeThread", "未识别");
                                //未识别的
                                // 防止concurrentHashMap 数据过多 ,超过一定数据 删除没用的
                                if (concurrentHashMap.size() > 20) {
                                    concurrentHashMap.clear();
                                }
                                if (concurrentHashMap.get(result.trackId) == null) {
                                    //找不到新增
                                    concurrentHashMap.put(result.trackId, 1);
                                } else {
                                    //找到了 把value 加1
                                    concurrentHashMap.put(result.trackId, (concurrentHashMap.get(result.trackId)) + 1);
                                }
                                //判断次数超过3次
                                if (concurrentHashMap.get(result.trackId) == 5) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            kahaos.setText("得分:"+result.detail.searchScore);
                                        }
                                    });
                                    for (int i = 0; i < detectionResult.faceList.length; i++) {
                                        FacePassImage images = detectionResult.images[i];
                                        if (images.trackId == result.trackId) {
                                           // sendAsyncMessage_Rest("-1",nv21ToBitmap.nv21ToBitmap(images.image, images.width, images.height),0,3,null,null,1,2);
                                            msrBitmap = nv21ToBitmap.nv21ToBitmap(images.image, images.width, images.height);
                                            sendAsyncMessage_Rest("-1",msrBitmap,0,3,null,"",1,2,0,0);
                                            break;
                                        }
                                    }
                                    Subject subject1 = new Subject();
                                    //subject1.setW(bitmap.getWidth());
                                    //subject1.setH(bitmap.getHeight());
                                    //图片在bitmabToBytes方法里面做了循转
                                    // subject1.setTxBytes(BitmapUtil.bitmabToBytes(bitmap));
                                    subject1.setId(System.currentTimeMillis());
                                    subject1.setName("无权限");
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
                                }
                            }

                        }
                    }

                } catch (InterruptedException | FacePassException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void interrupt() {
            isInterrupt = true;
            super.interrupt();
        }

    }


    private void removePepole(Subject subject){
        try {
            List<TimePeriod> timePeriodList=timePeriodBox.query().equal(TimePeriod_.sid,subject.getSid()).build().find();
            for (TimePeriod timePeriod : timePeriodList) {
                timePeriodBox.remove(timePeriod);
            }
            paAccessControl.deleteFace(subject.getTeZhengMa().getBytes());
            subjectBox.remove(subject);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onStarted(String ip) {
        Log.d("MianBanJiActivity3", "小服务器启动" + ip);

    }


    @Override
    public void onStopped() {
        Log.d("MianBanJiActivity3", "小服务器停止");
        //serverManager = null;
    }

    @Override
    public void onException(Exception e) {
        Log.d("MianBanJiActivity3", "小服务器异常" + e);
       // serverManager = null;
    }


    private class TanChuangThread extends Thread {
        boolean isRing;

        @Override
        public void run() {
            while (!isRing) {
                try {

                    MQPepepole commandsBean = linkedBlockingQueue.take();
                    // isLink = true;
                    switch (commandsBean.getInstructions()) {
                        case 1://新增和修改
                        {
                            Bitmap bitmap = null;
                            try {
                                bitmap = Glide.with(MyApplication.myApplication).asBitmap()
                                        .load(commandsBean.getVisitorImg())
                                        // .sizeMultiplier(0.9f)
                                        .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                        .get();
                            } catch (InterruptedException | ExecutionException e) {
                                Log.d("TanChuangThread", e.getMessage());
                               // sendAsyncMessage_Rest(commandsBean.getFaceId(),null,0,commandsBean.getVisitorType(),commandsBean.getBatchCode(),null,1,1);
                            }
                            if (bitmap != null) {
                                BitmapUtil.saveBitmapToSD(bitmap, MyApplication.SDPATH3, commandsBean.getFaceId() + ".png");
                                File filef = new File(MyApplication.SDPATH3 + File.separator + commandsBean.getFaceId() + ".png");
                                Log.d("TanChuangThread", commandsBean.getFaceId() + "未压缩前:filef.length():" + filef.length());
                                File file = Luban.with(MianBanJiActivity3.this).load(MyApplication.SDPATH3 + File.separator + commandsBean.getFaceId() + ".png")
                                        .ignoreBy(600)
                                        .setTargetDir(MyApplication.SDPATH3 + File.separator)
                                        .get(MyApplication.SDPATH3 + File.separator + commandsBean.getFaceId() + ".png");
                                if (file == null) {
                                    Log.d("TanChuangThread", "图片压缩失败");
                                    sendAsyncMessage_Rest(commandsBean.getFaceId(),null,0,commandsBean.getVisitorType(),commandsBean.getBatchCode(),null,1,1,0,0);
                                    break;
                                }
                                Log.d("TanChuangThread", commandsBean.getFaceId() + "压缩后:file.length():" + file.length());
                                FacePassAddFaceResult detectResult = null;
                                detectResult = paAccessControl.addFace(BitmapFactory.decodeFile(file.getAbsolutePath()));
                                if (detectResult != null && detectResult.result==0) {
                                    //先查询有没有
                                    try {
                                        byte [] faceToken=detectResult.faceToken;
                                        Subject subject2 = subjectBox.query().equal(Subject_.sid,commandsBean.getFaceId()).build().findUnique();
                                        if (subject2 != null) {//覆盖
                                            //取出所有时间段
                                            paAccessControl.deleteFace(subject2.getTeZhengMa().getBytes());
                                            paAccessControl.bindGroup("facepasstestx",faceToken);
                                            subject2.setTeZhengMa(new String(faceToken));
                                            subject2.setName(commandsBean.getVisitorName());
                                            subject2.setWorkNumber(commandsBean.getIcCard().toUpperCase());
                                            subject2.setDaka(commandsBean.getVisitorType());
                                            subject2.setIsOpen(0);
                                           // subject2.setBirthday(commandsBean.getAuthEndDate());
                                            subjectBox.put(subject2);
                                            Log.d("MyReceiver", "单个员工修改成功" + subject2.toString());
                                        }else {
                                            Subject subject = new Subject();
                                            subject.setTeZhengMa(new String(faceToken));
                                            subject.setId(System.currentTimeMillis());
                                            subject.setSid(commandsBean.getFaceId()+"");
                                            subject.setName(commandsBean.getVisitorName());
                                            subject.setWorkNumber(commandsBean.getIcCard().toUpperCase());
                                            subject.setIsOpen(0);
                                            subject.setDaka(commandsBean.getVisitorType());
                                            subjectBox.put(subject);
                                            paAccessControl.bindGroup("facepasstestx",faceToken);
                                            Log.d("MyReceiver", "单个员工新增成功" + subject.toString());
                                        }
                                        List<TimePeriod> timePeriodList=timePeriodBox.query().equal(TimePeriod_.sid,commandsBean.getFaceId()).build().find();
                                        //Log.d("TanChuangThread", "timePeriodList.size():" + timePeriodList.size());
                                        if (timePeriodList.size()<=0){//没有直接添加时间段
                                            try {
                                                String tt=DateUtils.dateToStamp(commandsBean.getAuthStartDate())+"T"+DateUtils.dateToStamp(commandsBean.getAuthEndDate());
                                                TimePeriod timePeriod=new TimePeriod();
                                                timePeriod.setSid(commandsBean.getFaceId());
                                                timePeriod.setTime(tt);
                                                timePeriodBox.put(timePeriod);
                                                Log.d("TanChuangThread", "添加时间段"+tt);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                                sendAsyncMessage_Rest(commandsBean.getFaceId(),null,0,commandsBean.getVisitorType(),commandsBean.getBatchCode(),null,1,1,0,0);
                                                return;
                                            }
                                        }else {//有时间段，取出来判断
                                            try {
                                                Log.d("TanChuangThread", "传过来的开始时间"+commandsBean.getAuthStartDate());
                                                Log.d("TanChuangThread", "传过来的结束时间"+commandsBean.getAuthEndDate());
                                                long startTime=DateUtils.dateToStamp(commandsBean.getAuthStartDate());
                                                long endTime=DateUtils.dateToStamp(commandsBean.getAuthEndDate());
                                                for (TimePeriod timePeriod : timePeriodList) {
                                                    String[] ti =timePeriod.getTime().split("T");
//                                                    Log.d("TanChuangThread", "需要修改的开始时间段"+DateUtils.time22(ti[0]));
//                                                    Log.d("TanChuangThread", "需要修改的结束时间段"+DateUtils.time22(ti[1]));
                                                    long minTime= Long.parseLong(ti[0]);
                                                    long maxTime= Long.parseLong(ti[1]);
                                                    if ((startTime>=minTime && endTime<=maxTime)){//A在B的范围内，或者B在A的范围内
                                                        Log.d("TanChuangThread", "在已存在本地数据库的范围内,不用改变");
                                                    }else if ((minTime>=startTime && maxTime<=endTime)){//在传过来的时间范围内，取传过来的时间段做最新的值
                                                        timePeriod.setTime(startTime+"T"+endTime);
                                                        timePeriodBox.put(timePeriod);
                                                      //  Log.d("TanChuangThread", "在传过来的时间范围内，取传过来的时间段做最新的值");
                                                    } else if (endTime<minTime || startTime>maxTime){//不在这个时间段的范围内。新增一个时间段，否则合并时间段
                                                        String tt=startTime+"T"+endTime;
                                                        TimePeriod timePeriod2=new TimePeriod();
                                                        timePeriod2.setSid(commandsBean.getFaceId());
                                                        timePeriod2.setTime(tt);
                                                        List<TimePeriod> periodList=timePeriodBox.query().equal(TimePeriod_.time,tt).build().find();
                                                        boolean isCR=true;
                                                        for (TimePeriod period : periodList) {
                                                            if (period.getSid().equals(timePeriod.getSid())){
                                                                isCR=false;
                                                                break;
                                                            }
                                                        }
                                                        if (isCR){
                                                            timePeriodBox.put(timePeriod2);
                                                            Log.d("TanChuangThread", "不在这个时间段的范围内。新增一个时间段");
                                                        }else {
                                                            Log.d("TanChuangThread", "不在这个时间段的范围内。但是这个人存在相同的时间段");
                                                        }
                                                    }else if ( endTime<maxTime  ){//在左边交集，取左边最小，和右边最大
                                                        timePeriod.setTime(startTime+"T"+maxTime);
                                                        timePeriodBox.put(timePeriod);
                                                        Log.d("TanChuangThread", "在左边交集");
                                                       // break;
                                                    }else {//在左边交集，取左边最小，和右边最大
                                                        timePeriod.setTime(minTime+"T"+endTime);
                                                        timePeriodBox.put(timePeriod);
                                                        Log.d("TanChuangThread", "在右边交集");
                                                    }
                                                }
                                            }catch (Exception e){
                                                e.printStackTrace();
                                                sendAsyncMessage_Rest(commandsBean.getFaceId(),null,0,commandsBean.getVisitorType(),commandsBean.getBatchCode(),null,1,1,0,0);
                                                return;
                                            }
                                        }
                                        sendAsyncMessage_Rest(commandsBean.getFaceId(),null,1,commandsBean.getVisitorType(),commandsBean.getBatchCode(),null,1,1,0,0);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        sendAsyncMessage_Rest(commandsBean.getFaceId(),null,0,commandsBean.getVisitorType(),commandsBean.getBatchCode(),null,1,1,0,0);
                                        Log.d("TanChuangThread", "异常" + e.getMessage());
                                    }
                                } else {
                                    sendAsyncMessage_Rest(commandsBean.getFaceId(),null,0,commandsBean.getVisitorType(),commandsBean.getBatchCode(),null,1,1,0,0);
                                    Log.d("TanChuangThread", "图片质量不合格");
                                }
                            } else {
                                sendAsyncMessage_Rest(commandsBean.getFaceId(),null,0,commandsBean.getVisitorType(),commandsBean.getBatchCode(),null,1,1,0,0);
                                Log.d("TanChuangThread", "图片下载失败");
                            }
                            break;
                        }
                        case 2://删除
                        {
                            try {
                                Subject subject = subjectBox.query().equal(Subject_.sid, commandsBean.getFaceId()).build().findUnique();
                                if (subject != null) {
                                    paAccessControl.deleteFace(subject.getTeZhengMa().getBytes());
                                    subjectBox.remove(subject);
                                    List<TimePeriod> timePeriodList=timePeriodBox.query().equal(TimePeriod_.sid,commandsBean.getFaceId()).build().find();
                                    for (TimePeriod timePeriod : timePeriodList) {
                                        timePeriodBox.remove(timePeriod);
                                    }
                                    sendAsyncMessage_Rest(commandsBean.getFaceId(),null,1,commandsBean.getVisitorType(),commandsBean.getBatchCode(),null,1,5,0,0);
                                }else {
                                    sendAsyncMessage_Rest(commandsBean.getFaceId(),null,0,commandsBean.getVisitorType(),commandsBean.getBatchCode(),null,1,5,0,0);

                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                sendAsyncMessage_Rest(commandsBean.getFaceId(),null,0,commandsBean.getVisitorType(),commandsBean.getBatchCode(),null,1,5,0,0);

                            }
                        }
                        break;
                        case 3://删除全部
                        {
                            try {
                                LazyList<Subject> subjectLazyList=subjectBox.query().build().findLazy();
                                for (Subject subject:subjectLazyList){
                                    try {
                                        paAccessControl.deleteFace(subject.getTeZhengMa().getBytes());
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        sendAsyncMessage_Rest(commandsBean.getFaceId(),null,0,commandsBean.getVisitorType(),commandsBean.getBatchCode(),null,1,6,0,0);
                                    }
                                }
                                subjectBox.removeAll();
                                timePeriodBox.removeAll();
                                sendAsyncMessage_Rest(commandsBean.getFaceId(),null,1,commandsBean.getVisitorType(),commandsBean.getBatchCode(),null,1,6,0,0);

                            }catch (Exception e){
                                e.printStackTrace();
                                sendAsyncMessage_Rest(commandsBean.getFaceId(),null,0,commandsBean.getVisitorType(),commandsBean.getBatchCode(),null,1,6,0,0);

                            }
                            break;
                        }
                        case 4:
                            Bitmap bitmap = null;
                            try {
                                bitmap = Glide.with(MyApplication.myApplication).asBitmap()
                                        .load(commandsBean.getVisitorImg())
                                        // .sizeMultiplier(0.9f)
                                        .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                        .get();
                            } catch (InterruptedException | ExecutionException e) {
                                Log.d("TanChuangThread", e.getMessage());
                            }
                            if (bitmap != null) {
                                BitmapUtil.saveBitmapToSD(bitmap, MyApplication.SDPATH3, commandsBean.getFaceId() + ".png");
                                File filef = new File(MyApplication.SDPATH3 + File.separator + commandsBean.getFaceId() + ".png");
                                Log.d("TanChuangThread", commandsBean.getFaceId() + "未压缩前:filef.length():" + filef.length());
                                File file = Luban.with(MianBanJiActivity3.this).load(MyApplication.SDPATH3 + File.separator + commandsBean.getFaceId() + ".png")
                                        .ignoreBy(600)
                                        .setTargetDir(MyApplication.SDPATH3 + File.separator)
                                        .get(MyApplication.SDPATH3 + File.separator + commandsBean.getFaceId() + ".png");
                                if (file == null) {
                                    Log.d("TanChuangThread", "图片压缩失败");
                                    sendAsyncMessage_Rest(commandsBean.getFaceId(),null,0,commandsBean.getVisitorType(),commandsBean.getBatchCode(),null,1,1,0,0);
                                    break;
                                }
                                Log.d("TanChuangThread", commandsBean.getFaceId() + "压缩后:file.length():" + file.length());
                                FacePassAddFaceResult detectResult = null;
                                detectResult = paAccessControl.addFace(BitmapFactory.decodeFile(file.getAbsolutePath()));
                                if (detectResult != null && detectResult.result==0) {
                                    //先查询有没有
                                    try {
                                        byte [] faceToken=detectResult.faceToken;
                                        Subject subject2 = subjectBox.query().equal(Subject_.sid,commandsBean.getFaceId()).build().findUnique();
                                        if (subject2 != null) {//覆盖
                                            //取出所有时间段
                                            paAccessControl.deleteFace(subject2.getTeZhengMa().getBytes());
                                            paAccessControl.bindGroup("facepasstestx", faceToken);
                                            subject2.setTeZhengMa(new String(faceToken));
                                            subjectBox.put(subject2);
                                            Log.d("MyReceiver", "修改图片成功" + subject2.toString());
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        sendAsyncMessage_Rest(commandsBean.getFaceId(),null,0,commandsBean.getVisitorType(),commandsBean.getBatchCode(),null,1,1,0,0);
                                    }
                                }else {
                                    sendAsyncMessage_Rest(commandsBean.getFaceId(),null,0,commandsBean.getVisitorType(),commandsBean.getBatchCode(),null,1,1,0,0);
                                }
                            }else {
                                sendAsyncMessage_Rest(commandsBean.getFaceId(),null,0,commandsBean.getVisitorType(),commandsBean.getBatchCode(),null,1,1,0,0);
                            }
                            break;
                        case 5:
                            Subject subject2 = subjectBox.query().equal(Subject_.sid,commandsBean.getFaceId()).build().findUnique();
                            if (subject2 != null) {//覆盖
                                subject2.setWorkNumber(commandsBean.getIcCard().toUpperCase());
                                subjectBox.put(subject2);
                                Log.d("MyReceiver", "修改ICCard成功" + subject2.toString());
                            }
                            break;
                        case 0:
                            sendAsyncMessage_Rest(null,null,0,0,null,null,1,0,0,0);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    SystemClock.sleep(1000);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DengUT.reboot();
                                        }
                                    });
                                }
                            }).start();
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
        if (mNfcAdapter!=null){
            try {
                mNfcAdapter.disableForegroundDispatch(MianBanJiActivity3.this);
            }catch (Exception e){
                e.printStackTrace();
                Log.d("MianBanJiActivity3", e.getMessage()+"异常是3");
            }
        }
        Log.d("MianBanJiActivity3", "暂停");

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

        //imageView=findViewById(R.id.vvvbb);
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

      //  manager2 = new CameraManager2();
      //  cameraView2 = findViewById(R.id.preview2);
      //  manager2.setPreviewDisplay(cameraView2);
        /* 注册相机回调函数 */
      //  manager2.setListener(this);

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

    @Override
    protected void onStop() {
        Log.d("MianBanJiActivity3", "停止");

      //  if (manager2 != null) {
        //    manager2.release();
      //  }

      //  mWatchDogCore.cancelBroadcastTask();//取消开门狗定时广播任务
      //  mWatchDogCore.cancelReceiveTask();//监听看门狗定时任务

        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d("MianBanJiActivity3", "重新开始2");
        super.onRestart();
        manager.open(getWindowManager(), SettingVar.cameraId, cameraWidth, cameraHeight);//前置是1
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("MianBanJiActivity3", "onStart");
    }


    public  void close() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (channel_xt != null && channel_xt.isOpen()) {
                    try {
                        channel_xt.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (channel_res != null && channel_res.isOpen()) {
                    try {
                        channel_res.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (channel_up != null && channel_up.isOpen()) {
                    try {
                        channel_up.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (channel_yw != null && channel_yw.isOpen()) {
                    try {
                        channel_yw.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (mConnection != null && mConnection.isOpen()) {
                    try {
                        mConnection.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        kaiPing();
        unbindService(connection);
        //unregisterReceiver(mNfcBR);
        unregisterReceiver(timeChangeReceiver);
        unregisterReceiver(netWorkStateReceiver);
        EventBus.getDefault().unregister(this);//解除订阅

        close();

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

        if (manager != null) {
            manager.release();
        }

        if (mReadThread != null) {
            mReadThread.interrupt();
        }

        if (linkedBlockingQueue != null) {
            linkedBlockingQueue.clear();
        }
        if (mFeedFrameQueue != null) {
            mFeedFrameQueue.clear();
        }
        if (mFeedFrameThread != null) {
            mFeedFrameThread.isIterrupt = true;
            mFeedFrameThread.interrupt();
        }

        if (tanChuangThread != null) {
            tanChuangThread.isRing = true;
            tanChuangThread.interrupt();
        }

        if (mRecognizeThread != null) {
            mRecognizeThread.isInterrupt = true;
            mRecognizeThread.interrupt();
        }


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
        //mWatchDogCore.unregisterWatchDogReceiver();

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

        if (event.equals("mFacePassHandler")) {
            paAccessControl = MyApplication.myApplication.getFacePassHandler();
            return;
        }
        if (event.equals("youwang")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(5000);
                    if (rabbitMQUtil==null){
                        rabbitMQUtil=new RabbitMQUtil(mqshow,MianBanJiActivity3.this);
                        rabbitMQUtil.init(baoCunBean.getHoutaiDiZhi(),5672,"guest","guest@ABCD");
                    }
                }
            }).start();

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

                    String xiaoshiss = DateUtils.timeMinute(System.currentTimeMillis() + "");
                    if (xiaoshiss.split(":")[0].equals("03") && xiaoshiss.split(":")[1].equals("40")) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    List<Subject> longList=new ArrayList<>();
                                    LazyList<Subject> subjectLazyList=subjectBox.query().build().findLazy();
                                    long time=System.currentTimeMillis();
                                    for (Subject subject : subjectLazyList) {
                                        List<TimePeriod> timePeriodList=timePeriodBox.query().equal(TimePeriod_.sid,subject.getSid()).build().find();
                                        boolean isGQ=false,isSC=true;
                                        for (TimePeriod timePeriod : timePeriodList) {
                                            long minTime= Long.parseLong(timePeriod.getTime().split("T")[0]);
                                            long maxTime= Long.parseLong(timePeriod.getTime().split("T")[1]);
                                            if (time>=minTime && time<=maxTime){//在范围内
                                                isGQ=true;
                                                break;
                                            }else {
                                                if (minTime>time){
                                                    isSC=false;
                                                }
                                            }
                                        }
                                        if (!isGQ){
                                            if (isSC){
                                                longList.add(subject);
                                            }
                                        }
                                    }
                                    for (Subject subject : longList) {
                                        removePepole(subject);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                    isnfc++;
                    if (isnfc>=10){
                        isnfc=0;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
//                                if (binder!=null){
//                                    try {
//                                        Log.d("TimeChangeReceiver", "binder.getNfcState():" + binder.getNfcState());
//
//                                    } catch (RemoteException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
                                disable();
                                SystemClock.sleep(3000);
                                enable();

                            }
                        }).start();
                    }

                    //1分钟一次指令获取
//                    try {
//                        if (baoCunBean.getHoutaiDiZhi() != null && !baoCunBean.getHoutaiDiZhi().equals("")) {
//                            if (isGET) {
//                                isGET = false;
//                                link_get_zhiling();
//                            }
//                        }
//                    } catch (Exception e) {
//                        isGET = true;
//                        Log.d("TimeChangeReceiver", e.getMessage());
//                    }

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            SystemClock.sleep(12000);
//                            link_infoSync();
//                            SystemClock.sleep(5000);
//                            final List<DaKaBean> huiFuBeanList = daKaBeanBox.getAll();
//                            if (huiFuBeanList.size() > 0) {
//                                generateXml(huiFuBeanList, JHM, baoCunBean.getName() + "", baoCunBean.getWeizhi() + "");
//                            }
//                        }
//                    }).start();

                    sendAsyncMessage_Rest(null,null,0,-1,null,null,1,100,0,0,AppUtils.queryStorage(),subjectBox.query().build().findLazy().size(),AppUtils.queryMemmoryTotal());

//{"result":[{"image":"http:192.168.2.121:8980/userfiles/fileupload/202005/1259733916046864386.jpg","instructions":"1","cardID":"E2B32712","jurisdiction":"0","name":"军总","id":"1259734117369262080","beginTime":"2020-05-11 14:36:33","endTime":"2021-05-11 14:36:32","personType":"0","subjectId":"1259734038776393728"}],"code":200,"machineCode":"4053132e72569d","desc":"成功"}
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


//AppUtils.queryStorage()
    public void sendAsyncMessage_Rest(String faceId,Bitmap bitmap,int result,int visitorType,String batchCode,String icCard,int machineStatus,int businessType,int faceCompareResult,int cardCompareResult) {//处理结果回调
        ResultFaceMessage faceMessage=new ResultFaceMessage();
        if (bitmap!=null){
            bitmap=BitmapUtil.rotateBitmap(bitmap,SettingVar.msrBitmapRotation);
            faceMessage.setCatchFaceImg(BitmapUtil.bitmapToBase64(bitmap));
        }
        faceMessage.setResult(result);//入库的结果
        faceMessage.setFaceId(faceId);
        faceMessage.setVisitorType(visitorType);
        faceMessage.setIp(FileUtil.getLocalHostIp());
        faceMessage.setMachineCode(JHM);
        faceMessage.setBatchCode(batchCode);
        faceMessage.setIcCard(icCard);
        faceMessage.setFaceCompareResult(faceCompareResult);
        faceMessage.setCardCompareResult(cardCompareResult);
        faceMessage.setMachineStatus(machineStatus);
        faceMessage.setBusinessType(businessType);
        faceMessage.setPushDate(DateUtils.time22(System.currentTimeMillis()+""));
        String ms= JSONObject.toJSONString(faceMessage);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (channel_res!=null){
                        channel_res.confirmSelect();
                        channel_res.basicPublish(EXCHANG_NAME_RES, KEY_NAME_RES, null, ms.getBytes(StandardCharsets.UTF_8));
                        boolean t=  channel_res.waitForConfirms();
                        Log.d("MianBanJiActivity3", "发送MQ消息结果:" + t+" 发送参数:"+ms);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendAsyncMessage_Rest(String faceId,Bitmap bitmap,int result,int visitorType,String batchCode,String icCard,int machineStatus,int businessType,int faceCompareResult,int cardCompareResult,String remainingMemory,int allPeopleSize,String memoryTotal) {//处理结果回调
        ResultFaceMessage faceMessage=new ResultFaceMessage();
        if (bitmap!=null){
            bitmap=BitmapUtil.rotateBitmap(bitmap,SettingVar.msrBitmapRotation);
            faceMessage.setCatchFaceImg(BitmapUtil.bitmapToBase64(bitmap));
        }
        faceMessage.setResult(result);//入库的结果
        faceMessage.setFaceId(faceId);
        faceMessage.setVisitorType(visitorType);
        faceMessage.setIp(FileUtil.getLocalHostIp());
        faceMessage.setMachineCode(JHM);
        faceMessage.setBatchCode(batchCode);
        faceMessage.setIcCard(icCard);
        faceMessage.setFaceCompareResult(faceCompareResult);
        faceMessage.setCardCompareResult(cardCompareResult);
        faceMessage.setMachineStatus(machineStatus);
        faceMessage.setBusinessType(businessType);
        faceMessage.setMemoryTotal(memoryTotal);
        faceMessage.setPushDate(DateUtils.time22(System.currentTimeMillis()+""));
        faceMessage.setRemainingMemory(remainingMemory);
        faceMessage.setAllPeopleSize(allPeopleSize);
        String ms= JSONObject.toJSONString(faceMessage);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (channel_res!=null){
                        channel_res.confirmSelect();
                        channel_res.basicPublish(EXCHANG_NAME_RES, KEY_NAME_RES, null, ms.getBytes(StandardCharsets.UTF_8));
                        boolean t=  channel_res.waitForConfirms();
                        Log.d("MianBanJiActivity3", "发送MQ消息结果:" + t+" 发送参数:"+ms);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


//    //查询是否开门
//    private void link_chick_IC2(final String icid, final String name) {
//        // final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//        RequestBody body = null;
//        body = new FormBody.Builder()
//                .add("companyId", "DG001")
//                .add("ic_card", icid + "")
//                .add("machine_code", FileUtil.getSerialNumber() == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber())
//                .build();
//
//        Request.Builder requestBuilder = new Request.Builder()
//                .header("Content-Type", "application/json")
//                .post(body)
//                .url(baoCunBean.getHoutaiDiZhi() + "/front/wisdom/app/openDoorToCard");
//        // step 3：创建 Call 对象
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .writeTimeout(5000, TimeUnit.MILLISECONDS)
//                .connectTimeout(5000, TimeUnit.MILLISECONDS)
//                .readTimeout(5000, TimeUnit.MILLISECONDS)
////				    .cookieJar(new CookiesManager())
//                //        .retryOnConnectionFailure(true)
//                .build();
//        Call call = okHttpClient.newCall(requestBuilder.build());
//        //step 4: 开始异步请求
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("AllConnects", "请求失败" + e.getMessage());
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
////                        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "请求超时", TastyToast.LENGTH_LONG, TastyToast.INFO);
////                        tastyToast.setGravity(Gravity.CENTER, 0, 0);
////                        tastyToast.show();
//                        stopMedie();
//                        soundPool.play(musicId.get(7), 1, 1, 0, 0, 1);
//                    }
//                });
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.d("AllConnects", "请求成功" + call.request().toString());
//                //获得返回体
//                try {
//                    ResponseBody body = response.body();
//                    final String ss = body.string().trim();
//
//                    if (ss.equals("true")) {
//                        // TPS980PosUtil.setJiaJiPower(1);
//                        DengUT.openDool();
//                        //启动定时器或重置定时器
//                        if (task != null) {
//                            task.cancel();
//                            //timer.cancel();
//                            task = new TimerTask() {
//                                @Override
//                                public void run() {
//
//                                    Message message = new Message();
//                                    message.what = 222;
//                                    if (mHandler != null)
//                                        mHandler.sendMessage(message);
//
//                                }
//                            };
//                            if (timer != null)
//                                timer.schedule(task, 5000);
//                        } else {
//                            task = new TimerTask() {
//                                @Override
//                                public void run() {
//                                    Message message = new Message();
//                                    message.what = 222;
//                                    if (mHandler != null)
//                                        mHandler.sendMessage(message);
//                                }
//                            };
//                            if (timer != null)
//                                timer.schedule(task, 5000);
//                        }
//                        // Log.d("MianBanJiActivity2", "开门");
//                        // link_chick_jilu(icid, name);
//                        subjectOnly = null;
//                        faceView.changeTexts();
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                stopMedie();
//                                soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);
//                            }
//                        });
//                    } else {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "没有进入权限", TastyToast.LENGTH_LONG, TastyToast.INFO);
//                                tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                                tastyToast.show();
//                                //soundPool.stop();
//                                stopMedie();
//                                soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
//                            }
//                        });
//
//                    }
//                    Log.d("AllConnects", "查询开门:" + ss);
//
//                } catch (Exception e) {
//                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            stopMedie();
//                            soundPool.play(musicId.get(7), 1, 1, 0, 0, 1);
//                        }
//                    });
//                }
//            }
//        });
//    }

//    //查询是否开门
//    private void link_chick_IC(final String icid, final String name) {
//        // final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//        RequestBody body = null;
//        body = new FormBody.Builder()
//                .add("companyId", "DG001")
//                .add("ic_card", icid + "")
//                .add("machine_code", FileUtil.getSerialNumber() == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber())
//                .build();
//
//        Request.Builder requestBuilder = new Request.Builder()
//                .header("Content-Type", "application/json")
//                .post(body)
//                .url(baoCunBean.getHoutaiDiZhi() + "/front/wisdom/app/findIcToAccessRecord");
//        // step 3：创建 Call 对象
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .writeTimeout(5000, TimeUnit.MILLISECONDS)
//                .connectTimeout(5000, TimeUnit.MILLISECONDS)
//                .readTimeout(5000, TimeUnit.MILLISECONDS)
////				    .cookieJar(new CookiesManager())
//                //        .retryOnConnectionFailure(true)
//                .build();
//        Call call = okHttpClient.newCall(requestBuilder.build());
//        //step 4: 开始异步请求
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                isOpenFace = false;
//                Log.d("AllConnects", "请求失败" + e.getMessage());
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
////                        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "请求超时", TastyToast.LENGTH_LONG, TastyToast.INFO);
////                        tastyToast.setGravity(Gravity.CENTER, 0, 0);
////                        tastyToast.show();
//                        stopMedie();
//                        soundPool.play(musicId.get(7), 1, 1, 0, 0, 1);
//                    }
//                });
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.d("AllConnects", "请求成功" + call.request().toString());
//                //获得返回体
//                try {
//                    ResponseBody body = response.body();
//                    final String ss = body.string().trim();
//                    isOpenFace = false;
//                    if (ss.equals("true")) {
//                        // TPS980PosUtil.setJiaJiPower(1);
//                        DengUT.openDool();
//                        //启动定时器或重置定时器
//                        if (task != null) {
//                            task.cancel();
//                            //timer.cancel();
//                            task = new TimerTask() {
//                                @Override
//                                public void run() {
//
//                                    Message message = new Message();
//                                    message.what = 222;
//                                    mHandler.sendMessage(message);
//
//                                }
//                            };
//                            timer.schedule(task, 5000);
//                        } else {
//                            task = new TimerTask() {
//                                @Override
//                                public void run() {
//                                    Message message = new Message();
//                                    message.what = 222;
//                                    mHandler.sendMessage(message);
//                                }
//                            };
//                            timer.schedule(task, 5000);
//                        }
//                        // Log.d("MianBanJiActivity2", "开门");
//                        // link_chick_jilu(icid, name);
//                        subjectOnly = null;
//                        faceView.changeTexts();
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                stopMedie();
//                                soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);
//                            }
//                        });
//                    } else {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "识别成功，但是没有进入权限", TastyToast.LENGTH_LONG, TastyToast.INFO);
//                                tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                                tastyToast.show();
//                                //soundPool.stop();
//                                stopMedie();
//                                soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
//                            }
//                        });
//
//                    }
//                    Log.d("AllConnects", "查询开门:" + ss);
//
//                } catch (Exception e) {
//                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            stopMedie();
//                            soundPool.play(musicId.get(7), 1, 1, 0, 0, 1);
//                        }
//                    });
//                }
//            }
//        });
//    }

//    public void generateXml(final List<DaKaBean> records, final String serialnumber, String machineName, String machineAddress) {
//        File file = new File(MyApplication.SDPATH, "record.xml");
//        if (file.exists()) {
//            file.delete();
//            file = new File(MyApplication.SDPATH, "record.xml");
//        }
//        try {
//            FileOutputStream fos = new FileOutputStream(file);
//            XmlSerializer xs = Xml.newSerializer();
//            xs.setOutput(fos, "utf-8");
//            xs.startDocument("utf-8", true);
//            xs.startTag(null, "Root");
//            xs.attribute(null, "serialnumber", serialnumber);
//
//            xs.startTag(null, "List");
//
//
//            for (DaKaBean record : records) {
//
//                xs.startTag(null, "history");
//
//                xs.startTag(null, "machineName");
//                xs.text(machineName);
//                xs.endTag(null, "machineName");
//
//                xs.startTag(null, "machineAddress");
//                xs.text(machineAddress);
//                xs.endTag(null, "machineAddress");
//
//                xs.startTag(null, "personName");
//                String department = record.getName();
//                if (department == null) {
//                    department = "";
//                }
//                xs.text(department);
//                xs.endTag(null, "personName");
//
//                xs.startTag(null, "iamge");
//                xs.text(record.getB64() + "");
//                //  xs.text("kuhyuytyg");
//                xs.endTag(null, "iamge");
//
//                xs.startTag(null, "pepopleType");
//                String peopleType = record.getRenyuanleixing();
//                if (peopleType == null) {
//                    peopleType = "";
//                }
//                xs.text(peopleType);
//                xs.endTag(null, "pepopleType");
//
//                xs.startTag(null, "companyId");
//                xs.text(record.getDianhua() + "");
//                xs.endTag(null, "companyId");
//
//                xs.startTag(null, "icCardNo");
//                xs.text(record.getBumen() + "");
//                xs.endTag(null, "icCardNo");
//
//                xs.startTag(null, "machineCode");
//                xs.text(serialnumber);
//                xs.endTag(null, "machineCode");
//
//                xs.startTag(null, "recognitionTime");
//                String cardId = record.getTime();
//                if (cardId == null) {
//                    cardId = "";
//                }
//                xs.text(cardId);
//                xs.endTag(null, "recognitionTime");
//
//                xs.endTag(null, "history");
//
//            }
//
//            xs.endTag(null, "List");
//
//            xs.endTag(null, "Root");
//            //生成xml头
//            xs.endDocument();
//
//            xs.flush();
//            fos.flush();
//            fos.close();
//
//            link_chick_jilu(records);
//
//        } catch (Exception e) {
//            Log.d("FileUtil", e.getMessage() + "");
//        } finally {
//            Log.d("FileUtil", "执行完成");
//        }
//    }

//    //上传识别记录
//    private void link_chick_jilu(List<DaKaBean> daKaBeanList) {
//
////        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
////        JSONArray array = new JSONArray();
////        final List<DaKaBean> huiFuBeanList = daKaBeanBox.getAll();
////        if (huiFuBeanList.size()==0)
////            return;
//        //   FileUtil.generateXml(huiFuBeanList,JHM,baoCunBean.getName() + "",baoCunBean.getWeizhi() + "");
//
////        for (DaKaBean bean : huiFuBeanList) {
////            try {
////                JSONObject object = new JSONObject();
////                object.put("machineName", baoCunBean.getName() + "");
////                object.put("machineAddress", baoCunBean.getWeizhi() + "");
////                object.put("personName", bean.getName() + "");
////                object.put("companyId", "DG001");
////                object.put("icCardNo", bean.getBumen() + "");
////                object.put("machineCode", JHM);
////                object.put("recognitionTime", bean.getTime());
////                array.put(object);
////            } catch (JSONException e) {
////                e.printStackTrace();
////            }
////
////        }
////        Log.d("MianBanJiActivity3", "上传记录：" + array.toString());
//        RequestBody fileBody = RequestBody.create(new File(MyApplication.SDPATH + File.separator + "record.xml"), MediaType.parse("application/octet-stream"));
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("file", System.currentTimeMillis() + ".xml", fileBody)
//                .build();
//        Request.Builder requestBuilder = new Request.Builder()
//                .header("Content-Type", "application/json")
//                .post(requestBody)
//                .url(baoCunBean.getHoutaiDiZhi() + "/front/wisdom/app/save_access_records2");
//        // step 3：创建 Call 对象
//        Call call = okHttpClient.newCall(requestBuilder.build());
//        //step 4: 开始异步请求
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("AllConnects", "请求失败" + e.getMessage());
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        stopMedie();
//                        soundPool.play(musicId.get(8), 1, 1, 0, 0, 1);
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.d("AllConnects", "请求成功" + call.request().toString());
//                //获得返回体
//                try {
//                    ResponseBody body = response.body();
//                    String ss = body.string().trim();
//                    Log.d("AllConnects", "上传开门记录:" + ss);
//                    JsonObject jsonObject = GsonUtil.parse(ss).getAsJsonObject();
//
//                    if (jsonObject.get("code").getAsInt() == 0) {
//                        for (DaKaBean d : daKaBeanList) {
//                            daKaBeanBox.remove(d);
//                        }
//                    }
//
//                } catch (Exception e) {
//                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
//                }
//            }
//        });
//    }

//    //信鸽信息处理
//    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
//    public void onDataSynEvent(XGBean xgBean) {
//        if (paAccessControl != null) {
//            try {
//                tsxxChuLi.setData(xgBean, MianBanJiActivity3.this, paAccessControl);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//    }


    private void bofang() {
        MediaPlayer mMediaPlayer = MediaPlayer.create(MianBanJiActivity3.this, R.raw.deng2);
        mMediaPlayer.start();
    }


    private void kaiPing() {
        try {
            Intent intent = new Intent();
            intent.setAction("LYD_SHOW_NAVIGATION_BAR");
            intent.putExtra("type", 1);
            this.sendBroadcast(intent);
            sendBroadcast(new Intent("com.android.internal.policy.impl.showNavigationBar"));
            sendBroadcast(new Intent("com.android.systemui.statusbar.phone.statusopen"));
        }catch (Exception e){
            e.printStackTrace();
        }

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



//    private void init_NFC() {
//        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
//        if (mNfcAdapter == null) {
//            Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "设备不支持NFC", TastyToast.LENGTH_LONG, TastyToast.INFO);
//            tastyToast.setGravity(Gravity.CENTER, 0, 0);
//            tastyToast.show();
//        } else if (!mNfcAdapter.isEnabled()) {
//            Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "NFC未开启,10秒后重启APP", TastyToast.LENGTH_LONG, TastyToast.INFO);
//            tastyToast.setGravity(Gravity.CENTER, 0, 0);
//            tastyToast.show();
//            finish();
//            RestartAPPTool.restartAPP(MianBanJiActivity3.this);
////            new Thread(new Runnable() {
////                @Override
////                public void run() {
////                    SystemClock.sleep(10000);
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////
////                        }
////                    });
////                }
////            }).start();
//
//        }
//     //   mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
//      //  Log.d("MianBanJiActivity3", "mNfcAdapter:" + mNfcAdapter.toString());
////        if (mNfcAdapter!=null){
////            Log.d("MianBanJiActivity3", "mNfcAdapter.isEnabled():" + mNfcAdapter.isEnabled());
////        }
//     //   mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(MianBanJiActivity3.this, getClass()), 0);
//       // IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
//      //  tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
//
//    }


    public void initNFC2(){
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter[] intentFilters = new IntentFilter[]{ new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED) };
//        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        if (mNfcAdapter!=null)
        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, mTechList);
    }


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = INfcPacemaker.Stub.asInterface(service);
            Log.i("ffffffffff", "NFC 服务连接 connected.");


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mqshow.setVisibility(View.VISIBLE);
                    mqshow.setText("NFC连接断开");
                }
            });
            Log.e("fffffffff", "NFC 服务断开连接 disconnected.");
        }
    };

    public void processIntent(Intent intent) {
      //  Log.d("MianBanJiActivity3", "NFC的intent"+intent.toString());
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
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
                                timer.schedule(task, 3200);
                            } else {
                                task = new TimerTask() {
                                    @Override
                                    public void run() {
                                        Message message = new Message();
                                        message.what = 222;
                                        mHandler.sendMessage(message);
                                    }
                                };
                                timer.schedule(task, 3200);
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
                            //上传识别记录
                            sendAsyncMessage_Rest(subject.getSid(),msrBitmap,1,subject.getDaka(),null,finalSdfds,1,2,1,1);
                        }
//                    link_chick_IC(sdfds, name);
                    } else {
                        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "人脸信息与卡号不匹配!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                        tastyToast.setGravity(Gravity.CENTER, 0, 0);
                        tastyToast.show();
                        stopMedie();
                        soundPool.play(musicId.get(3), 1, 1, 0, 0, 1);
                        sendAsyncMessage_Rest(subject.getSid(),msrBitmap,1,subject.getDaka(),null,finalSdfds,1,2,1,0);
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


    public static class NetWorkStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取所有网络连接的信息
            Network[] networks = connMgr.getAllNetworks();
            //用于存放网络连接信息
            StringBuilder sb = new StringBuilder();
            //通过循环将网络信息逐个取出来
            Log.d("MianBanJiActivity3", "networks.length:" + networks.length);
            if (networks.length == 0) {
                //没网
                Log.d("MianBanJiActivity3", "没网2");
            }
            for (Network network : networks) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
                if (networkInfo.isConnected()) {
                    //连接上
                    Log.d("MianBanJiActivity3", "有网2");
                    EventBus.getDefault().post("youwang");

                    break;
                }
            }

        }
    }


//    //获取指令
//    private void link_get_zhiling() {
//        if (baoCunBean.getHoutaiDiZhi() == null || baoCunBean.getHoutaiDiZhi().equals("")) {
//            isGET = true;
//            return;
//        }
//        RequestBody body = null;
//        body = new FormBody.Builder()
//                .add("company_id", "DG001")
//                .add("machine_code", JHM)
//                .build();
//
//        Request.Builder requestBuilder = new Request.Builder()
//                .header("Content-Type", "application/json")
//                .post(body)
//                .url(baoCunBean.getHoutaiDiZhi() + "/front/wisdom/app/getInstructions");
//        // step 3：创建 Call 对象
//        Call call = okHttpClient.newCall(requestBuilder.build());
//        //step 4: 开始异步请求
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                //  Log.d("MianBanJiActivity3", "请求失败"+baoCunBean.getHoutaiDiZhi());
//                Log.d("AllConnects", "请求失败" + e.getMessage());
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "请求指令失败，请检查网络", TastyToast.LENGTH_LONG, TastyToast.INFO);
//                        tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                        tastyToast.show();
//                    }
//                });
//                // if (linkedBlockingQueue.size()==0){
//                isGET = true;
//                // }
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.d("AllConnects", "请求成功" + call.request().toString());
//                //获得返回体
//                try {
//                    ResponseBody body = response.body();
//                    String ss = body.string().trim();
//                    Log.d("AllConnects", "获取指令:" + ss);
//                    JsonObject jsonObject = GsonUtil.parse(ss).getAsJsonObject();
//                    Gson gson = new Gson();
//
//                    ZhiLingBean commandsBean = gson.fromJson(jsonObject, ZhiLingBean.class);
//                    if (commandsBean != null && commandsBean.getCode() == 200) {
//                        //desc : “成功”
//                        for (ZhiLingBean.ResultBean resultBean : commandsBean.getResult()) {
//                            resultBean.setMachineCode(commandsBean.getMachineCode());
//                          //  linkedBlockingQueue.put(resultBean);
//                        }
////                        if (linkedBlockingQueue.size()==0){
////
////                        }
//                        isGET = true;
//                        Log.d("MianBanJiActivity3", "获取指令linkedBlockingQueue.size():" + linkedBlockingQueue.size());
//                    } else {
//                        isGET = true;
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "请求指令失败Code错误", TastyToast.LENGTH_LONG, TastyToast.INFO);
//                                tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                                tastyToast.show();
//                            }
//                        });
//                    }
//
//                } catch (final Exception e) {
//                    // if (linkedBlockingQueue.size()==0){
//                    isGET = true;
//                    // }
//                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "请求指令失败" + e.getMessage(), TastyToast.LENGTH_LONG, TastyToast.INFO);
//                            tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                            tastyToast.show();
//                        }
//                    });
//
//                }
//            }
//        });
//    }

//    //数据同步
//    private void link_infoSync() {
//        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//
//        final List<HuiFuBean> huiFuBeanList = huiFuBeanBox.getAll();
//
//        JSONArray array = new JSONArray();
//        if (huiFuBeanList.size() != 0) {
//            for (HuiFuBean bean : huiFuBeanList) {
//                try {
//                    JSONObject object = new JSONObject();
//                    object.put("subjectId", bean.getPepopleId());
//                    object.put("personType", bean.getPepopleType());
//                    object.put("type", bean.getType());
//                    object.put("msg", bean.getMsg());
//                    object.put("id", bean.getShortId());
//                    object.put("serialnumber", JHM);
//                    array.put(object);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        } else {
//            return;
//        }
//        Log.d("MianBanJiActivity3", "数据同步：" + array.toString());
//        RequestBody body = RequestBody.create(array.toString(), JSON);
//
//        Request.Builder requestBuilder = new Request.Builder()
//                .header("Content-Type", "application/json")
//                .post(body)
//                .url(baoCunBean.getHoutaiDiZhi() + "/front/wisdom/app/delInstructions");
//        // step 3：创建 Call 对象
//        Call call = okHttpClient.newCall(requestBuilder.build());
//        //step 4: 开始异步请求
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("AllConnects", "数据同步请求失败" + e.getMessage());
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "数据同步请求失败,请检查网络", TastyToast.LENGTH_LONG, TastyToast.INFO);
//                        tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                        tastyToast.show();
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.d("AllConnects", "请求成功" + call.request().toString());
//                //获得返回体
//                try {
//                    ResponseBody body = response.body();
//                    String ss = body.string().trim();
//                    Log.d("AllConnects", "数据同步:" + ss);
//                    if (ss.equals("true")) {
//                        for (HuiFuBean bean : huiFuBeanList) {
//                            huiFuBeanBox.remove(bean);
//                        }
//                    } else {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "数据同步flase", TastyToast.LENGTH_LONG, TastyToast.INFO);
//                                tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                                tastyToast.show();
//                            }
//                        });
//                    }
//                } catch (final Exception e) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "数据同步" + e.getMessage(), TastyToast.LENGTH_LONG, TastyToast.INFO);
//                            tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                            tastyToast.show();
//                        }
//                    });
//                    Log.d("WebsocketPushMsg", e.getMessage() + "数据同步");
//                }
//            }
//        });
//    }

}
