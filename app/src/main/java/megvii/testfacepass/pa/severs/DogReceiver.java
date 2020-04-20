package megvii.testfacepass.pa.severs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import megvii.testfacepass.pa.utils.ApiCode;


public class DogReceiver extends BroadcastReceiver {
    private ReceiveListener receiveListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 判断是否为sendbroadcast发送的广播
        if (ApiCode.BROADCAST_DOG2GUARD.equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Long timestamp = bundle.getLong("timestamp");
                int dataState = bundle.getInt("dataState");
                Log.e("broadcast广播", "DogReceiver=" + timestamp + ",dataState=" + dataState);
                receiveListener.setReceiveData(timestamp, dataState);
            }
        }
    }

    public interface ReceiveListener {
        void setReceiveData(Long timestamp, int dataState);
    }

    public void setReceiveListener(ReceiveListener listener) {
        this.receiveListener = listener;
    }

}
