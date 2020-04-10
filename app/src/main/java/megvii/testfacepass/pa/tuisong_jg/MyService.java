package megvii.testfacepass.pa.tuisong_jg;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.pingan.ai.access.common.PaAccessControlMessage;
import com.pingan.ai.access.entiry.PaAccessFaceInfo;
import com.pingan.ai.access.manager.PaAccessControl;
import com.pingan.ai.access.result.PaAccessDetectFaceResult;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.PutMapping;
import com.yanzhenjie.andserver.annotation.QueryParam;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.http.multipart.MultipartFile;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.objectbox.Box;
import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.beans.ResBean;
import megvii.testfacepass.pa.beans.Subject;
import megvii.testfacepass.pa.beans.Subject_;
import megvii.testfacepass.pa.utils.BitmapUtil;
import megvii.testfacepass.pa.utils.FileUtil;
import megvii.testfacepass.pa.utils.GsonUtil;


@RestController
@RequestMapping(path = "/app")
public class MyService {

    private Box<Subject> subjectBox  = MyApplication.myApplication.getSubjectBox();;
    private PaAccessControl paAccessControl=PaAccessControl.getInstance();
    private  String serialnumber=FileUtil.getSerialNumber(MyApplication.myApplication) == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber(MyApplication.myApplication);

    @PostMapping("/deleteFacee")
    public String deleteFacee(
            @RequestParam(name = "id") String id) {
        if (paAccessControl==null)
            return requsBean(-1,"识别算法未初始化");
        paAccessControl.stopFrameDetect();
        try {
            PaAccessFaceInfo face = paAccessControl.queryFaceById(id);
            List<Subject> subject = subjectBox.query().equal(Subject_.teZhengMa, id).build().find();
            for (Subject subject1: subject){
                File file =new File(MyApplication.SDPATH3,subject1.getTeZhengMa()+".png");
                Log.d("MyService", "file删除():" + file.delete());
                subjectBox.remove(subject);
            }
            if (face != null) {
                paAccessControl.deleteFaceById(face.faceId);
                paAccessControl.startFrameDetect();
                return requsBean(0,"删除成功");
            }else {
                paAccessControl.startFrameDetect();
                return requsBean(-1,"未找到改人员");
            }

          }catch (Exception e){
            e.printStackTrace();
            paAccessControl.startFrameDetect();
            return requsBean(-1,e+"");
        }
    }

    //        getName()，获取该文件的key，也就是表单中的name
//        getFilename()，获取该文件名称，可能为空
//        getContentType()，获取该文件的内容类型
//        isEmpty()，判断该文件是否是非空的
//        getSize()，获取文件大小
//        getBytes()，获取文件的byte数组，不推荐使用
//        getStream()，获取该文件的输入流
//        transferTo(File)，转移该文件到目标位置

    //新增人员
    @PostMapping("/addFace")
    public String addFace(@RequestParam(name = "id") String id,
                         @RequestParam(name = "name") String name,
                         @RequestParam(name = "number") String kahao,
                         @RequestParam(name = "machineStatus") String isOpen,
                         @RequestParam(name = "personType")String leixing,
                          @RequestParam(name = "endTime")String time,
                         @RequestParam(name = "image") MultipartFile file
                ) throws IOException {
        if (paAccessControl==null)
            return requsBean(-1,"识别算法未初始化");

        paAccessControl.stopFrameDetect();
       Bitmap bitmap=readInputStreamToBitmap(file.getStream(),file.getSize());

        PaAccessDetectFaceResult detectResult = paAccessControl.detectFaceByBitmap(bitmap);

        if (detectResult!=null && detectResult.message== PaAccessControlMessage.RESULT_OK) {
            BitmapUtil.saveBitmapToSD(bitmap, MyApplication.SDPATH3, id + ".png");
            //先查询有没有
            try {
                List<Subject> subjects = subjectBox.query().equal(Subject_.teZhengMa, id).build().find();
                for (Subject subject:subjects){
                    if (subject!=null)
                        subjectBox.remove(subject);
                }
                PaAccessFaceInfo face = paAccessControl.queryFaceById(id);
                if (face != null) {
                    paAccessControl.deleteFaceById(face.faceId);
                }
                paAccessControl.addFace(id , detectResult.feature, MyApplication.GROUP_IMAGE);

                Subject subject = new Subject();
                subject.setTeZhengMa(id); //人员id==图片id
                subject.setId(System.currentTimeMillis());
                subject.setPeopleType(leixing);
//                subject.setBirthday(renShu.getBirthday());
                subject.setName(name);
                subject.setIsOpen(Integer.parseInt(isOpen));
                subject.setBirthday(time);
//                subject.setEntryTime(renShu.getEntryTime());
//                subject.setPhone(renShu.getPhone());
//                subject.setEmail(renShu.getEmail());
//                subject.setRemark(renShu.getRemark());
//                subject.setPosition(renShu.getPosition());
                subject.setWorkNumber(kahao);
//                subject.setStoreId(renShu.getStoreId());
              //  subject.setStoreName(renShu.getStoreName());
               // subject.setCompanyId(renShu.getCompanyId());
                //subject.setDepartmentName(renShu.getDepartmentName());

                subjectBox.put(subject);
                Log.d("MyReceiver", "单个员工入库成功"+subject.toString());
                paAccessControl.startFrameDetect();
                return requsBean(0,"成功");
            } catch (Exception e) {
                e.printStackTrace();
                paAccessControl.startFrameDetect();
                return requsBean(-1,e+"");
            }
        }else {
            paAccessControl.startFrameDetect();
            return requsBean(-1,"质量检测失败");
        }
    }



