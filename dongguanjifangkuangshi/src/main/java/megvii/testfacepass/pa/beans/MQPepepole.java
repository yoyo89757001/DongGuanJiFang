package megvii.testfacepass.pa.beans;

public class MQPepepole {


    /**
     * visitorImg : http://148.70.42.163:9001/access-control-visitor/2020-07-28/1595923669280.jpg
     * instructions : 1
     * icCard : 52504312
     * visitorName : 谢教授
     * visitorId : 19
     * authStartDate : 2020-07-28 20:20:20
     * authEndDate : 2020-07-30 22:22:22
     * machineCode : 2905A98031700891
     * pushDate : 2020-07-28 19:35:35
     */

    private String visitorImg;
    private int instructions;
    private String icCard;
    private String visitorName;
    private String visitorId;
    private String authStartDate;
    private String authEndDate;
    private String machineCode;
    private String pushDate;

    public String getVisitorImg() {
        return visitorImg;
    }

    public void setVisitorImg(String visitorImg) {
        this.visitorImg = visitorImg;
    }

    public int getInstructions() {
        return instructions;
    }

    public void setInstructions(int instructions) {
        this.instructions = instructions;
    }

    public String getIcCard() {
        return icCard;
    }

    public void setIcCard(String icCard) {
        this.icCard = icCard;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

    public String getAuthStartDate() {
        return authStartDate;
    }

    public void setAuthStartDate(String authStartDate) {
        this.authStartDate = authStartDate;
    }

    public String getAuthEndDate() {
        return authEndDate;
    }

    public void setAuthEndDate(String authEndDate) {
        this.authEndDate = authEndDate;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getPushDate() {
        return pushDate;
    }

    public void setPushDate(String pushDate) {
        this.pushDate = pushDate;
    }

    @Override
    public String toString() {
        return "MQPepepole{" +
                "visitorImg='" + visitorImg + '\'' +
                ", instructions=" + instructions +
                ", icCard='" + icCard + '\'' +
                ", visitorName='" + visitorName + '\'' +
                ", visitorId='" + visitorId + '\'' +
                ", authStartDate='" + authStartDate + '\'' +
                ", authEndDate='" + authEndDate + '\'' +
                ", machineCode='" + machineCode + '\'' +
                ", pushDate='" + pushDate + '\'' +
                '}';
    }
}
