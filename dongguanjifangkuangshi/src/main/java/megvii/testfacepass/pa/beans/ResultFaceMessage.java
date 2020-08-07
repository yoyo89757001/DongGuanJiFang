package megvii.testfacepass.pa.beans;

public class ResultFaceMessage {//发送执行结果
    private String faceId;
    private String machineCode;
    private String ip;
    private String pushDate;//#推送时间
    private int visitorType;//人员类型
    private String batchCode;//批次
    private int businessType;//业务类型
    private int machineStatus;//,#设备状态:0异常 1正常
    private int result;//#入库结果:0失败 1成功
    private String icCard;//#实际刷的卡号
    private String catchFaceImg;
    private int faceCompareResult;
    private int cardCompareResult;
    private String remainingMemory;
    private int allPeopleSize;

    public String getRemainingMemory() {
        return remainingMemory;
    }

    public void setRemainingMemory(String remainingMemory) {
        this.remainingMemory = remainingMemory;
    }

    public int getAllPeopleSize() {
        return allPeopleSize;
    }

    public void setAllPeopleSize(int allPeopleSize) {
        this.allPeopleSize = allPeopleSize;
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

    public String getCatchFaceImg() {
        return catchFaceImg;
    }

    public void setCatchFaceImg(String catchFaceImg) {
        this.catchFaceImg = catchFaceImg;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
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

    public int getVisitorType() {
        return visitorType;
    }

    public void setVisitorType(int visitorType) {
        this.visitorType = visitorType;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public int getBusinessType() {
        return businessType;
    }

    public void setBusinessType(int businessType) {
        this.businessType = businessType;
    }

    public int getMachineStatus() {
        return machineStatus;
    }

    public void setMachineStatus(int machineStatus) {
        this.machineStatus = machineStatus;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getIcCard() {
        return icCard;
    }

    public void setIcCard(String icCard) {
        this.icCard = icCard;
    }


}
