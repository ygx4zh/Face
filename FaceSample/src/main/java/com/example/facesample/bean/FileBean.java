package com.example.facesample.bean;


import java.io.File;

public class FileBean {

    private File file;
    private boolean selected;

    public FileBean(){}

    public FileBean(File file, boolean selected) {
        this.file = file;
        this.selected = selected;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
