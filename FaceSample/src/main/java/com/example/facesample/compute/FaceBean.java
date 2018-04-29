package com.example.facesample.compute;

import com.arcsoft.facerecognition.AFR_FSDKFace;


public class FaceBean {

    public FaceBean(AFR_FSDKFace fsdkFace, String name){
        this.mface = fsdkFace;
        this.name = name;
    }

    private AFR_FSDKFace mface;

    private String name;


    public AFR_FSDKFace getMface() {
        return mface;
    }

    public void setMface(AFR_FSDKFace mface) {
        this.mface = mface;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
