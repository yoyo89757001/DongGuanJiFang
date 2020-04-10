package megvii.testfacepass.pa.beans;

import java.util.List;

public class ZhiLingBean {


    /**
     * result : [{"image":"http://192.168.2.183:8980/userfiles/fileupload/202004/1245264538174672896.jpg","instructions":"1","cardID":"12AE2712","jurisdiction":"0","name":"军总","id":"1245265752668950528","personType":"1","subjectId":"1245265746520100864"}]
     * code : 200
     * desc : 成功
     */

    private int code;
    private String desc;
    private List<ResultBean> result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * image : http://192.168.2.183:8980/userfiles/fileupload/202004/1245264538174672896.jpg
         * instructions : 1
         * cardID : 12AE2712
         * jurisdiction : 0
         * name : 军总
         * id : 1245265752668950528
         * personType : 1
         * subjectId : 1245265746520100864
         */

        private String image;
        private String instructions;
        private String cardID;
        private String jurisdiction;
        private String name;
        private String id;
        private String personType;
        private String subjectId;
        private String beginTime;
        private String endTime;

        public String getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(String beginTime) {
            this.beginTime = beginTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getInstructions() {
            return instructions;
        }

        public void setInstructions(String instructions) {
            this.instructions = instructions;
        }

        public String getCardID() {
            return cardID;
        }

        public void setCardID(String cardID) {
            this.cardID = cardID;
        }

        public String getJurisdiction() {
            return jurisdiction;
        }

        public void setJurisdiction(String jurisdiction) {
            this.jurisdiction = jurisdiction;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPersonType() {
            return personType;
        }

        public void setPersonType(String personType) {
            this.personType = personType;
        }

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }
    }
}
