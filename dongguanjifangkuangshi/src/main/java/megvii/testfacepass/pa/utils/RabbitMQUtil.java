package megvii.testfacepass.pa.utils;



import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

//TODO 引入
//TODO implementation 'com.rabbitmq:amqp-client:4.12.0'
//TODO implementation files('libs\\slf4j-simple-1.7.9.jar')

public class RabbitMQUtil {
   // private MqCaback mqCaback;
    private  Connection mConnection = null;
    private  Channel mChannelSend = null;
    private final static String QUEUE_NAME = "PQEB-"+(FileUtil.getSerialNumber() == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber());
    private final static String EXCHANG_NAME = "PEXB-"+(FileUtil.getSerialNumber() == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber());
    private final static String KEY_NAME = "PKB-"+(FileUtil.getSerialNumber() == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber());
    private TextView mqshow;
    private Activity activity;

    public RabbitMQUtil(TextView mqshow,Activity activity) {
        this.mqshow=mqshow;
        this.activity=activity;
    }


    public  void init(String host,int port,String username,String password) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setPort(port);
            factory.setUsername(username);
            factory.setPassword(password);
            factory.setAutomaticRecoveryEnabled(true);
            factory.setNetworkRecoveryInterval(5000);
            mConnection = factory.newConnection();
            EventBus.getDefault().post(mConnection);
          //  mChannelSend = mConnection.createChannel();
          //  mChannelSend.exchangeDeclare(QUEUE_NAME, "direct");
          //  Callback();
        } catch (Exception e) {
            e.printStackTrace();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mqshow.setText("创建MQ失败");
                    mqshow.setVisibility(View.VISIBLE);
                }
            });
            Log.d("RabbitMQUtil", e.getMessage()+"创建MQ异常");
        }
    }


    public void sendAsyncMessage(String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mChannelSend.basicPublish(QUEUE_NAME, KEY_NAME, null, message.getBytes(StandardCharsets.UTF_8));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void Callback(){
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
           Log.d("RabbitMQUtil", "收到业务消息:"+message);
          //  mqCaback.receivedMessage(message);
        };
        try {
            /* 3.消费者关联队列 */
            mChannelSend.queueDeclare(QUEUE_NAME, true, false, false, null);
            /* 4.消费者绑定交换机 参数1 队列 参数2交换机 参数3 routingKey */
            mChannelSend.queueBind(QUEUE_NAME, EXCHANG_NAME, KEY_NAME);
            mChannelSend.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
                Log.d("RabbitMQUtil", "取消业务消息Tag"+consumerTag);
              //  mqCaback.cancelMessage(consumerTag);
            });
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("RabbitMQUtil", e.getMessage()+"接收异常");
        }
    }

    public  void close() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mChannelSend != null && mChannelSend.isOpen()) {
                    try {
                        mChannelSend.close();
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
}