    //获取后缀名
    private  String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }
    /*
     * Java文件操作 获取不带扩展名的文件名
     * */
    private   String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }


    //新增人员
    @PostMapping("/addBitmapBatch")
    public String addFaceBatch(@RequestParam(name = "bitmapZip") MultipartFile file
    ) throws IOException {
        if (paAccessControl==null)
            return requsBean(-1,"识别算法未初始化");
        paAccessControl.stopFrameDetect();
        if (file.getFilename()==null){
            return requsBean(-1,"文件名为空");
        }
        Log.d("MyService", MyApplication.SDPATH2 + File.separator + file.getFilename());

        file.transferTo(new File(MyApplication.SDPATH2 , file.getFilename()));
        ZipFile zipFile=null;
        List fileHeaderList=null;
        try {
            zipFile = new ZipFile(MyApplication.SDPATH2+File.separator+file.getFilename());
            zipFile.setFileNameCharset("GBK");
            fileHeaderList = zipFile.getFileHeaders();
            zipFile.setRunInThread(false); // true 在子线程中进行解压 false主线程中解压
            zipFile.extractAll(MyApplication.SDPATH2); // 将压缩文件解压到filePath中..
        } catch (ZipException e) {
            e.printStackTrace();
            return requsBean(-1,e.getMessage()+"");
        }
        if (fileHeaderList==null){
            return requsBean(-1,"解压文件失败");
        }
        Log.d("MyService", "fileHeaderList.size():" + fileHeaderList.size());
        int size=fileHeaderList.size();
        StringBuilder kaimen=new StringBuilder();
        for(int i = 0; i < size; i++) {
            FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
           // Log.d("MyService", MyApplication.SDPATH2 + File.separator + fileHeader.getFileName() +" 图片路径");
            Bitmap bitmap=BitmapFactory.decodeFile(MyApplication.SDPATH2+File.separator+fileHeader.getFileName());

          //  Log.d("MyService", "bitmap.getWidth():" + bitmap.getWidth());
            PaAccessDetectFaceResult detectResult = paAccessControl.detectFaceByBitmap(bitmap);
            String id = getFileNameNoEx(fileHeader.getFileName());
            if (detectResult!=null && detectResult.message== PaAccessControlMessage.RESULT_OK) {
                //先查询有没有
                try {
                   Subject subject= subjectBox.query().equal(Subject_.teZhengMa,id).build().findFirst();
                    paAccessControl.addFace(id , detectResult.feature, MyApplication.GROUP_IMAGE);
                   if (subject==null){
//                       Subject subject2 = new Subject();
//                       subject2.setTeZhengMa(id); //人员id==图片id
//                       subject2.setId(System.currentTimeMillis());
//                       subjectBox.put(subject2);
//                       Log.d("MyService", "单个员工入库成功"+subject2.toString());
                       kaimen.append(id);
                       kaimen.append(",");
                   }else {
                       Log.d("MyService", "已经在库中"+subject.toString());
                   }
//                    PaAccessFaceInfo face = paAccessControl.queryFaceById(id);
//                    if (face != null) {
//                        paAccessControl.deleteFaceById(face.faceId);
//                        Subject subject = subjectBox.query().equal(Subject_.teZhengMa, id).build().findUnique();
//                        if (subject!=null)
//                            subjectBox.remove(subject);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                   // paAccessControl.startFrameDetect();
                    kaimen.append(id);
                    kaimen.append(",");
                  //  return requsBean(-1,e+"");
                }
            }else {
                //paAccessControl.startFrameDetect();
                kaimen.append(id);
                kaimen.append(",");
               // return requsBean(-1,"质量检测失败");
            }
        }
        paAccessControl.startFrameDetect();
        if (kaimen.length()>0){
            Log.d("MyService", kaimen.toString());
            kaimen.delete(kaimen.length()-1,kaimen.length());
            return requsBean(0,kaimen.toString());
        }else {
            return requsBean(0,"成功");
        }
    }

    //修改是否开门
    @PostMapping("/updateIsOpen")
    public String upload(@RequestParam(name = "dates") String dates
                         ) throws IOException {
        if (paAccessControl==null)
            return requsBean(-1,"识别算法未初始化");
        if (dates==null || dates.equals(""))
            return requsBean(-1,"数据为空");
           paAccessControl.stopFrameDetect();
                try {
                    Log.d("MyService", dates);
                    JsonArray jsonArray= GsonUtil.parse(dates).getAsJsonArray();
                    // Gson gson=new Gson();
                    //  final PiLiangJSON renShu=gson.fromJson(jsonArray,PiLiangJSON.class);
                    StringBuilder kaimen=new StringBuilder();
                    for (JsonElement object : jsonArray){
                        String id = object.getAsJsonObject().get("id").getAsString();
                        int isOpen = object.getAsJsonObject().get("machineStatus").getAsInt();
                        Subject subject = subjectBox.query().equal(Subject_.teZhengMa, id).build().findUnique();
                        if (subject!=null){
                            subject.setIsOpen(isOpen);
                            subjectBox.put(subject);
                        }else {
                            kaimen.append(id);
                            kaimen.append(",");
                        }
                    }
                    paAccessControl.startFrameDetect();
                    if (kaimen.length()>0){
                        Log.d("MyService", kaimen.toString());
                        kaimen.delete(kaimen.length()-1,kaimen.length());
                        return requsBean(0,kaimen.toString());
                    }else {
                        return requsBean(0,"成功");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    paAccessControl.startFrameDetect();
                    return requsBean(-1,e.getMessage());
                }

    }


    @GetMapping("/getOnline")
    public String online(){
        return requsBean(1,"true");
    }

    @PostMapping("/batchAddFace")
    public String batchAddFace(
            @RequestParam(name = "person") String person) {
        if (paAccessControl==null)
            return requsBean(-1,"识别算法未初始化");
        try {
            if (person==null || person.equals(""))
                return requsBean(-1,"数据为空");
            Log.d("MyService", person);
            JsonArray jsonArray= GsonUtil.parse(person).getAsJsonArray();
           // Gson gson=new Gson();
          //  final PiLiangJSON renShu=gson.fromJson(jsonArray,PiLiangJSON.class);
            for (JsonElement object : jsonArray){
                String name = object.getAsJsonObject().get("name").getAsString();
                String id = object.getAsJsonObject().get("id").getAsString();
                String personType = object.getAsJsonObject().get("personType").getAsString();
                String ic_no = object.getAsJsonObject().get("ic_no").getAsString();
                Subject subject = new Subject();
                subject.setPeopleType(personType);
                subject.setId(System.currentTimeMillis());
                subject.setTeZhengMa(id);
                subject.setIsOpen(1);
                subject.setName(name);
                subject.setWorkNumber(ic_no);
                subjectBox.put(subject);
                Log.d("MyService", "subjectBox.getAll().size():" + subjectBox.getAll().size());
            }
            return requsBean(0,"成功");
        }catch (Exception e){
            e.printStackTrace();
            paAccessControl.startFrameDetect();
            return requsBean(-1,e+"");
        }
    }



    private String requsBean(int code,String msg){

        return JSON.toJSONString(new ResBean(code,msg,serialnumber));
    }

    private  Bitmap readInputStreamToBitmap(InputStream ins, long fileSize) {
        if (ins == null) {
            return null;
        }
        byte[] b;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int size = -1;
            int len = 0;// 已经接收长度
            size = ins.read(buffer);
            while (size != -1) {
                len = len + size;//
                bos.write(buffer, 0, size);
                if (fileSize == len) {// 接收完毕
                    break;
                }
                size = ins.read(buffer);
            }
            b = bos.toByteArray();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return null;
    }
}
