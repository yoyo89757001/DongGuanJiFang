package megvii.testfacepass.pa.beans;

public class UpdataFaceMessage {

    private long faceId;
    private String catchFaceImg;
    private String machineCode;
    private String ip;
    private String pushDate;
    private int visitorType;
    private String icCard;
    private int faceCompareResult;//1,#人脸比对结果:0失败,1成功
    private int cardCompareResult;//1,#刷卡结果:0失败,1成功

    public String getIcCard() {
        return icCard;
    }

    public void setIcCard(String icCard) {
        this.icCard = icCard;
    }

    public int getFaceCompareResult() {
        return faceCompareResult;
    }

    public void setFaceCompareResult(int faceCompareResult) {
        this.faceCompareResult = faceCompareResult;
    }

    public int getCardCompareResult() {
        return cardCompareResult;
    }

    public void setCardCompareResult(int cardCompareResult) {
        this.cardCompareResult = cardCompareResult;
    }

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

    public String getCatchFaceImg() {
        return catchFaceImg;
    }

    public void setCatchFaceImg(String catchFaceImg) {
        this.catchFaceImg = catchFaceImg;
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
