package megvii.testfacepass.pa.utils;

public interface MqCabackXT {

    public void receivedMessagext(String msg);
    public void cancelMessagext(String tag);
}
