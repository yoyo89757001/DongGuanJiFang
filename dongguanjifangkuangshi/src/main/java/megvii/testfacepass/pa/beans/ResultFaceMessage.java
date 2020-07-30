package megvii.testfacepass.pa.beans;

public class ResultFaceMessage {//发送执行结果
    private long faceId;
    private int checkinResult;
    private String machineCode;
    private String msg;
    private String ip;
    private String pushDate;
    private int visitorType;


    public int getVisitorType() {
        return visitorType;
    }

    public void setVisitorType(int visitorType) {
        this.visitorType = visitorType;
    }

    public long getFaceId() {
        return faceId;
    }

    public void setFaceId(long faceId) {
        this.faceId = faceId;
    }

    public int getCheckinResult() {
        return checkinResult;
    }

    public void setCheckinResult(int checkinResult) {
        this.checkinResult = checkinResult;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPushDate() {
        return pushDate;
    }

    public void setPushDate(String pushDate) {
        this.pushDate = pushDate;
    }
}
