package megvii.testfacepass.pa.beans;

public class XinTiao {

    private String machineIp;
    private String machineCode;
    private String pushDate;
    private int machineStatus;


    public String getPushDate() {
        return pushDate;
    }

    public void setPushDate(String pushDate) {
        this.pushDate = pushDate;
    }

    public String getMachineIp() {
        return machineIp;
    }

    public void setMachineIp(String machineIp) {
        this.machineIp = machineIp;
    }

    public int getMachineStatus() {
        return machineStatus;
    }

    public void setMachineStatus(int machineStatus) {
        this.machineStatus = machineStatus;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

}
