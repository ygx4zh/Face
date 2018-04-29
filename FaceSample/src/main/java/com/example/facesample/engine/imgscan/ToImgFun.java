package com.example.facesample.engine.imgscan;

import android.text.TextUtils;
import android.util.Log;

import com.example.facesample.bean.ImgBean;
import com.example.facesample.db.bean.FaceImageBean;

import org.json.JSONObject;

import java.io.File;


public class ToImgFun implements Function<File, FaceImageBean> {
    private static final String TAG = "ToImgFun";

    private FaceFilter filter = new FaceFilterImpl();
    private FaceFeatureHandler<String> sFaceHandler
            = new FaceFeature2FilePathHandler();

    @Override
    public FaceImageBean applyAs(File f) {
        String image_suffix = isImage(f);
        if(TextUtils.isEmpty(image_suffix)) return null;

        // 人脸特征码
        byte[] bytes = filter.applyAs(f);

        // 处理特征码
        FaceImageBean faceImageBean = new FaceImageBean();
        faceImageBean.setFace_token(bytes == null ?
                "" : sFaceHandler.handle(bytes));

        faceImageBean.setFname(f.getName());
        faceImageBean.setPath(f.getAbsolutePath());
        faceImageBean.setType(bytes == null ? FaceImageBean.DEFAULT: FaceImageBean.SINGLE_FACE);

        String path = f.getAbsolutePath();
        String replace = path.replace("."+image_suffix, ".txt");
        Log.e(TAG, "applyAs: "+replace);
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
