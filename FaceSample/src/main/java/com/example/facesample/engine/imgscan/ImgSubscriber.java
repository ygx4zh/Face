package com.example.facesample.engine.imgscan;


import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

import java.io.File;
import java.util.List;

public abstract class ImgSubscriber {

    private File path;

    private int frequency;

    public ImgSubscriber(File parentalPath){
        this(parentalPath, -1);
    }

    public ImgSubscriber(File parentalPath, int frequency){
        this.path = parentalPath;
        this.frequency = frequency;
    }

    public File getParentalPath(){
        return this.path;
    }

    public int getFrequency() {
        return frequency;
    }

    /**
     * 开始扫描时
     */
    @MainThread
    public abstract void onScanStart();

    /**
     * 扫描过程中上报文件数据
     * @param files 扫描到的文件对象
     *
     * 注意: 当{@link #frequency} <=0 时, 这个方法不会被调用, 只会执行{@link #onScanCompleted(List)}
     */
    @WorkerThread
    public void onScanProgress(List<File> files){}

    /**
     * 扫描完成后一次性上报文件
     * @param files 扫描到的文件对象
     *
     * 注意: 当{@link #frequency} > 0 时, 这个方法不会被调用, 只会执行{@link #onScanProgress(List)}
     */
    @WorkerThread
    public void onScanCompleted(List<File> files){}

    /**
     * 扫描结束
     */
    @MainThread
    public abstract void onScanEnd();
}
