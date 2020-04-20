package megvii.testfacepass.pa.utils;

import java.io.File;

/**
 * Created by liuyuan on 2017/12/26.
 * 接口代码
 */

public class ApiCode {
    public static final String APP_ID = "pp_app_id";//公钥appId 云π平台生成,并写在代码中
    public static final String APP_KEY = "paic#01234@56789";//公钥appKey 云π平台生成,并写在代码中

    public static final String HTTP_URL = "/api/";
    public static final String getAccessKey = HTTP_URL + "device/getAccessKey";//根据公钥获取私钥
    public static final String getServerTime = HTTP_URL + "device/getServerTime";//获取服务器时间
    public static final String getHearbeat = HTTP_URL + "device/hearbeat";//心跳
    public static final String getAcmInfo = HTTP_URL + "device/getAcmInfo";//根据设备ID获取设备信息
    public static final String queryFtrList = HTTP_URL + "repository/queryFtrList";//全量特征
    public static final String syncFtr = HTTP_URL + "repository/syncFtr";//增量特征
    public static final String imageRecog = HTTP_URL + "recognize/face/imageRecog";//图片识别
    public static final String recordUpload = HTTP_URL + "repository/recognize/record/upload";//识别记录上传
    public static final String snapshotUpload = HTTP_URL + "snapshot/upload";//抓拍记录上传
    public static final String reconcilation = HTTP_URL + "repository/recognize/record/accountCheck";//识别记录对账
    public static final String uploadReconcilationFile = HTTP_URL + "repository/recognize/record/uploadCheckResultFile";//对账文件上传
    public static final String heartbeatCheck = HTTP_URL + "device/receiveResult";//心跳伪推送状态回传
    public static final String uploadRuntimeLog = HTTP_URL + "device/analysis/uploadRuntimeLog";
	public static final String uploadDetectInfo = HTTP_URL + "device/analysis/uploadDetectInfo";//上报用于分析的检测信息接口

    /**
     * 获取token post
     */
    public static final String GETTOKEN = "/pai/v1/data/token";

    /**
     * 获取算法机信息 post
     */
    public static final String GETARITHMACHINEINFO = HTTP_URL + "getArithMachineInfo";

    /**
     * 获取人脸特征 post
     */
    public static final String GETFACEFEATURE = HTTP_URL + "getFaceFeature";

    /**
     * 同步人脸特征 post
     */
    public static final String SYNCHRONIZATIONFEATURE = HTTP_URL + "synchronizationFeature";

    /**
     * 发送识别结果 post
     */
    public static final String GETRECOGNIZERESULT = HTTP_URL + "getRecognizeResult";

    /**
     * 心跳 post
     */
    public static final String HEARTBEAT = HTTP_URL + "heartbeat";
    /**
     * 心跳 post
     */
    public static final String DORECOGNIZE = HTTP_URL + "doRecognize";

    /**
     * 后端识别并返回数据 post
     */
    public static final String SYSTEMINFOSYNC = HTTP_URL + "systemInfoSync";

    /**
     * token过期或失效错误
     */
    public static final String TOKENERROR = "The accessToken is empty or invalid.";

    /**
     * 文件夹名称
     */
    public static final String FILEDIR = "EntranceGuard";


    /**
     * 文件夹名称
     */
    public static final String SNAPSHOTFILEDIR = "EntranceGuard" + File.separator + "snapshotPic";

    /**
     * volley错误代码
     */
    public static final String VOLLEY_ERROR_1 = "服务器连接失败";
    public static final String VOLLEY_ERROR_2 = "网络超时,请检查网络连接.";
    public static final String VOLLEY_ERROR_3 = "解析异常";
    public static final String VOLLEY_ERROR_4 = "服务器异常";
    public static final String VOLLEY_ERROR_5 = "未知错误";
    public static final String VOLLEY_ERROR_6 = "请求数据失败，请退出APP后重试！";
    public static final String VOLLEY_ERROR_7 = "地址验证失败,请检查地址是否正确!";
    public static final String VOLLEY_ERROR_8 = "无数据";
    public static final String CAMERAERROR = "相机调用失败,请确认设备已经适配或相机连接正常";
    public static final String SERVICEERROR = "同步特征服务启动异常";
    public static final String APPSERVICSTARTEERROR = "监听服务启动异常";
    public static final String APPSERVICESTOPERROR = "监听服务停止异常";
    public static final String APPSERVICEERROR = "监听服务异常";
    public static final String LOGSERVICSTARTEERROR = "日志服务启动异常";
    public static final String LOGSERVICSTOPEERROR = "日志服务停止异常";

