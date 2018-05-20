package com.example.facesample.engine.imgscan;

import android.text.TextUtils;
import android.util.Log;

import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.example.facesample.compute.FaceVerify;
import com.example.facesample.db.DBManager;
import com.example.facesample.db.bean.FaceImageBean;
import com.example.facesample.utils.AppHelper;

import org.json.JSONObject;

import java.io.File;


public class ToImgFun implements Function<File, FaceImageBean> {
    private static final String TAG = "ToImgFun";

    /**
     * 可以从人脸提取特征码的对象
     */
    private Face2FeatureWrapper transer = new Face2FeatureWrapper();

    /**
     * 将人脸特征码保存到本地, 并返回一个保存人脸特征文件的路径
     */
    private FaceFeatureHandler<String> sFaceHandler
            = new FaceFeature2FilePathHandler();
    // private FaceFilterImpl faceFilter = new FaceFilterImpl();



    @Override
    public FaceImageBean applyAs(File f) {
        String image_suffix = isImage(f);
        if(TextUtils.isEmpty(image_suffix)) return null;

       /* long start = System.currentTimeMillis();
        Boolean hasFace = faceFilter.filte(f);
        long end = System.currentTimeMillis();
        Log.e(TAG, "applyAs: filte face "+(end-start)+" // "+hasFace+" // "+f.getAbsolutePath());
        if(!hasFace) return null;

        */

        // 提取人脸特征码
        AFR_FSDKFace afr_fsdkFace = transer.applyAs(f);

        // 如果特征码为null, 表示这张图不包含人脸
        if(afr_fsdkFace == null){
            Log.e(TAG, "applyAs: is null "+f.getAbsolutePath());
            return null;
        }

        // 处理特征码
        FaceImageBean faceImageBean = new FaceImageBean();

        // 调用方法, 将人脸特征码保存到指定文件夹, 并返回所保存的文件的路径
        String face_feature = sFaceHandler.handle(afr_fsdkFace.getFeatureData());
        // 将人脸特征保存的路径设置给人脸数据对象
        faceImageBean.setFace_feature(face_feature);

        // 通过人脸特征码, 从数据库搜索看有无相似图片, 返回值为相似图片的face_token
        String faceToken = DBManager.searchFace(afr_fsdkFace);

        // 如果未搜索到相似图片(face_token未空则表示未搜到)
        if(TextUtils.isEmpty(faceToken)){
            // 创建一个face_token;
            // 注意, face_token只是一个长度为30的随机字符串, 它并不是根据图片转换而来;
            // 它是用于设置给人脸, 标记着一组相似图片的token;
            // face_token相同的人脸, 是一组相似face, 这样可以在对比的时候加快对比速度
            // 例如: 有一组具有相同face_token的人脸数据, 在进行人脸对比的时候, 只要这组人脸中任一张图片匹配上了,
            // 就说明对比的人脸都能匹配上这一组人脸, 这一就不需要一张一张图片的再去比对特征码, 提高了比对效果
            faceToken = AppHelper.createFaceToken();
        }

        faceImageBean.setFace_token(faceToken);

        faceImageBean.setFname(f.getName());
        faceImageBean.setPath(f.getAbsolutePath());
        faceImageBean.setType(afr_fsdkFace.getFeatureData() == null ? FaceImageBean.DEFAULT: FaceImageBean.SINGLE_FACE);

        String path = f.getAbsolutePath();
        String replace = path.replace("."+image_suffix, ".txt");

        // 读取对应的附加资料文件,
        // 如果一张图片 ldh_1.png 有资料文件ldh_1.txt, 就能读取到图片的资料, 否则默认资料为空;
        JSONObject jsonObject = ImgHelper.readExtra(replace);
        faceImageBean.setExtra(jsonObject.toString());

        return faceImageBean;
    }

    private static String isImage(File file) {
        if (file == null) return "";

        String name = file.getName();
        String[] split = name.split("\\.");
        if (split.length < 2) return "";

        String suffix = split[split.length - 1];
        if (TextUtils.isEmpty(name)) {
            return "";
        }

        switch (suffix.toLowerCase()) {
            case "jpeg":
            case "jpg":
            case "png":
                return suffix;
            default:
                return "";
        }
    }
}
