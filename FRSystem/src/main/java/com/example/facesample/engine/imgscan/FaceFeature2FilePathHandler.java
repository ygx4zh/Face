package com.example.facesample.engine.imgscan;

import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 将人脸特征码转为本地文件的处理者
 *
 * 返回值是转换后文件的路径
 */
public class FaceFeature2FilePathHandler implements FaceFeatureHandler<String> {

    private File face_features;

    public FaceFeature2FilePathHandler(){
        face_features = new File(Environment.getExternalStorageDirectory(), "face_features");
        if(!face_features.exists()){
            face_features.mkdir();
        }
    }

    @Override
    public String handle(byte[] face_feature) {
        File file = new File(face_features, System.currentTimeMillis() + "_feature");
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(face_feature);
            os.flush();
        } catch (IOException e) {
            return null;
        }finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ignored) {
                }
            }
        }
        return file.getAbsolutePath();
    }
}