    //已经改为从π获取,如果π获取不到，就取这个值
    public static final int DETECTTIME = 3000;//定时任务用于多次识别的逻辑 非常重要，关乎到识别成功后弹出框的显示时间
    public static final int DEFAULT_FTRSYNCSECONDS = 10;//默认的同步特征时间，10秒

    public static final int DEFAULT_TRACKID_VALUE = -1;//上一个trackId默认-1

//    public static final int DEFAULT_LIVENESS_COUNT = 30;//默认的活体失败报警次数，当同一个trackId达到次数时报警
//    public static final int DEFAULT_COMPARE_COUNT = 5;//默认的比对失败次数，当同一个trackId达到次数时UI进行提示
    public static final int DEFAULT_LIVENESS_COUNT = 50;//默认的活体失败报警次数，当同一个trackId达到次数时报警
    public static final int DEFAULT_COMPARE_COUNT = 15;//默认的比对失败次数，当同一个trackId达到次数时UI进行提示
    public static final int DEFAULT_LOSE_FRAME_REAL_COUNT = 2;//真实场景默认丢失帧数
    public static final int DEFAULT_LOSE_FRAME_SHOW_COUNT = 3;//展示场景默认丢失帧数
    public static final int DELETE_FILE_MINUTS = 30;//删除图片时间

    public static final int SAVE_PIC_UNLIVENESS_NUM = 5;
    public static final int SAVE_PIC_COMPAREFAIL_NUM = 3;

    public static final int KILL_HOUR = 3;//定时杀死自己 小时
    public static final int KILL_MINUTE = 0;//定时杀死自己 分钟

    //门禁发给看门狗的广播
    public static final String BROADCAST_GUARD2DOG = "ruitong.tech.guard2dog";
    //看门狗发给门禁的广播
    public static final String BROADCAST_DOG2GUARD = "ruitong.tech.dog2guard";
    //服务名称
    public static final String MYAPPSERVICENAME = "com.ruitong.tech.entranceguard.service.MyAppService";
    //看门狗包名
    public static final String WATCHDOG_PACKAGENAME = "com.ruitong.tech.guardwatchdog";
    //appService检测间隔
    public static final int APPSERVICE_SECOND = 60 * 1000;
    //广播任务重复间隔
    public static final int BROADCAST_SECOND = 10 * 1000;
    //丢失心跳几次后拉起门禁次数
    public static final int PUSH_GUARD_NUM = 2;
    //丢失心跳几次后重启门禁机
    public static final int REBOOT_DEVICE_NUM = 10;
    //看门狗相关的日志标签
    public static final String DOG_TAG = "dog_tag";

    /**
     * 每页数量
     */
    public static final int PAGESIZE = 100;

    /**
     * 每页数量
     */
    public static final int PAGESIZE_BIG = 200;

    /**
     * 定时任务时间间隔(单位 秒)
     */
    public static final int TIMESECOND = 600;

    public static final int HEARTBEATSECOND = 60;//心跳(单位 s)

    /**
     * 算法版本南京小模型
     */
    public static final int ALGORITHM_VERSION_2000 = 2000;

    /**
     * 算法版本深圳小模型
     */
    public static final int ALGORITHM_VERSION_4000 = 4000;

    /**
     * 成功
     */
    public static final int STATUS_CODE_SUCCESS = 0;

    /**
     * 失败
     */
    public static final String STATUS_CODE_FAIL = "10001";

    public static final float SDK_LIVENESSSCORE = 1.33f;//活体分数 设置为1.33,95%通过率；1.74，96%通过率

    public static final float RGBLIVENESSTHRESHOLD = 1.42F;//SDK2.3.0建议设置分数 rgb活体分数阈值
    public static final float IRLIVENESSTHRESHOLD = 1.74F;//SDK2.3.0建议设置分数 ir活体分数阈值


    public static final boolean ISLOGSERVICEENABLE = true;//日志服务，默认关闭，如果开启，则需要手动修改


}
