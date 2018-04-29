package com.example.facesample.db.bean;


import android.graphics.Bitmap;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

/**
 * 人脸图片对象
 *
 */
@Entity
public class FaceImageBean {

    /** 默认无人脸 */
    public static final int DEFAULT     = 0;

    /** 只有一张脸 */
    public static final int SINGLE_FACE = 1;

    /** 多张脸    */
    public static final int MULTI_FACE  = 2;

    /** 使用bitmap */
    public static final int FACE_BITMAP = 3;

    @Id
    private String fname;
    private String face_token;
    private String path;
    private String extra;

    @Transient
    private Bitmap mBmp;
    /**
     * 图片类型
     *
     * @see #DEFAULT
     * @see #SINGLE_FACE
     * @see #MULTI_FACE
     */
    private int type;

    @Generated(hash = 1568474043)
    public FaceImageBean(String fname, String face_token, String path, String extra,
            int type) {
        this.fname = fname;
        this.face_token = face_token;
        this.path = path;
        this.extra = extra;
        this.type = type;
    }

    @Generated(hash = 1365577280)
    public FaceImageBean() {
    }

    public Bitmap getmBmp() {
        return mBmp;
    }

    public void setmBmp(Bitmap mBmp) {
        this.mBmp = mBmp;
    }

    public String getFname() {
        return this.fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getFace_token() {
        return this.face_token;
    }

    public void setFace_token(String face_token) {
        this.face_token = face_token;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExtra() {
        return this.extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FaceImageBean{" +
                "fname='" + fname + '\'' +
                ", face_token='" + face_token + '\'' +
                ", path='" + path + '\'' +
                ", extra='" + extra + '\'' +
                ", type=" + type +
                '}';
    }
}
