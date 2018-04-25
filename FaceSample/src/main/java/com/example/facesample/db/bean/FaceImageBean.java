package com.example.facesample.db.bean;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

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

    @Id
    private String face_token;
    private String name;
    private String path;
    private String parentalPath;

    /**
     * 图片类型
     *
     * @see #DEFAULT
     * @see #SINGLE_FACE
     * @see #MULTI_FACE
     */
    private int type;

    @Generated(hash = 1582435062)
    public FaceImageBean(String face_token, String name, String path,
            String parentalPath, int type) {
        this.face_token = face_token;
        this.name = name;
        this.path = path;
        this.parentalPath = parentalPath;
        this.type = type;
    }

    @Generated(hash = 1365577280)
    public FaceImageBean() {
    }

    public String getFace_token() {
        return this.face_token;
    }

    public void setFace_token(String face_token) {
        this.face_token = face_token;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParentalPath() {
        return this.parentalPath;
    }

    public void setParentalPath(String parentalPath) {
        this.parentalPath = parentalPath;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
