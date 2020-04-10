//package megvii.testfacepass.pa.ui;
//
//import android.app.Activity;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.nfc.NfcAdapter;
//import android.nfc.NfcManager;
//import android.nfc.Tag;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.Gravity;
//import android.widget.Toast;
//
//import com.sdsmdg.tastytoast.TastyToast;
//
//import megvii.testfacepass.pa.R;
//
//
//public class NFCActivity extends Activity {
//    private NfcAdapter mNfcAdapter;
//    private PendingIntent mPendingIntent;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_nfc2);
//
//        NfcManager mNfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
//        mNfcAdapter = mNfcManager.getDefaultAdapter();
//        if (mNfcAdapter == null) {
//            Toast tastyToast = TastyToast.makeText(NFCActivity.this, "设备不支持NFC", TastyToast.LENGTH_LONG, TastyToast.INFO);
//            tastyToast.setGravity(Gravity.CENTER, 0, 0);
//            tastyToast.show();
//        } else if ((mNfcAdapter != null) && (!mNfcAdapter.isEnabled())) {
//            Toast tastyToast = TastyToast.makeText(NFCActivity.this, "请先去设置里面打开NFC开关", TastyToast.LENGTH_LONG, TastyToast.INFO);
//            tastyToast.setGravity(Gravity.CENTER, 0, 0);
//            tastyToast.show();
//        } else if ((mNfcAdapter != null) && (mNfcAdapter.isEnabled())) {
//
//        }
//        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()), 0);
//        init_NFC();
//    }
//
////    @Override
////    public void onNewIntent(Intent intent) {
////        //  super.onNewIntent(intent);
////        // Log.d("SheZhiActivity2", "intent:" + intent);
////        processIntent(intent);
////    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.d("SheZhiActivity2", "暂停");
//        if (mNfcAdapter != null) {
//            stopNFC_Listener();
//        }
//    }
//
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (mNfcAdapter != null) {
//            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
//            if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(this.getIntent().getAction())) {
//                processIntent(this.getIntent());
//            }
//        }
//    }
//
//    private void init_NFC() {
//        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
//        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
//    }
//
//    private void stopNFC_Listener() {
//        mNfcAdapter.disableForegroundDispatch(this);
//    }
//
//    public void processIntent(Intent intent) {
//        String data = null;
//        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//        String[] techList = tag.getTechList();
//        Log.d("SheZhiActivity2", "tag.describeContents():" + tag.describeContents());
//        byte[] ID;
//        data = tag.toString();
//        ID =  tag.getId();
//        data += "\n\nUID:\n" +byteToString(ID);
//        data += "\nData format:";
//        for (String tech : techList) {
//            data += "\n" + tech;
//        }
//        Log.d("SheZhiActivity2", byteToString(ID));
//        //   link_uplodeic(byteToString(ID));
//    }
//    /**
//     * 将byte数组转化为字符串
//     *
//     * @param src
//     * @return
//     */
//    public static String byteToString(byte[] src) {
//        StringBuilder stringBuilder = new StringBuilder();
//        if (src == null || src.length <= 0) {
//            return null;
//        }
//        char[] buffer = new char[2];
//        for (int i = 0; i < src.length; i++) {
//            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
//            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
//            System.out.println(buffer);
//            stringBuilder.append(buffer);
//        }
//        return stringBuilder.toString();
//    }
//
//}
