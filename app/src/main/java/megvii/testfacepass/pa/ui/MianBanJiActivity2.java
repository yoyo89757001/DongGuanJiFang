//package megvii.testfacepass.pa.ui;
//
//import android.animation.Animator;
//import android.animation.ValueAnimator;
//import android.app.Activity;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.CursorLoader;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.content.res.Configuration;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.graphics.drawable.BitmapDrawable;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.os.SystemClock;
//import android.provider.MediaStore;
//import android.text.TextUtils;
//import android.util.DisplayMetrics;
//import android.util.Log;
//
//import android.view.Gravity;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//
//import android.view.animation.DecelerateInterpolator;
//import android.view.animation.Interpolator;
//import android.widget.ImageView;
//
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.ScrollView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//
//import com.badoo.mobile.util.WeakHandler;
//import com.bumptech.glide.Glide;
//
//import com.bumptech.glide.request.RequestOptions;
//
//import com.common.pos.api.util.TPS980PosUtil;
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//import com.jude.rollviewpager.adapter.StaticPagerAdapter;
//
//import com.pingan.ai.access.common.PaAccessControlMessage;
//import com.pingan.ai.access.common.PaAccessDetectConfig;
//import com.pingan.ai.access.entiry.YuvInfo;
//import com.pingan.ai.access.impl.OnPaAccessDetectListener;
//import com.pingan.ai.access.manager.PaAccessControl;
//import com.pingan.ai.access.result.PaAccessCompareFacesResult;
//import com.pingan.ai.access.result.PaAccessDetectFaceResult;
//
//
//import com.pingan.ai.access.result.PaAccessMultiFaceBaseInfo;
//import com.sdsmdg.tastytoast.TastyToast;
//
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.TimeUnit;
//
//import butterknife.ButterKnife;
//import io.objectbox.Box;
//import me.grantland.widget.AutofitTextView;
//
//import megvii.testfacepass.pa.MyApplication;
//import megvii.testfacepass.pa.R;
//import megvii.testfacepass.pa.beans.BaoCunBean;
//import megvii.testfacepass.pa.beans.BenDiJiLuBean;
//import megvii.testfacepass.pa.beans.DaKaBean;
//import megvii.testfacepass.pa.beans.GuanHuai;
//import megvii.testfacepass.pa.beans.GuanHuai_;
//import megvii.testfacepass.pa.beans.Subject;
//import megvii.testfacepass.pa.beans.Subject_;
//import megvii.testfacepass.pa.beans.TQBean;
//import megvii.testfacepass.pa.beans.TodayBean;
//import megvii.testfacepass.pa.beans.XGBean;
//import megvii.testfacepass.pa.beans.XinXiAll;
//import megvii.testfacepass.pa.beans.XinXiIdBean;
//import megvii.testfacepass.pa.beans.XinXiIdBean_;
//import megvii.testfacepass.pa.camera.CameraManager;
//import megvii.testfacepass.pa.camera.CameraManager2;
//import megvii.testfacepass.pa.camera.CameraPreview;
//import megvii.testfacepass.pa.camera.CameraPreview2;
//import megvii.testfacepass.pa.camera.CameraPreviewData;
//import megvii.testfacepass.pa.camera.CameraPreviewData2;
//import megvii.testfacepass.pa.cookies.CookiesManager;
//import megvii.testfacepass.pa.dialog.MiMaDialog3;
//import megvii.testfacepass.pa.dialog.MiMaDialog4;
//import megvii.testfacepass.pa.tts.control.InitConfig;
//import megvii.testfacepass.pa.tts.control.MySyntherizer;
//import megvii.testfacepass.pa.tts.control.NonBlockSyntherizer;
//import megvii.testfacepass.pa.tts.listener.UiMessageListener;
//import megvii.testfacepass.pa.tts.util.OfflineResource;
//import megvii.testfacepass.pa.tuisong_jg.TSXXChuLi;
//import megvii.testfacepass.pa.utils.BitmapUtil;
//import megvii.testfacepass.pa.utils.DateUtils;
//import megvii.testfacepass.pa.utils.FileUtil;
//
//
//import megvii.testfacepass.pa.utils.GsonUtil;
//import megvii.testfacepass.pa.utils.NV21ToBitmap;
//import megvii.testfacepass.pa.utils.SettingVar;
//
//import megvii.testfacepass.pa.view.ClockView;
//import megvii.testfacepass.pa.view.FaceView;
//import megvii.testfacepass.pa.view.GlideCircleTransform;
//import megvii.testfacepass.pa.view.GlideCircleTransform270;
//
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.FormBody;
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//import okhttp3.ResponseBody;
//
//
//public class MianBanJiActivity2 extends Activity implements CameraManager.CameraListener,CameraManager2.CameraListener2 {
//
//   // 显示导航栏：com.android.internal.policy.impl.showNavigationBar
//   // 隐藏导航栏：com.android.internal.policy.impl.hideNavigationBar
//
//   // 显示通知栏：com.android.systemui.statusbar.phone.statusopen
//   // 隐藏通知栏：com.android.systemui.statusbar.phone.statusclose
//
//    private TextView tishi;
//    private Box<BenDiJiLuBean> benDiJiLuBeanBox = null;
//    private  ConcurrentHashMap map =new ConcurrentHashMap();
//    private Box<Subject> subjectBox = null;
//    protected Handler mainHandler;
//    private RelativeLayout top_rl;
//    private String appId = "11644783";
//    private String appKey = "knGksRFLoFZ2fsjZaMC8OoC7";
//    private String secretKey = "IXn1yrFezEo55LMkzHBGuTs1zOkXr9P4";
//    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
//    private TtsMode ttsMode = TtsMode.MIX;
//    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
//    // assets目录下bd_etts_speech_female.data为离线男声模型；bd_etts_speech_female.data为离线女声模型
//    private String offlineVoice = OfflineResource.VOICE_FEMALE;
//    // 主控制类，所有合成控制方法从这个类开始
//    private MySyntherizer synthesizer;
//    private long lingshiId=-1;
//  //  private static boolean isOne = true;
//  //  private static Vector<Subject> vipList = new Vector<>();//vip的弹窗
// //   private static Vector<Subject> dibuList = new Vector<>();//下面的弹窗
// //   private static Vector<Subject> shuList = new Vector<>();//下面的弹窗
//    private Bitmap msrBitmap=null;
//    private RequestOptions myOptions = new RequestOptions()
//            .fitCenter()
//            .error(R.drawable.erroy_bg)
//            .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
//    // .transform(new GlideRoundTransform(MainActivity.this,10));
//
//    private RequestOptions myOptions2 = new RequestOptions()
//            .fitCenter()
//            .error(R.drawable.erroy_bg)
//            //   .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
//            .transform(new GlideCircleTransform270(MyApplication.myApplication, 2, Color.parseColor("#ffffffff"),270));
//
//    private Box<TodayBean> todayBeanBox = null;
//
//   private OkHttpClient okHttpClient = new OkHttpClient.Builder()
//            .writeTimeout(10000, TimeUnit.MILLISECONDS)
//            .connectTimeout(10000, TimeUnit.MILLISECONDS)
//            .readTimeout(10000, TimeUnit.MILLISECONDS)
////				    .cookieJar(new CookiesManager())
//            //        .retryOnConnectionFailure(true)
//            .build();
//            .build();
//    private final Timer timer = new Timer();
//    private TimerTask task;
//    //  private DBG_View dbg_view;
// //   private static FacePassSDKMode SDK_MODE = FacePassSDKMode.MODE_OFFLINE;
//    private static final String DEBUG_TAG = "FacePassDemo";
//    private ClockView clockView;
//    private LinkedBlockingQueue<Subject> linkedBlockingQueue;
//    /* 人脸识别Group */
//   // private static final String group_name = "facepasstestx";
//    private AutofitTextView riqi,tianqi,wendu,xingqi;
//    private ImageView logo;
//    private MediaPlayer mMediaPlayer;
//  //  private WindowManager wm;
//    /* SDK 实例对象 */
//     private TodayBean todayBean = null;
//    /* 相机实例 */
//    private CameraManager manager;
//    private CameraManager2 manager2;
//    /* 显示人脸位置角度信息 */
//   // private XiuGaiGaoKuanDialog dialog = null;
//    /* 相机预览界面 */
//    private CameraPreview cameraView;
//    private CameraPreview2 cameraView2;
//    private boolean isAnXia = true;
//    /* 在预览界面圈出人脸 */
//     private FaceView faceView;
//    /* 相机是否使用前置摄像头 */
//    private static boolean cameraFacingFront = true;
//    private TextView shijian,gongsi;
//   // private int cameraRotation;
//    private static final int cameraWidth = 1280;
//    private static final int cameraHeight = 720;
//    // private int mSecretNumber = 0;
//    // private static final long CLICK_INTERVAL = 500;
//    //  private long mLastClickTime;
//  //  private IjkVideoView shipingView;
//    private boolean isOP=true;
//    private int heightPixels;
//    private int widthPixels;
//    int screenState = 0;// 0 横 1 竖
//
//    TanChuangThread tanChuangThread;
//    private ConcurrentHashMap<Long, Integer> concurrentHashMap = new ConcurrentHashMap<Long, Integer>();
//    private Box<GuanHuai> guanHuaiBox = null;
//    private Box<XinXiAll> xinXiAllBox = null;
//    private Box<XinXiIdBean> xinXiIdBeanBox = null;
//    private int dw, dh;
//    //  private LayoutInflater mInflater = null;
//    /*图片缓存*/
//    //  private FaceImageCache mImageCache;
//    // private Handler mAndroidHandler;
//    private Box<BaoCunBean> baoCunBeanDao = null;
//    private Box<DaKaBean> daKaBeanBox = null;
//   // private Box<TodayBean> todayBeanBox = null;
//    private BaoCunBean baoCunBean = null;
//   // private TodayBean todayBean = null;
//    private IntentFilter intentFilter;
//    private TimeChangeReceiver timeChangeReceiver;
//    private WeakHandler mHandler;
//  //  private ClockView clockView;
//   // private DiBuAdapter diBuAdapter = null;
//   // private GridLayoutManager gridLayoutManager = new GridLayoutManager(MianBanJiActivity.this, 2, LinearLayoutManager.HORIZONTAL, false);
//    private TSXXChuLi tsxxChuLi=null;
//    private static boolean isSC=true;
//    private RelativeLayout linearLayout;
//    private static boolean isDH=false;
//    private static boolean isShow=true;
//    private ImageView shezhi;
//    private PaAccessControl paAccessControl;
//    private Float mCompareThres;
//    private static String faceId="";
//    private  long feature2 =-1;
//    private ImageView yinying,tianqi_im;
//    private NV21ToBitmap nv21ToBitmap;
//
//    private int pp = 0;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        todayBeanBox=MyApplication.myApplication.getTodayBeanBox();
//        todayBean = todayBeanBox.get(123456L);
//        guanHuaiBox = MyApplication.myApplication.getGuanHuaiBox();
//        xinXiAllBox = MyApplication.myApplication.getXinXiAllBox();
//        xinXiIdBeanBox = MyApplication.myApplication.getXinXiIdBeanBox();
//        benDiJiLuBeanBox = MyApplication.myApplication.getBenDiJiLuBeanBox();
//        baoCunBeanDao = MyApplication.myApplication.getBaoCunBeanBox();
//        mainHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//            }
//
//        };
//        baoCunBean = baoCunBeanDao.get(123456L);
//        subjectBox = MyApplication.myApplication.getSubjectBox();
//        daKaBeanBox=MyApplication.myApplication.getDaKaBeanBox();
//        mCompareThres= baoCunBean.getShibieFaZhi();
//        //每分钟的广播
//        intentFilter = new IntentFilter();
//        intentFilter.addAction(Intent.ACTION_TIME_TICK);//每分钟变化
//        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);//设置了系统时区
//        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);//设置了系统时间
//        timeChangeReceiver = new TimeChangeReceiver();
//        registerReceiver(timeChangeReceiver, intentFilter);
//        linkedBlockingQueue = new LinkedBlockingQueue<>(1);
//
//        EventBus.getDefault().register(this);//订阅
//
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        dw = dm.widthPixels;
//        dh = dm.heightPixels;
//        tsxxChuLi=new TSXXChuLi();
//
//        nv21ToBitmap=new NV21ToBitmap(MianBanJiActivity2.this);
//        /* 初始化界面 */
//        Log.d("MianBanJiActivity2", "jh:" + baoCunBean);
//        initView();
//        link_P1();
//
//
//        if (baoCunBean != null){
//
//            try {
//                //PaAccessControl.getInstance().getPaAccessDetectConfig();
//                initFaceConfig();
//                paAccessControl = PaAccessControl.getInstance();
//                paAccessControl.setOnPaAccessDetectListener(onDetectListener);
//                Log.d("MianBanJiActivity2", "paAccessControl:" + paAccessControl);
//            }catch (Exception e){
//                Log.d("MianBanJiActivity2", e.getMessage()+"初始化失败");
//
//                return;
//            }
//
//            initialTts();
//
//
//        }
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                SystemClock.sleep(3000);
//
//                if (paAccessControl!=null)
//                    paAccessControl.startFrameDetect();
//
//            }
//        }).start();
//
//        tanChuangThread = new TanChuangThread();
//        tanChuangThread.start();
//
//        mHandler = new WeakHandler(new Handler.Callback() {
//            @Override
//            public boolean handleMessage(Message msg) {
//                switch (msg.what) {
//                    case 111:{
//                        yinying.setVisibility(View.VISIBLE);
//                        //  guang.setVisibility(View.VISIBLE);
//
//                        isShow=false;
//                        //弹窗
//                        if (linearLayout.getChildCount() > 0 && !isDH) {
//                            final View view1 = linearLayout.getChildAt(0);
//                            //消失动画(从右往左)
//                            ValueAnimator anim = ValueAnimator.ofFloat(1f, 0f);
//                            anim.setDuration(400);
//                            anim.setRepeatMode(ValueAnimator.RESTART);
//                            Interpolator interpolator = new DecelerateInterpolator();
//                            anim.setInterpolator(interpolator);
//                            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                                @Override
//                                public void onAnimationUpdate(ValueAnimator animation) {
//                                    float currentValue = (float) animation.getAnimatedValue();
//                                    // 获得改变后的值
////								System.out.println(currentValue);
//                                    // 输出改变后的值
//                                    // 步骤4：将改变后的值赋给对象的属性值，下面会详细说明
//                                    view1.setScaleX(currentValue);
//                                    view1.setScaleY(currentValue);
//                                    // 步骤5：刷新视图，即重新绘制，从而实现动画效果
//                                    view1.requestLayout();
//                                }
//                            });
//                            anim.addListener(new Animator.AnimatorListener() {
//                                @Override
//                                public void onAnimationStart(Animator animation) {
//                                    isDH = true;
//                                }
//
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    isDH = false;
//                                    linearLayout.removeView(view1);
//                                }
//
//                                @Override
//                                public void onAnimationCancel(Animator animation) {
//                                }
//
//                                @Override
//                                public void onAnimationRepeat(Animator animation) {
//                                }
//                            });
//                            anim.start();
//                        }
//
//
//                        Subject bean = (Subject) msg.obj;
//                        if (!bean.getName().equals("陌生人")){
//                           // bofang();
//                            menjing1();
//                            if (baoCunBean.getTishiyu()!=null)
//                            synthesizer.speak(baoCunBean.getTishiyu());
//                        }else {
//                            synthesizer.speak("无权限");
//                        }
//
//                        final View view1 = View.inflate(MianBanJiActivity2.this, R.layout.bootom_item, null);
//                        LinearLayout llll =  view1.findViewById(R.id.llllll);
//                        TextView name =  view1.findViewById(R.id.name);
//                        ImageView touxiang = view1.findViewById(R.id.touxiang);
//                        TextView bumen =  view1.findViewById(R.id.bumen);
//                        final ScrollView scrollView_03 = view1.findViewById(R.id.scrollview_03);
//                        final LinearLayout xiaoxi_ll = view1.findViewById(R.id.xiaoxi_ll);
//
//                        setxinxi(bean,scrollView_03,xiaoxi_ll);
//
//                        name.setText(bean.getName() + "");
//                        bumen.setText(bean.getDepartmentName() + "");
//
//                        if (bean.getDepartmentName().equals("访客") || bean.getDepartmentName().equals("VIP访客")){
//                            llll.setBackgroundResource(R.drawable.fangkebg3);
//                        }else if (bean.getName().equals("陌生人")){
//                            llll.setBackgroundResource(R.drawable.moshengrenbg3);
//                        }else {
//                            llll.setBackgroundResource(R.drawable.lanseygbg);
//                        }
//
//                        try {
//                            if (bean.getDisplayPhoto() != null) {
//
//                                Glide.with(MianBanJiActivity2.this)
//                                        .load(bean.getDisplayPhoto())
//                                        .apply(myOptions)
//                                        .into(touxiang);
//                              //  Log.d("MianBanJiActivity2", "ddasda");
//                            } else {
//                                if (bean.getTeZhengMa()!=null) {
//
//                                    Glide.with(MianBanJiActivity2.this)
//                                            .load(MyApplication.SDPATH3+File.separator+bean.getTeZhengMa()+".png")
//                                            .apply(myOptions)
//                                            .into(touxiang);
//                                }else {
//
//
//                                    Glide.with(MianBanJiActivity2.this)
//                                            .load(new BitmapDrawable(MianBanJiActivity2.this.getResources(),msrBitmap))
//                                            .apply(myOptions2)
//                                            .into(touxiang);
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//
//                        }
//
//                        view1.setY(dh);
//                        linearLayout.addView(view1);
//
//                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) touxiang.getLayoutParams();
//                        layoutParams.width = (int)(dw*0.22f);
//                        layoutParams.height = (int)(dw*0.22f);
//                        layoutParams.topMargin = -(int)(dw*0.1f);
//                        touxiang.setLayoutParams(layoutParams);
//                        touxiang.invalidate();
//
//                        LinearLayout.LayoutParams naeee = (LinearLayout.LayoutParams) name.getLayoutParams();
//                        naeee.topMargin = (int) (dh * 0.1f);
//                        name.setLayoutParams(naeee);
//                        name.invalidate();
//
//                        LinearLayout.LayoutParams sscr = (LinearLayout.LayoutParams) scrollView_03.getLayoutParams();
//                        sscr.topMargin = (int) (dh * 0.06f);
//                        scrollView_03.setLayoutParams(sscr);
//                        scrollView_03.invalidate();
//
//                        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) llll.getLayoutParams();
//                        layoutParams1.height = (int) (dh * 0.62f);
//                        layoutParams1.width = (int) (dw * 0.7f);
//                        llll.setLayoutParams(layoutParams1);
//                        llll.invalidate();
//
//
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
//
//                        //入场动画(从右往左)
//                        ValueAnimator anim = ValueAnimator.ofInt(dh,-((int) (dh * 0.02f)));
//                        anim.setDuration(400);
//                        anim.setRepeatMode(ValueAnimator.RESTART);
//                        Interpolator interpolator = new DecelerateInterpolator(2f);
//                        anim.setInterpolator(interpolator);
//                        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                            @Override
//                            public void onAnimationUpdate(ValueAnimator animation) {
//                                int currentValue = (Integer) animation.getAnimatedValue();
//                                //  Log.d("MianBanJiActivity2", "currentValue:" + currentValue);
//                                // 获得改变后的值
////								System.out.println(currentValue);
//                                // 输出改变后的值
//                                // 步骤4：将改变后的值赋给对象的属性值，下面会详细说明
//                                view1.setY(currentValue);
//                                // 步骤5：刷新视图，即重新绘制，从而实现动画效果
//                                view1.requestLayout();
//                            }
//                        });
//                        anim.addListener(new Animator.AnimatorListener() {
//                            @Override
//                            public void onAnimationStart(Animator animation) {
//                            }
//
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                            }
//
//                            @Override
//                            public void onAnimationCancel(Animator animation) {
//                            }
//
//                            @Override
//                            public void onAnimationRepeat(Animator animation) {
//                            }
//                        });
//                        anim.start();
//                        break;
//                    }
//
//                    case 222: {
//
//                        menjing2();
//
//                        if (linearLayout.getChildCount() > 0 && !isDH) {
//                            final View view1 = linearLayout.getChildAt(0);
//                            //消失动画(从右往左)
//                            ValueAnimator anim = ValueAnimator.ofFloat(1f, 0f);
//                            anim.setDuration(400);
//                            anim.setRepeatMode(ValueAnimator.RESTART);
//                            Interpolator interpolator = new DecelerateInterpolator();
//                            anim.setInterpolator(interpolator);
//                            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                                @Override
//                                public void onAnimationUpdate(ValueAnimator animation) {
//                                    float currentValue = (float) animation.getAnimatedValue();
//                                    // 获得改变后的值
////								System.out.println(currentValue);
//                                    // 输出改变后的值
//                                    // 步骤4：将改变后的值赋给对象的属性值，下面会详细说明
//                                    view1.setScaleX(currentValue);
//                                    view1.setScaleY(currentValue);
//                                    // 步骤5：刷新视图，即重新绘制，从而实现动画效果
//                                    view1.requestLayout();
//                                }
//                            });
//                            anim.addListener(new Animator.AnimatorListener() {
//                                @Override
//                                public void onAnimationStart(Animator animation) {
//                                    isDH = true;
//                                    yinying.setVisibility(View.GONE);
//                                    //guang.setVisibility(View.GONE);
//
//                                }
//
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    isDH = false;
//                                    linearLayout.removeAllViews();
//                                    isShow=true;
//
//
//                                }
//
//                                @Override
//                                public void onAnimationCancel(Animator animation) {
//                                }
//
//                                @Override
//                                public void onAnimationRepeat(Animator animation) {
//                                }
//                            });
//                            anim.start();
//                        }
//
//                        break;
//                    }
//
//                }
//                return false;
//            }
//        });
//
//        isSC=true;
//
//    }
//
//
//    @Override
//    protected void onResume() {
//
//        manager.open(getWindowManager(), 1, cameraWidth, cameraHeight);//前置是1
//        manager2.open(getWindowManager(), 0, cameraWidth, cameraHeight,90);//前置是1
//
//
//        guanPing();
////        new Thread(new Runnable() {
////            @Override
////            public void run() {
////                SystemClock.sleep(5000);
////                runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
////                        layoutParams1.height = (int) (dh * 0.52f);
////                        layoutParams1.width = (int) (dw * 0.5f);
////                        layoutParams1.topMargin=(int) (dw * 0.2f);
////                        cameraView.setLayoutParams(layoutParams1);
////                        cameraView.invalidate();
////
////                        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) cameraView2.getLayoutParams();
////                        layoutParams2.height = (int) (dh * 0.52f);
////                        layoutParams2.topMargin=(int) (dw * 0.2f);
////                        layoutParams2.width = (int) (dw * 0.5f);
////                        layoutParams2.leftMargin=(int) (dw * 0.5f);
////                        cameraView2.setLayoutParams(layoutParams2);
////                        cameraView2.invalidate();
////                    }
////                });
////
////            }
////        }).start();
//
//
//        super.onResume();
//    }
//
//
//    private YuvInfo rgb, ir;
//    /* 相机回调函数 */
//    @Override
//    public void onPictureTaken(CameraPreviewData cameraPreviewData) {
//        /* 如果SDK实例还未创建，则跳过 */
//
//        if (paAccessControl == null) {
//
//            return;
//        }
//
//        rgb = new YuvInfo(cameraPreviewData.nv21Data, SettingVar.cameraId, SettingVar.faceRotation, cameraPreviewData.width, cameraPreviewData.height);
//        if (!baoCunBean.isHuoTi()){
//          //  Log.d("MianBanJiActivity2", "进来");
//            paAccessControl.offerFrameBuffer(cameraPreviewData.nv21Data, cameraPreviewData.width, cameraPreviewData.height,SettingVar.faceRotation, SettingVar.cameraId);
//        }
//
//      //  paAccessControl.offerFrameBuffer(cameraPreviewData.nv21Data, cameraPreviewData.width, cameraPreviewData.height,SettingVar.faceRotation, SettingVar.getCaneraID());
//     //   Log.d("MianBanJiActivity2", "cameraPreviewData.rotation:" + cameraPreviewData.rotation+"   "+SettingVar.cameraId);
//        /* 将相机预览帧转成SDK算法所需帧的格式 FacePassImage */
//    }
////天波面板机 90  270
//
////深圳杨总面板机 270  270
//
////高通 90  90
//
//    /* 相机回调函数 */
//    @Override
//    public void onPictureTaken2(CameraPreviewData2 cameraPreviewData) {
//        /* 如果SDK实例还未创建，则跳过 */
//       // Log.d("MianBanJiActivity2", "cameraPreviewData2.rotation:" + cameraPreviewData.front);
//        if (paAccessControl == null) {
//            return;
//        }
//      //  paAccessControl.offerFrameBuffer(cameraPreviewData.nv21Data, cameraPreviewData.width, cameraPreviewData.height,SettingVar.faceRotation, SettingVar.getCaneraID());
//        ir = new YuvInfo(cameraPreviewData.nv21Data, cameraPreviewData.front, 90, cameraPreviewData.width, cameraPreviewData.height);
//        if (rgb==null || !baoCunBean.isHuoTi())
//            return;
//        int result = paAccessControl.offerIrFrameBuffer(rgb, ir);//提供数据到队列
//       // Log.d("MianBanJiActivity2", "cameraPreviewData.result:" + result);
//        /* 将相机预览帧转成SDK算法所需帧的格式 FacePassImage */
//    }
//
//
//
//    private class TanChuangThread extends Thread {
//        boolean isRing;
//
//        @Override
//        public void run() {
//            while (!isRing) {
//                try {
//                    //有动画 ，延迟到一秒一次
//                    Subject subject = linkedBlockingQueue.take();
//                    if (subject.getPeopleType() != null) {
//                        switch (subject.getPeopleType()) {
//                            case "员工": {
//                                Message message2 = Message.obtain();
//                                message2.what = 111;
//                                message2.obj = subject;
//                                mHandler.sendMessage(message2);
//                                break;
//                            }
//                            case "普通访客":{
//                                //普通访客
//                                subject.setDepartmentName("访客");
//                                Message message2 = Message.obtain();
//                                message2.what = 111;
//                                message2.obj = subject;
//                                mHandler.sendMessage(message2);
//
//                                break;
//                            }
//                            case "白名单":
//                                //vip
//                                subject.setDepartmentName("VIP访客");
//                                Message message2 = Message.obtain();
//                                message2.what = 111;
//                                message2.obj = subject;
//                                mHandler.sendMessage(message2);
//
//                                break;
//                            case "黑名单":
//
//                                break;
//                            default:
//                                EventBus.getDefault().post("没有对应身份类型,无法弹窗");
//
//                        }
//                    }
//                    SystemClock.sleep(410);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        @Override
//        public void interrupt() {
//            isRing = true;
//           // Log.d("RecognizeThread", "中断了弹窗线程");
//            super.interrupt();
//        }
//    }
//
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (paAccessControl!=null){
//            Log.d("MianBanJiActivity2", "停止");
//            paAccessControl.stopFrameDetect();
//        }
//
//    }
//
//
//
//    private void initView() {
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//        int windowRotation = ((WindowManager) (getApplicationContext().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getRotation() * 90;
////        if (windowRotation == 0) {
////            cameraRotation = 90;
////        } else if (windowRotation == 90) {
////            cameraRotation = 0;
////        } else if (windowRotation == 270) {
////            cameraRotation = 180;
////        } else {
////            cameraRotation = 270;
////        }
////        Log.i(DEBUG_TAG, "cameraRation: " + cameraRotation);
//        cameraFacingFront = true;
//        SharedPreferences preferences = getSharedPreferences(SettingVar.SharedPrefrence, Context.MODE_PRIVATE);
//        SettingVar.isSettingAvailable = preferences.getBoolean("isSettingAvailable", SettingVar.isSettingAvailable);
//        SettingVar.isCross = preferences.getBoolean("isCross", SettingVar.isCross);
//        SettingVar.faceRotation = preferences.getInt("faceRotation", SettingVar.faceRotation);
//        SettingVar.cameraPreviewRotation = preferences.getInt("cameraPreviewRotation", SettingVar.cameraPreviewRotation);
//        SettingVar.cameraFacingFront = preferences.getBoolean("cameraFacingFront", SettingVar.cameraFacingFront);
//        if (SettingVar.isSettingAvailable) {
//          //  cameraRotation = SettingVar.faceRotation;
//            cameraFacingFront = SettingVar.cameraFacingFront;
//        }
//
//      //  Log.d("MianBanJiActivity2", "cameraFacingFront:" + cameraFacingFront);
//
//        final int mCurrentOrientation = getResources().getConfiguration().orientation;
//
//        if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
//            screenState = 1;
//        } else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
//            screenState = 0;
//        }
//        setContentView(R.layout.activity_mianbanji3);
//
//        ButterKnife.bind(this);
//
//        linearLayout=findViewById(R.id.linearLayout);
//        // top_rl=findViewById(R.id.toprl);
//        shijian=findViewById(R.id.shijian);
//        riqi=findViewById(R.id.riqi);
//        wendu=findViewById(R.id.wendu);
//        xingqi=findViewById(R.id.xingqi);
//        tianqi=findViewById(R.id.tianqi);
//        tianqi_im=findViewById(R.id.tianqi_im);
//        gongsi=findViewById(R.id.gongsi);
//        tishi=findViewById(R.id.tishi);
//        ImageView shezhi = findViewById(R.id.shezhi);
//        // guang=findViewById(R.id.guang_im);
//        yinying=findViewById(R.id.yinying);
//        logo=findViewById(R.id.logo);
//
//        if (baoCunBean.getWenzi1()!=null)
//            gongsi.setText(baoCunBean.getWenzi1());
//        else
//            gongsi.setText("请从后台设置贵公司名");
//
//        clockView=findViewById(R.id.clockView);
//
//        riqi.setText(DateUtils.timesTwo(System.currentTimeMillis() + ""));
//        String shijianss=DateUtils.timeMinute(System.currentTimeMillis() + "");
//        shijian.setText(shijianss);
//
//        String sj[]=shijianss.split(":");
//
//        clockView.setTime(Integer.valueOf(sj[0]), Integer.valueOf(sj[1]), 0);
//        clockView.start();
//
//
//
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        heightPixels = displayMetrics.heightPixels;
//        widthPixels = displayMetrics.widthPixels;
//        SettingVar.mHeight = heightPixels;
//        SettingVar.mWidth = widthPixels;
//
//        /* 初始化界面 */
//        faceView = findViewById(R.id.fcview);
//        faceView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                MiMaDialog3 miMaDialog=new MiMaDialog3(MianBanJiActivity2.this,baoCunBean.getMima2());
//                WindowManager.LayoutParams params= miMaDialog.getWindow().getAttributes();
//                params.width=dw;
//                params.height=dh+60;
//                miMaDialog.getWindow().setGravity(Gravity.CENTER);
//                miMaDialog.getWindow().setAttributes(params);
//                miMaDialog.show();
//
//            }
//        });
//        manager = new CameraManager();
//        cameraView = (CameraPreview) findViewById(R.id.preview);
//        manager.setPreviewDisplay(cameraView);
//        /* 注册相机回调函数 */
//        manager.setListener(this);
//
//
//        manager2 = new CameraManager2();
//        cameraView2 =  findViewById(R.id.preview2);
//        manager2.setPreviewDisplay(cameraView2);
//        /* 注册相机回调函数 */
//        manager2.setListener(this);
//
//
//        LinearLayout bootm_ll=findViewById(R.id.bootom_ll);
//
//        xingqi.setText(DateUtils.getWeek(System.currentTimeMillis()));
//
//        if (todayBean != null) {
//            wendu.setText(todayBean.getTemperature());
//            tianqi.setText(todayBean.getWeather());
//
//            if (todayBean.getWeather().contains("晴")) {
//                tianqi_im.setBackgroundResource(R.drawable.qing);
//            } else if (todayBean.getWeather().contains("雨")) {
//                tianqi_im.setBackgroundResource(R.drawable.xiayu);
//            } else if (todayBean.getWeather().contains("多云")) {
//                tianqi_im.setBackgroundResource(R.drawable.duoyun);
//            } else if (todayBean.getWeather().contains("阴")) {
//                tianqi_im.setBackgroundResource(R.drawable.yintian);
//            }
//        }
//
//        LinearLayout.LayoutParams dp1 = (LinearLayout.LayoutParams) clockView.getLayoutParams();
//        dp1.width = (int) (dw * 0.44f);
//        dp1.height = (int) (dw * 0.44f);
//        clockView.setLayoutParams(dp1);
//        clockView.invalidate();
//
//        LinearLayout.LayoutParams dp133 = (LinearLayout.LayoutParams) tianqi_im.getLayoutParams();
//        dp133.width = (int) (dw * 0.1f);
//        dp133.height = (int) (dw * 0.1f);
//        tianqi_im.setLayoutParams(dp133);
//        tianqi_im.invalidate();
//
//        RelativeLayout.LayoutParams dp122 = (RelativeLayout.LayoutParams) tishi.getLayoutParams();
//        dp122.width = (int) (dw * 0.72f);
//        dp122.height = (int) (dh * 0.1f);
//        dp122.bottomMargin = (int) (dw * 0.12f);
//        tishi.setLayoutParams(dp122);
//        tishi.invalidate();
//
//
//        RelativeLayout.LayoutParams dp12 = (RelativeLayout.LayoutParams) bootm_ll.getLayoutParams();
//        dp12.height = (int) (dh * 0.23f);
//        bootm_ll.setLayoutParams(dp12);
//        bootm_ll.invalidate();
//
//
//        RelativeLayout.LayoutParams dp = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
//        dp.height = (int) (dh * 0.62f);
//        dp.width = (int) (dw * 0.7f);
//        linearLayout.setLayoutParams(dp);
//        linearLayout.invalidate();
//
//        shezhi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                MiMaDialog4 miMaDialog=new MiMaDialog4(MianBanJiActivity2.this,baoCunBean.getMima());
//                WindowManager.LayoutParams params= miMaDialog.getWindow().getAttributes();
//                params.width=dw;
//                params.height=dh;
////                miMaDialog.getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
//                miMaDialog.getWindow().setAttributes(params);
//                miMaDialog.show();
//            }
//        });
//
//        RelativeLayout.LayoutParams dp1333 = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
//        dp1333.topMargin = (int) (dh * 0.14f);
//        dp1333.bottomMargin = (int) (dh * 0.22f);
//        cameraView.setLayoutParams(dp1333);
//        cameraView.invalidate();
//
//    }
//
//    public static final int TIMEOUT2 = 1000 * 100;
//    private void link_P1() {
//
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .writeTimeout(TIMEOUT2, TimeUnit.MILLISECONDS)
//                .connectTimeout(TIMEOUT2, TimeUnit.MILLISECONDS)
//                .readTimeout(TIMEOUT2, TimeUnit.MILLISECONDS)
//                .cookieJar(new CookiesManager())
//                .retryOnConnectionFailure(true)
//                .build();
//
//        MultipartBody mBody;
//        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
//
//        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.bbjj11);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
//        int options = 100;
//        while (baos.toByteArray().length / 1024 > 500) {  //循环判断如果压缩后图片是否大于500kb,大于继续压缩
//            baos.reset();//重置baos即清空baos
//            options -= 10;//每次都减少10
//            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
//            long length = baos.toByteArray().length;
//        }
//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
//        Date date = new Date(System.currentTimeMillis());
//        String filename = format.format(date);
//        File file = new File(Environment.getExternalStorageDirectory(),filename+".png");
//        try {
//            FileOutputStream fos = new FileOutputStream(file);
//            try {
//                fos.write(baos.toByteArray());
//                fos.flush();
//                fos.close();
//            } catch (IOException e) {
//
//                e.printStackTrace();
//            }
//        } catch (FileNotFoundException e) {
//
//            e.printStackTrace();
//        }
//        RequestBody fileBody1 = RequestBody.create(MediaType.parse("application/octet-stream"),file);
//
//        builder.addFormDataPart("img",file.getName(), fileBody1);
//        //builder.addFormDataPart("subject_id","228");
//        mBody = builder.build();
//
//        Request.Builder requestBuilder = new Request.Builder()
//                .header("Content-Type", "application/json")
//                .post(mBody)
//                .url("http://47.110.128.4:8081/api/v1/user/upload/img");
//
//        // step 3：创建 Call 对象
//        Call call = okHttpClient.newCall(requestBuilder.build());
//
//        //step 4: 开始异步请求
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//
//                Log.d("AllConnects图片上传", "请求识别失败" + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//                    Log.d("AllConnects", "请求识别成功" + response.toString());
//                //获得返回体
//                try {
//                    ResponseBody body = response.body();
//                    String ss = body.string();
//                    Log.d("AllConnects图片上传", "传照片" + ss);
//
//                } catch (Exception e) {
//
//                    Log.d("AllConnects图片上传异常", e.getMessage());
//                }
//            }
//        });
//
//    }
//
//    OnPaAccessDetectListener onDetectListener = new OnPaAccessDetectListener() {
//
//
//        //每一帧数据的回调
//        @Override
//        public void onFaceDetectFrame(int message, PaAccessDetectFaceResult faceDetectFrame) {
//            // faceDetectFrame 未检测到人脸就为null，所以要做非空判断
//            if (message==1001){
//                faceId="";
//                feature2=-1;
//              //  tishi.setVisibility(View.GONE);
//            }
//           // Log.d("Robin", " faceDetectFrame: " +message);
////            faceRectView.setFaceRect(faceDetectFrame); //Robin 绘制人脸框 ,影响性能，去掉
////            if (message == 1001) { //Robin 未检测到人脸,隐藏人脸框
////                faceRectView.setVisibility(View.INVISIBLE);
////            } else {
////                faceRectView.setVisibility(View.VISIBLE);
////            }
////            if (System.currentTimeMillis() - mLastTime > 1000) { //Robin 超过1秒隐藏弹出的view
////                mRegisterView.setVisibility(View.INVISIBLE);
////            }
////
////            viewList.add(mTvTips);
////            viewList.add(mTvLiveScore);
////            viewList.add(mTvCompare);
////            viewList.add(mTvFacesNum);
////
////            if (mDevCp) { //Robin 开发者模式
////                setVisibleForViews(viewList, true);
////                showTips(message);
////            } else {
////                setVisibleForViews(viewList, false);
////            }
//        }
//
//        @Override
//        public void onFaceDetectResult(int var1,PaAccessDetectFaceResult detectResult) {
//           // Log.d("Robin","detectResult : " + detectResult.facePassFrame.blurness);
//          //  int message = detectResult.message;
//
//
////            if (mDevCp) { //Robin 显示实时人脸属性
////                setVisibleForViews(viewList, true);
////            } else {
////                setVisibleForViews(viewList, false);
////            }
////            if (detectResult.message != PaAccessControlMessage.RESULT_OK) {
////                mRegisterView.setVisibility(View.INVISIBLE);
////                return;
////            }
////            mTvLiveScore.setText("活体分数：" + detectResult.livenessScore);
////            if (mMLivenessRegister) { //Robin 开启了活体
////                //注意！活体阈值可根据文档介绍适当调整
////                if (detectResult.livenessScore < 0.8) { //Robin 不开启活体，就不进行活体检测
////                    viewList.add(mTvCompare);
////                    setVisibleForViews(viewList, false);
////                    mTvTips.setText("最终比对结果: " + "比对失败。活体分数小于阈值，非活体。说明：活体阈值可调节");
////                    mRegisterView.setVisibility(View.INVISIBLE);
////                    return;
////                }
////            } else { //Robin 未开启活体
////                mTvLiveScore.setText("未开启活体检测");
////            }
////
//            //1比N
//           // Log.d("MianBanJiActivity2", "var1:" + var1);
//            Log.d("MianBanJiActivity2", "detectResult:" + detectResult);
//            if (detectResult==null)
//                return;
//           // Log.d("MianBanJiActivity2", "detectResult.feature:" + detectResult.feature);
//            PaAccessCompareFacesResult paFacePassCompareResult = paAccessControl.compareFaceToAll(detectResult.feature);
//            if (paFacePassCompareResult==null || paFacePassCompareResult.message != PaAccessControlMessage.RESULT_OK) {
//                Log.d("MianBanJiActivity2", "没有人脸信息");
//                return;
//            }
//
//            //人脸信息完整的
//            final String id = paFacePassCompareResult.id;
//           // String gender = getGender(detectResult.gender);
//            boolean attriButeEnable = PaAccessControl.getInstance().getPaAccessDetectConfig().isAttributeEnabled(); //Robin 是否检测了人脸属性
//         //   Log.d("MianBanJiActivity2", "paFacePassCompareResult.compareScore:" + paFacePassCompareResult.compareScore);
//
////            //百分之一误识为0.52；千分之一误识为0.56；万分之一误识为0.60 比对阈值可根据实际情况调整
//            if (paFacePassCompareResult.compareScore > mCompareThres) {
//                feature2=detectResult.trackId;
//               // 不相等 弹窗
//                if (!id.equals(faceId)){
//                    faceId=id;
//                    final Subject subject = subjectBox.query().equal(Subject_.teZhengMa, id).build().findUnique();
//                    if (subject != null) {
//                        linkedBlockingQueue.offer(subject);
//                        // mianBanJiView.setBitmap(FileUtil.toRoundBitmap(mFacePassHandler.getFaceImage(result.faceToken)),subject.getName());
//                        // mianBanJiView.setType(1);
//                        //保存刷脸记录
////                                DaKaBean daKaBean=new DaKaBean();
////                                daKaBean.setId2(subject.getId()+"");
////                                daKaBean.setName(subject.getName());
////                                daKaBean.setBumen(subject.getDepartmentName()+"");
////                                daKaBean.setRenyuanleixing(subject.getPeopleType()+"");
////                                daKaBean.setTime(DateUtils.time(System.currentTimeMillis()+""));
////                                daKaBean.setDianhua(subject.getPhone()+"");
////                                daKaBeanBox.put(daKaBean);
////                                link_shangchuanjilu(subject);
//
//                    } else {
//                        EventBus.getDefault().post("没有查询到人员信息");
//
//                    }
//                }
//
//
////                //Robin 所有成功注册的图片都保存在根目录的paFacePass目录下
////                mRegisterView.setIvPhoto(Constants.FILE_PATH + File.separator + id + ".png");
////                mRegisterView.setVisibility(View.VISIBLE);
////                mRegisterView.setName(id);
////                mTvTips.setText("最终比对结果: " + "比对成功");
////                // 比对成功后可以调用  PaAccessControl.getInstance().stopOffer()停止检测，做一些处理。处理完成后使用Handler.postDelayed,PaAccessControl.getInstance().startOffer()开始检测
////            } else {
////                mTvTips.setText("最终比对结果: " + "比对失败。人脸相似度过低。");
////                mRegisterView.setVisibility(View.INVISIBLE);
////            }
////            mTempName = id; //Robin 记录上一次的id
////            mLastTime = System.currentTimeMillis();
//
//            }else {
//
//                //陌生人
//              //  Log.d("MianBanJiActivity2", "陌生人"+id);
////                if (isShow)
////                tishi.setVisibility(View.VISIBLE);
//
//                pp++;
//                if (pp>5) {
//                    pp = 0;
//                    if (feature2 == -1) {
//                        feature2 = detectResult.trackId;
//
//                        msrBitmap = nv21ToBitmap.nv21ToBitmap(detectResult.frame, detectResult.frameWidth, detectResult.frameHeight);
//                        // Bitmap bitmap = BitmapUtil.getBitmap(facePassFrame.frame, facePassFrame.frmaeWidth, facePassFrame.frameHeight, facePassFrame.frameOri);
//                        // bitmap = BitmapUtil.getCropBitmap(bitmap, facePassFrame.rectX, facePassFrame.rectY, facePassFrame.rectW, facePassFrame.rectH);
//
//                        //  tianqi_im.setImageBitmap(msrBitmap);
//                        // Log.d("MianBanJiActivity2", "msrBitmap:" + msrBitmap.getWidth());
//
//                        Subject subject1 = new Subject();
//                        //subject1.setW(bitmap.getWidth());
//                        //subject1.setH(bitmap.getHeight());
//                        //图片在bitmabToBytes方法里面做了循转
//                        // subject1.setTxBytes(BitmapUtil.bitmabToBytes(bitmap));
//                        subject1.setId(System.currentTimeMillis());
//                        subject1.setName("陌生人");
//                        subject1.setTeZhengMa(null);
//                        subject1.setPeopleType("员工");
//                        subject1.setDepartmentName("无权限!");
//                        linkedBlockingQueue.offer(subject1);
//
//                    } else if (feature2 != detectResult.trackId) {
//
//                        Bitmap bitmap = nv21ToBitmap.nv21ToBitmap(detectResult.frame, detectResult.frameWidth, detectResult.frameHeight);
//                        // Bitmap bitmap = BitmapUtil.getBitmap(facePassFrame.frame, facePassFrame.frmaeWidth, facePassFrame.frameHeight, facePassFrame.frameOri);
//                        //  bitmap = BitmapUtil.getCropBitmap(bitmap, facePassFrame.rectX, facePassFrame.rectY, facePassFrame.rectW, facePassFrame.rectH);
//                        Subject subject1 = new Subject();
//                        // subject1.setW(bitmap.getWidth());
//                        // subject1.setH(bitmap.getHeight());
//                        //图片在bitmabToBytes方法里面做了循转
//                        subject1.setTxBytes(BitmapUtil.bitmabToBytes(bitmap));
//                        subject1.setId(System.currentTimeMillis());
//                        subject1.setName("陌生人");
//                        subject1.setTeZhengMa(null);
//                        subject1.setPeopleType("员工");
//                        subject1.setDepartmentName("没有进入权限,请与前台联系!");
//                        linkedBlockingQueue.offer(subject1);
//
//                    }
//                }
//
//            }
//
//        }
//
//        @Override
//        public void onMultiFacesDetectFrameBaseInfo(int i, List<PaAccessMultiFaceBaseInfo> list) {
//
//            Log.d("MianBanJiActivity2", "list.size():" + list.size());
//
//        }
//    };
//
//
//
//
//
//    @Override
//    protected void onStop() {
//
//        SettingVar.isButtonInvisible = false;
//       // mToastBlockQueue.clear();
//        //mDetectResultQueue.clear();
//       // mFeedFrameQueue.clear();
//        if (manager != null) {
//            manager.release();
//        }
//      //  marqueeView.stopFlipping();
//        super.onStop();
//    }
//
//    @Override
//    protected void onRestart() {
//        //faceView.clear();
//        // faceView.invalidate();
//        //  if (shipingView!=null)
//        // shipingView.start();
//        super.onRestart();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        //marqueeView.startFlipping();
//    }
//
//
//    @Override
//    protected void onDestroy() {
//
//
//
//        if (tanChuangThread != null) {
//            tanChuangThread.isRing = true;
//            tanChuangThread.interrupt();
//        }
//
//        unregisterReceiver(timeChangeReceiver);
//
//        EventBus.getDefault().unregister(this);//解除订阅
//
//        if (manager != null) {
//            manager.release();
//        }
//
////        if (mFacePassHandler != null) {
////            mFacePassHandler.release();
////        }
//
//        if (synthesizer != null)
//            synthesizer.release();
//
//
//
//
//        timer.cancel();
//        if (task != null)
//            task.cancel();
//
//        super.onDestroy();
//    }
//
//
//    private static final int REQUEST_CODE_CHOOSE_PICK = 1;
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            //从相册选取照片后读取地址
//            case REQUEST_CODE_CHOOSE_PICK:
//                if (resultCode == RESULT_OK) {
//                    String path = "";
//                    Uri uri = data.getData();
//                    String[] pojo = {MediaStore.Images.Media.DATA};
//                    CursorLoader cursorLoader = new CursorLoader(this, uri, pojo, null, null, null);
//                    Cursor cursor = cursorLoader.loadInBackground();
//                    if (cursor != null) {
//                        cursor.moveToFirst();
//                        path = cursor.getString(cursor.getColumnIndex(pojo[0]));
//                    }
//                    if (!TextUtils.isEmpty(path) && "file".equalsIgnoreCase(uri.getScheme())) {
//                        path = uri.getPath();
//                    }
//                    if (TextUtils.isEmpty(path)) {
//                        try {
//                            path = FileUtil.getPath(getApplicationContext(), uri);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    if (TextUtils.isEmpty(path)) {
//                        toast("图片选取失败！");
//                        return;
//                    }
////                    if (!TextUtils.isEmpty(path) && mFaceOperationDialog != null && mFaceOperationDialog.isShowing()) {
////                        EditText imagePathEdt = (EditText) mFaceOperationDialog.findViewById(R.id.et_face_image_path);
////                        imagePathEdt.setText(path);
////                    }
//                }
//                break;
//        }
//    }
//
//
//
//    private void toast(String msg) {
//        Toast.makeText(MianBanJiActivity2.this, msg, Toast.LENGTH_SHORT).show();
//    }
//
//
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            if (keyCode == KeyEvent.KEYCODE_MENU) {
//                startActivity(new Intent(MianBanJiActivity2.this, SheZhiActivity2.class));
//                finish();
//            }
//
//        }
//
//        return super.onKeyDown(keyCode, event);
//
//    }
//
//
//
//    /**
//     * 初始化引擎，需要的参数均在InitConfig类里
//     * <p>
//     * DEMO中提供了3个SpeechSynthesizerListener的实现
//     * MessageListener 仅仅用log.i记录日志，在logcat中可以看见
//     * UiMessageListener 在MessageListener的基础上，对handler发送消息，实现UI的文字更新
//     * FileSaveListener 在UiMessageListener的基础上，使用 onSynthesizeDataArrived回调，获取音频流
//     */
//    protected void initialTts() {
//        // 设置初始化参数
//        SpeechSynthesizerListener listener = new UiMessageListener(mainHandler); // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
//        Map<String, String> params = getParams();
//        // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
//        InitConfig initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);
//        synthesizer = new NonBlockSyntherizer(this, initConfig, mainHandler); // 此处可以改为MySyntherizer 了解调用过程
//
//    }
//
//    /**
//     * 合成的参数，可以初始化时填写，也可以在合成前设置。
//     *
//     * @return
//     */
//    protected Map<String, String> getParams() {
//        Map<String, String> params = new HashMap<String, String>();
//        // 以下参数均为选填
//        params.put(SpeechSynthesizer.PARAM_SPEAKER, baoCunBean.getBoyingren() + ""); // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
//        params.put(SpeechSynthesizer.PARAM_VOLUME, "8"); // 设置合成的音量，0-9 ，默认 5
//        params.put(SpeechSynthesizer.PARAM_SPEED, baoCunBean.getYusu() + "");// 设置合成的语速，0-9 ，默认 5
//        params.put(SpeechSynthesizer.PARAM_PITCH, baoCunBean.getYudiao() + "");// 设置合成的语调，0-9 ，默认 5
//        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);         // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
//        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
//        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
//        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
//        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
//
//        // 离线资源文件
//        OfflineResource offlineResource = createOfflineResource(offlineVoice);
//        // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
//        params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
//        params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
//                offlineResource.getModelFilename());
//
//        return params;
//    }
//
//    protected OfflineResource createOfflineResource(String voiceType) {
//        OfflineResource offlineResource = null;
//        try {
//            offlineResource = new OfflineResource(this, voiceType);
//        } catch (IOException e) {
//            // IO 错误自行处理
//            e.printStackTrace();
//            // toPrint("【error】:copy files from assets failed." + e.getMessage());
//        }
//        return offlineResource;
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
//    public void onDataSynEvent(String event) {
//
//        if (event.equals("ditu123")) {
//            // if (baoCunBean.getTouxiangzhuji() != null)
//            //    daBg.setImageBitmap(BitmapFactory.decodeFile(baoCunBean.getTouxiangzhuji()));
//            baoCunBean = baoCunBeanDao.get(123456L);
//            if (baoCunBean.getWenzi1() != null)
//                gongsi.setText(baoCunBean.getWenzi1());
//
//            if (baoCunBean.getXiaBanTime() != null) {
//                logo.setImageBitmap(BitmapFactory.decodeFile(baoCunBean.getXiaBanTime()));
//            }
//            //   Log.d("MainActivity101", "dfgdsgfdgfdgfdg");
//            return;
//        }
//
//        if (event.equals("kaimen")){
//            menjing1();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    SystemClock.sleep(8000);
//                    menjing2();
//                }
//            }).start();
//            return;
//        }
//        if (event.equals("guanbimain")){
//            finish();
//            return;
//        }
//        Toast tastyToast = TastyToast.makeText(MianBanJiActivity2.this, event, TastyToast.LENGTH_LONG, TastyToast.INFO);
//        tastyToast.setGravity(Gravity.CENTER, 0, 0);
//        tastyToast.show();
//
//    }
//
//
//    class TimeChangeReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            switch (Objects.requireNonNull(intent.getAction())) {
//                case Intent.ACTION_TIME_TICK:
//                    //mianBanJiView.setTime(DateUtils.time(System.currentTimeMillis()+""));
//                   // String riqi11 = DateUtils.getWeek(System.currentTimeMillis()) + "   " + DateUtils.timesTwo(System.currentTimeMillis() + "");
//                    //  riqi.setTypeface(tf);
//
//                    riqi.setText(DateUtils.timesTwo(System.currentTimeMillis() + ""));
//                    shijian.setText(DateUtils.timeMinute(System.currentTimeMillis() + ""));
//                    xingqi.setText(DateUtils.getWeek(System.currentTimeMillis()));
//
//                    String xiaoshiss=DateUtils.timeMinute(System.currentTimeMillis() + "");
//                    if (xiaoshiss.split(":")[0].equals("06") && xiaoshiss.split(":")[1].equals("30")){
//                        Log.d("TimeChangeReceiver", "同步");
//                        final List<BenDiJiLuBean> benDiJiLuBeans=benDiJiLuBeanBox.getAll();
//                        final int size=benDiJiLuBeans.size();
//
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                for (int i=0;i<size;i++){
//                                    while (isSC){
//                                        isSC=false;
//                                        link_shangchuanjilu2(benDiJiLuBeans.get(i));
//                                    }
//
//                                }
//
//                            }
//                        }).start();
//
//                    }
//                    if (xiaoshiss.split(":")[0].equals("22") && xiaoshiss.split(":")[1].equals("30")){
//                        //删除今天的访客
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                List<Subject> subjectList=subjectBox.query().contains(Subject_.birthday,DateUtils.nyr(System.currentTimeMillis()+"")).and()
//                                        .equal(Subject_.peopleType,"普通访客")
//                                        .or().equal(Subject_.peopleType,"白名单").build().find();
//                                for (Subject s:subjectList){
//                                    Log.d("TimeChangeReceiver", s.toString());
//                                    try {
//                                        paAccessControl.deleteFaceById(s.getTeZhengMa());
//                                        subjectBox.remove(s);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//
//                                }
//
//                            }
//                        }).start();
//
//                    }
//                    if (xiaoshiss.split(":")[0].equals("10") && xiaoshiss.split(":")[1].equals("01")){
//                        //如果用户在22：30 前关了机 就需要第二天早上删除昨天的访客
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                List<Subject> subjectList=subjectBox.query().contains(Subject_.birthday,DateUtils.nyr((System.currentTimeMillis()-86400000)+"")).and()
//                                        .equal(Subject_.peopleType,"普通访客")
//                                        .or().equal(Subject_.peopleType,"白名单").build().find();
//                                for (Subject s:subjectList){
//                                    Log.d("TimeChangeReceiver", s.toString());
//                                    try {
//                                        paAccessControl.deleteFaceById(s.getTeZhengMa());
//                                        subjectBox.remove(s);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//
//                                }
//
//                            }
//                        }).start();
//
//                    }
//
//                    Date date = new Date();
//                    date.setTime(System.currentTimeMillis());
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.setTime(date);
//                    int t = calendar.get(Calendar.HOUR_OF_DAY);
//
//                    //每过一分钟 触发
//                    if (baoCunBean != null && baoCunBean.getDangqianShiJian()!=null && !baoCunBean.getDangqianShiJian().equals(DateUtils.timesTwo(System.currentTimeMillis() + "")) && t >= 6) {
//
//                        //一天请求一次
//                        try {
//                            if (baoCunBean.getDangqianChengShi2() == null) {
////                                Toast tastyToast = TastyToast.makeText(MianBanJiActivity2.this, "获取天气失败,没有设置当前城市", TastyToast.LENGTH_LONG, TastyToast.INFO);
////                                tastyToast.setGravity(Gravity.CENTER, 0, 0);
////                                tastyToast.show();
//                                return;
//                            }
//                            Log.d("TimeChangeReceiver", baoCunBean.getDangqianChengShi());
//                            okHttpClient = new OkHttpClient();
//                            okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
//                                    .get()
//                                    .url("http://apis.juhe.cn/simpleWeather/query?city=" +
//                                            baoCunBean.getDangqianChengShi() + "&key=8ee00f3d480b636d67d6a8966f07ffb7");
////                                    .url("http://v.juhe.cn/weather/index?format=1&cityname=" +
////                                            baoCunBean.getDangqianChengShi() + "&key=356bf690a50036a5cfc37d54dc6e8319");
//
//                            // step 3：创建 Call 对象
//                            Call call = okHttpClient.newCall(requestBuilder.build());
//                            //step 4: 开始异步请求
//                            call.enqueue(new Callback() {
//                                @Override
//                                public void onFailure(Call call, IOException e) {
//                                    Log.d("AllConnects", "请求失败" + e.getMessage());
//                                }
//
//                                @Override
//                                public void onResponse(Call call, okhttp3.Response response) throws IOException {
//                                    Log.d("AllConnects", "请求成功" + call.request().toString());
//                                    //获得返回体
//                                    try {
//
//                                        ResponseBody body = response.body();
//                                        String ss = body.string().trim();
//                                        Log.d("AllConnects", "天气" + ss);
//                                        JsonObject jsonObject = GsonUtil.parse(ss).getAsJsonObject();
//                                        Gson gson = new Gson();
//                                        final TQBean renShu = gson.fromJson(jsonObject, TQBean.class);
//
//                                        final TodayBean todayBean = new TodayBean();
//                                        todayBean.setId(123456L);
//                                        todayBean.setTemperature(renShu.getResult().getFuture().get(0).getTemperature());//温度
//                                        todayBean.setWeather(renShu.getResult().getRealtime().getInfo()); //天气
//                                        todayBean.setWind(renShu.getResult().getRealtime().getDirect()); //风力
//                                        todayBean.setUv_index(renShu.getResult().getRealtime().getAqi()); //紫外线
//                                        todayBean.setHumidity(renShu.getResult().getRealtime().getHumidity());//湿度
//                                        //todayBean.setDressing_advice(renShu.getResult().getRealtime().getDressing_advice());
//
//                                        todayBeanBox.put(todayBean);
//                                        baoCunBean.setDangqianShiJian(DateUtils.timesTwo(System.currentTimeMillis() + ""));
//                                        baoCunBeanDao.put(baoCunBean);
//
//
//
//                                        //更新界面
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                // AssetManager mgr = getAssets();
//                                                //Univers LT 57 Condensed
//                                                //  Typeface tf = Typeface.createFromAsset(mgr, "fonts/Univers LT 57 Condensed.ttf");
//                                                // Typeface tf2 = Typeface.createFromAsset(mgr, "fonts/hua.ttf");
//                                                //  String riqi2 = DateUtils.timesTwo(System.currentTimeMillis() + "") + "   " + DateUtils.getWeek(System.currentTimeMillis());
//
//                                                //   wendu.setTypeface(tf2);
//                                                //  tianqi.setTypeface(tf2);
//                                                //  fengli.setTypeface(tf2);
//                                                //  ziwaixian.setTypeface(tf2);
//                                                // shidu.setTypeface(tf2);
//                                                // jianyi.setTypeface(tf2);
//
//                                                // xingqi.setText(DateUtils.getWeek(System.currentTimeMillis()));
//                                                riqi.setText(DateUtils.timesTwodian(System.currentTimeMillis() + ""));
//
//                                                wendu.setText(todayBean.getTemperature());
//                                                tianqi.setText(todayBean.getWeather());
//
//                                                xingqi.setText(DateUtils.getWeek(System.currentTimeMillis()));
//
//                                                //  ziwaixian.setText("紫外线强度");
////                                                if (todayBean.getUv_index().contains("强")) {
////                                                    qiangdu_bg.setBackgroundResource(R.drawable.qiang_tq);
////                                                } else if (todayBean.getUv_index().contains("弱")) {
////                                                    qiangdu_bg.setBackgroundResource(R.drawable.ruo_tq);
////                                                } else if (todayBean.getUv_index().contains("中等")) {
////                                                    qiangdu_bg.setBackgroundResource(R.drawable.zhongdeng_tq);
////                                                }
//
//                                                if (todayBean.getWeather().contains("晴")) {
//                                                    tianqi_im.setBackgroundResource(R.drawable.qing);
//                                                } else if (todayBean.getWeather().contains("雨")) {
//                                                    tianqi_im.setBackgroundResource(R.drawable.xiayu);
//                                                } else if (todayBean.getWeather().contains("多云")) {
//                                                    tianqi_im.setBackgroundResource(R.drawable.duoyun);
//                                                } else if (todayBean.getWeather().contains("阴")) {
//                                                    tianqi_im.setBackgroundResource(R.drawable.yintian);
//                                                }
//
//                                            }
//                                        });
//                                        //把所有人员的打卡信息重置
//                                        List<Subject> subjectList = subjectBox.getAll();
//                                        for (Subject s : subjectList) {
//                                            s.setDaka(0);
//                                            subjectBox.put(s);
//                                        }
//
//                                    } catch (Exception e) {
//                                        Log.d("WebsocketPushMsg", e.getMessage() + "ttttt");
//                                    }
//
//                                }
//                            });
//                        } catch (Exception e) {
//                            e.printStackTrace();
////                            Toast tastyToast = TastyToast.makeText(MianBanJiActivity2.this, "获取天气失败,没有设置当前城市", TastyToast.LENGTH_LONG, TastyToast.INFO);
////                            tastyToast.setGravity(Gravity.CENTER, 0, 0);
////                            tastyToast.show();
//                            return;
//                        }
//
//                    }
//
//
//                    break;
//                case Intent.ACTION_TIME_CHANGED:
//                    //设置了系统时间
//                    // Toast.makeText(context, "system time changed", Toast.LENGTH_SHORT).show();
//                    break;
//                case Intent.ACTION_TIMEZONE_CHANGED:
//                    //设置了系统时区的action
//                    //  Toast.makeText(context, "system time zone changed", Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }
//    }
//
//
//    //轮播适配器
//    private class TestNomalAdapter extends StaticPagerAdapter {
//        private int[] imgs = {
//                R.drawable.dbg_1,
//                R.drawable.ceshi,
//                R.drawable.ceshi3,
//        };
//
//        @Override
//        public View getView(ViewGroup container, int position) {
//            ImageView view = new ImageView(container.getContext());
//            view.setImageResource(imgs[position]);
//            view.setScaleType(ImageView.ScaleType.FIT_XY);
//            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            return view;
//        }
//
//        @Override
//        public int getCount() {
//            return imgs.length;
//        }
//    }
//
//
//    //上传识别记录
//    private void link_shangchuanjilu(final Subject subject) {
//       // final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//
//        RequestBody body = null;
//
//        body = new FormBody.Builder()
//                //.add("name", subject.getName()) //
//                //.add("companyId", subject.getCompanyId()+"") //公司di
//                //.add("companyName",subject.getCompanyName()+"") //公司名称
//                //.add("storeId", subject.getStoreId()+"") //门店id
//                //.add("storeName", subject.getStoreName()+"") //门店名称
//                .add("subjectId", subject.getId() + "") //员工ID
//                .add("subjectType", subject.getPeopleType()==null?"员工":subject.getPeopleType()) //人员类型
//                // .add("department", subject.getPosition()+"") //部门
//                .add("discernPlace",baoCunBean.getTuisongDiZhi()+"")//识别地点
//                // .add("discernAvatar",  "") //头像
//                .add("identificationTime", DateUtils.time(System.currentTimeMillis() + ""))//时间
//                .build();
//
//
//        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
//                .header("Content-Type", "application/json")
//                .post(body)
//                .url(baoCunBean.getHoutaiDiZhi() + "/app/historySave");
//
//        // step 3：创建 Call 对象
//        Call call = okHttpClient.newCall(requestBuilder.build());
//
//        //step 4: 开始异步请求
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("AllConnects", "请求失败" + e.getMessage());
//                BenDiJiLuBean bean=new BenDiJiLuBean();
//                bean.setSubjectId(subject.getId());
//                bean.setDiscernPlace(baoCunBean.getTuisongDiZhi()+"");
//                bean.setSubjectType(subject.getPeopleType());
//                bean.setIdentificationTime(DateUtils.time(System.currentTimeMillis() + ""));
//                benDiJiLuBeanBox.put(bean);
//
//                List<BenDiJiLuBean> bb=  benDiJiLuBeanBox.getAll();
//                for (int i=0;i<bb.size();i++){
//                    Log.d("MainActivity2", bb.toString());
//                }
//            }
//
//            @Override
//            public void onResponse(Call call, okhttp3.Response response) throws IOException {
//                Log.d("AllConnects", "请求成功" + call.request().toString());
//                //获得返回体
//                try {
//                    ResponseBody body = response.body();
//                    String ss = body.string().trim();
//                    Log.d("AllConnects", "上传识别记录" + ss);
//
//
//                } catch (Exception e) {
//                    BenDiJiLuBean bean=new BenDiJiLuBean();
//                    bean.setSubjectId(subject.getId());
//                    bean.setDiscernPlace(baoCunBean.getTuisongDiZhi()+"");
//                    bean.setSubjectType(subject.getPeopleType());
//                    bean.setIdentificationTime(DateUtils.time(System.currentTimeMillis() + ""));
//                    benDiJiLuBeanBox.put(bean);
//
//                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
//                }
//            }
//        });
//    }
//
//    //信鸽信息处理
//    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
//    public void onDataSynEvent(XGBean xgBean) {
//        if (paAccessControl!=null){
//            try {
//                paAccessControl.stopFrameDetect();
//                tsxxChuLi.setData(xgBean, MianBanJiActivity2.this,paAccessControl);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//
//
//    //上传识别记录2
//    private void link_shangchuanjilu2(final BenDiJiLuBean subject) {
//      //  final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//
//        RequestBody body = null;
//
//        body = new FormBody.Builder()
//                //.add("name", subject.getName()) //
//                //.add("companyId", subject.getCompanyId()+"") //公司di
//                //.add("companyName",subject.getCompanyName()+"") //公司名称
//                //.add("storeId", subject.getStoreId()+"") //门店id
//                //.add("storeName", subject.getStoreName()+"") //门店名称
//                .add("subjectId", subject.getSubjectId() + "") //员工ID
//                .add("subjectType", subject.getSubjectType()+"") //人员类型
//                // .add("department", subject.getPosition()+"") //部门
//                .add("discernPlace",baoCunBean.getTuisongDiZhi()+"")//识别地点
//                // .add("discernAvatar",  "") //头像
//                .add("identificationTime",subject.getIdentificationTime()+"")//时间
//                .build();
//
//
//        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
//                .header("Content-Type", "application/json")
//                .post(body)
//                .url(baoCunBean.getHoutaiDiZhi() + "/app/historySave");
//
//        // step 3：创建 Call 对象
//        Call call = okHttpClient.newCall(requestBuilder.build());
//
//        //step 4: 开始异步请求
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("AllConnects", "请求失败" + e.getMessage());
//                isSC=true;
//            }
//
//            @Override
//            public void onResponse(Call call, okhttp3.Response response) throws IOException {
//                Log.d("AllConnects", "请求成功" + call.request().toString());
//                //获得返回体
//                try {
//                    ResponseBody body = response.body();
//                    String ss = body.string().trim();
//                    Log.d("AllConnects", "上传识别记录" + ss);
//                    //成功的话 删掉本地保存的记录
//                    benDiJiLuBeanBox.remove(subject.getId());
//
//                } catch (Exception e) {
//
//                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
//
//                }finally {
//                    isSC=true;
//                }
//            }
//        });
//    }
//
//
//    private void bofang(){
//
//        mMediaPlayer=MediaPlayer.create(MianBanJiActivity2.this, R.raw.deng2);
//
//        mMediaPlayer.start();
//
//    }
//
//
//
//
//
//
//    private void kaiPing(){
//
//        sendBroadcast(new Intent("com.android.internal.policy.impl.showNavigationBar"));
//        sendBroadcast(new Intent("com.android.systemui.statusbar.phone.statusopen"));
//    }
//    private void guanPing(){
//
//        sendBroadcast(new Intent("com.android.internal.policy.impl.hideNavigationBar"));
//        sendBroadcast(new Intent("com.android.systemui.statusbar.phone.statusclose"));
//    }
//
//    private void menjing1(){
//        // TPS980PosUtil.setJiaJiPower(1);
//        TPS980PosUtil.setRelayPower(1);
//        Log.d("MianBanJiActivity2", "打开");
//    }
//
//    private void menjing2(){
//        //  TPS980PosUtil.setJiaJiPower(0);
//        TPS980PosUtil.setRelayPower(0);
//        Log.d("MianBanJiActivity2", "关闭");
//    }
//
//
//
//    /**
//     * 获取本地化后的config
//     * 注册和比对使用不同的设置
//     */
//    private void initFaceConfig() {
//        //Robin 使用比对的设置
//
//        PaAccessDetectConfig faceDetectConfig = PaAccessControl.getInstance().getPaAccessDetectConfig();
//
//            faceDetectConfig.setFaceConfidenceThreshold(0.85f); //检测是不是人的脸。默认使用阀值 0.85f，阈值视具体 情况而定，最大为 1。
//            faceDetectConfig.setYawThreshold(40);//人脸识别角度
//            faceDetectConfig.setRollThreshold(40);
//            faceDetectConfig.setPitchThreshold(40);
//            // 注册图片模糊度可以设置0.9f（最大值1.0）这样能让底图更清晰。比对的模糊度可以调低一点，这样能加快识别速度，识别模糊度建议设置0.1f
//            faceDetectConfig.setBlurnessThreshold(0.4f);
//            faceDetectConfig.setMinBrightnessThreshold(30); // 人脸图像最小亮度阀值，默认为 30，数值越小越 暗，太暗会影响人脸检测和活体识别，可以根据 需求调整。
//            faceDetectConfig.setMaxBrightnessThreshold(240);// 人脸图像最大亮度阀值，默认为 240，数值越大 越亮，太亮会影响人脸检测和活体识别，可以根 据需求调整。
//            faceDetectConfig.setAttributeEnabled(false);//人脸属性开关，默认关闭。会检测出人脸的性别 和年龄。人脸属性的检测会消耗运算资源，可视 情况开启，未开启性别和年龄都返回-1
//            faceDetectConfig.setLivenessEnabled(baoCunBean.isHuoTi());//活体开关
//            faceDetectConfig.setTrackingMode(baoCunBean.isHuoTi()); //Robin 跟踪模式跟踪模式，开启后会提高检脸检出率，减小检脸耗时。门禁场景推荐开启。图片检测会强制关闭
//            faceDetectConfig.setIrEnabled(baoCunBean.isHuoTi()); //非Ir模式，因为是单例模式，所以最好每个界面都设置是否开启Ir模式
//            faceDetectConfig.setMinScaleThreshold(0.1f);//设置最小检脸尺寸，可以用这个来控制最远检脸距离。默认采用最小值 0.1，约 1.8 米，在 640*480 的预览分辨率下，最小人脸尺寸 为(240*0.1)*(240*0.1)即 24*24。 0.2 的最远 识别距离约 1.2 米;0.3 的最远识别距离约约 0.8 米。detectFaceMinScale 取值范围 [0.1,0.3]。门禁场景推荐 0.1;手机场景推荐 0.3。
//            PaAccessControl.getInstance().setPaAccessDetectConfig(faceDetectConfig);
//
//
//    }
//
//    //0表示男性，1表示女性
//    private String getGender(int gender) {
//        return gender == 0 ? "男" : "女";
//    }
//
//    private void setxinxi(Subject bean2, final ScrollView scrollView_03, final LinearLayout xiaoxi_ll){
//        //0小邮局 1生日提醒 2入职关怀 3节日关怀
//        final List<GuanHuai> ygguanHuaiList = guanHuaiBox.query().equal(GuanHuai_.employeeId, bean2.getId()).build().find();
//        final List<GuanHuai> ygguanHuaiList2 = guanHuaiBox.query().equal(GuanHuai_.employeeId, 0L).build().find();
//        if (ygguanHuaiList2.size() > 0) {
//            ygguanHuaiList.addAll(ygguanHuaiList2);
//        }
//        //信息推送
//        List<XinXiAll> xinXiAlls = xinXiAllBox.getAll();
//        for (XinXiAll all : xinXiAlls) {
//            if (all.getEmployeeId().equals("0") && all.getStartTime() <= System.currentTimeMillis()) {
//                GuanHuai guanHuai1 = new GuanHuai();
//                guanHuai1.setMarkedWords(all.getEditNews());
//                guanHuai1.setNewsStatus("");
//                guanHuai1.setProjectileStatus("4");
//                ygguanHuaiList.add(guanHuai1);
//
//            } else {
//                //查出来
//                final List<XinXiIdBean> xinXiIdBeans = xinXiIdBeanBox.query().equal(XinXiIdBean_.ygid, bean2.getId()).build().find();
//                for (XinXiIdBean idBean : xinXiIdBeans) {
//                    if (idBean.getUuid().equals(all.getId()) && all.getStartTime() <= System.currentTimeMillis()) {
//                        GuanHuai guanHuai1 = new GuanHuai();
//                        guanHuai1.setMarkedWords(all.getEditNews());
//                        guanHuai1.setNewsStatus("");
//                        guanHuai1.setProjectileStatus("4");
//                        ygguanHuaiList.add(guanHuai1);
//                    }
//                }
//            }
//        }
//
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                final int si = ygguanHuaiList.size();
//                for (int i = 0; i < si; i++) {
//
//                    final int finalI = i;
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            final View view_xiaoxi = View.inflate(MianBanJiActivity2.this, R.layout.xiaoxi_item, null);
//                            RelativeLayout rl_xiaoxi = view_xiaoxi.findViewById(R.id.rl_xiaoxi);
//                            TextView neirong = view_xiaoxi.findViewById(R.id.neirong);
//
//                            switch (ygguanHuaiList.get(finalI).getProjectileStatus()) {
//                                case "0":
//                                    //小邮局
//                                    try {
//
//                                        neirong.setText(ygguanHuaiList.get(finalI).getMarkedWords());
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                    break;
//                                case "1":
//                                    // 生日提醒
//
//                                    try {
//
//                                        neirong.setText(ygguanHuaiList.get(finalI).getMarkedWords());
//
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                    break;
//                                case "2":
//                                    //入职关怀
//
//                                    try {
//                                        neirong.setText(ygguanHuaiList.get(finalI).getMarkedWords());
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                    break;
//                                case "3":
//                                    //节日关怀
//
//                                    try {
//                                        neirong.setText(ygguanHuaiList.get(finalI).getMarkedWords());
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                    break;
//                                case "4":
//                                    //节日关怀
//
//                                    try {
//                                        neirong.setText(ygguanHuaiList.get(finalI).getMarkedWords());
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                    break;
//                            }
//
//                            view_xiaoxi.setY(dh);
//                            xiaoxi_ll.addView(view_xiaoxi);
//
//                            LinearLayout.LayoutParams layoutParams6 = (LinearLayout.LayoutParams) rl_xiaoxi.getLayoutParams();
//                            layoutParams6.bottomMargin = 10;
//                            layoutParams6.topMargin = 10;
//                            layoutParams6.height = ((int) ((float) dh * 0.05));
//                            rl_xiaoxi.setLayoutParams(layoutParams6);
//                            rl_xiaoxi.invalidate();
//
//
//                            float sfff = 20 + ((float) dh * 0.05f);
//
//                            ValueAnimator animator = ValueAnimator.ofFloat(dh, sfff * finalI);
//                            //动画时长，让进度条在CountDown时间内正好从0-360走完，
//                            animator.setDuration(1000);
//                            animator.setInterpolator(new DecelerateInterpolator());//匀速
//                            animator.setRepeatCount(0);//0表示不循环，-1表示无限循环
//                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                                @Override
//                                public void onAnimationUpdate(ValueAnimator animation) {
//                                    /**
//                                     * 这里我们已经知道ValueAnimator只是对值做动画运算，而不是针对控件的，因为我们设置的区间值为0-1.0f
//                                     * 所以animation.getAnimatedValue()得到的值也是在[0.0-1.0]区间，而我们在画进度条弧度时，设置的当前角度为360*currentAngle，
//                                     * 因此，当我们的区间值变为1.0的时候弧度刚好转了360度
//                                     */
//                                    float jiaodu = (float) animation.getAnimatedValue();
//                                    view_xiaoxi.setY(jiaodu);
//
//                                }
//                            });
//                            animator.start();
//
//                            scrollView_03.fullScroll(ScrollView.FOCUS_DOWN);
//                        }
//                    });
//                    SystemClock.sleep(600);
//
//                }
//            }
//        }).start();
//    }
//
//}
