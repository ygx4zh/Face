package com.example.facesample.bean;


import java.io.File;

public class ImgBean {

    private File imgFile;
    private String title;

    public ImgBean(File imgFile, String title) {
        this.imgFile = imgFile;
        this.title = title;
    }

    public File getImgFile() {
        return imgFile;
    }

    public void setImgFile(File imgFile) {
        this.imgFile = imgFile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
