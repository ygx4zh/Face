package com.example.facesample.engine.imgscan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.example.facesample.compute.FaceVerify;

import java.io.File;

public class Face2FeatureWrapper implements Function<File,AFR_FSDKFace> {
    @Override
    public AFR_FSDKFace applyAs(File p) {
        Bitmap bitmap = BitmapFactory.decodeFile(p.getAbsolutePath());

        return FaceVerify.extraBitmapFeature(bitmap);
    }

}
