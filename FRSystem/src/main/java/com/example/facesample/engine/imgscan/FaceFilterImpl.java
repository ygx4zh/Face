package com.example.facesample.engine.imgscan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.example.facesample.compute.FaceVerify;

import java.io.File;



public class FaceFilterImpl implements FaceFilter {
    @Override
    public byte[] applyAs(File p) {
        Bitmap bitmap = BitmapFactory.decodeFile(p.getAbsolutePath());
        AFR_FSDKFace afr_fsdkFace = FaceVerify.extraBitmapFeature(bitmap);

        if(afr_fsdkFace == null) return null;

        return afr_fsdkFace.getFeatureData();
    }
}
