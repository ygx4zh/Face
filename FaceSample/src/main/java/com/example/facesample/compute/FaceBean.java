package com.example.facesample.compute;

import com.arcsoft.facerecognition.AFR_FSDKFace;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class FaceBean {

    public FaceBean(AFR_FSDKFace fsdkFace, String name) {
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

    public static FaceBean decodeFile(String path) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        File file = new File(path);
        InputStream is = null;
        FaceBean bean = null;
        try {
            is = new FileInputStream(file);
            int len = -1;
            byte[] buf = new byte[1024];
            while ((len = is.read(buf)) != -1) {
                baos.write(buf);
                baos.flush();
            }
            bean = new FaceBean(new AFR_FSDKFace(baos.toByteArray()),file.getName());
        } catch (Exception e) {
        }finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
        return bean;
    }
}
