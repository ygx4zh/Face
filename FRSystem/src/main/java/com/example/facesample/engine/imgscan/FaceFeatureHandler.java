package com.example.facesample.engine.imgscan;

/**
 * 人脸特征码处理者
 *
 * @param <Returner> 处理后的返回值
 */
public interface FaceFeatureHandler<Returner> {

    Returner handle(byte[] face_feature);
}
