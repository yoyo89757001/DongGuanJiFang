package megvii.testfacepass.pa.beans;

public class UpdataFaceMessage {

    private long faceId;
    private String catchFaceImg;
    private String compareResult;
    private String machineCode;
    private String ip;
    private String pushDate;


    public long getFaceId() {
        return faceId;
    }

    public void setFaceId(long faceId) {
        this.faceId = faceId;
    }

    public String getCatchFaceImg() {
        return catchFaceImg;
    }

    public void setCatchFaceImg(String catchFaceImg) {
        this.catchFaceImg = catchFaceImg;
    }

    public String getCompareResult() {
        return compareResult;
    }

    public void setCompareResult(String compareResult) {
        this.compareResult = compareResult;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
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
