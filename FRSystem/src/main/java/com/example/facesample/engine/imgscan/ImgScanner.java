package com.example.facesample.engine.imgscan;


import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.example.facesample.bean.ImgBean;
import com.example.facesample.db.bean.FaceImageBean;
import com.example.facesample.utils.AppHelper;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImgScanner {
    private static final String TAG = "ImgScanner";
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * 扫描sd卡
     * @param filter 过滤器, 将文件转成T 对象的过滤器
     * @param subscriber 订阅者, 结果和扫描过程的回调者
     * @param <T> 结果类型
     */
    public static<T> void scanSDCard(
                                     final Function<File,T> filter,
                                     final ImgSubscriber<T> subscriber) {
        subscriber.onScanStart();

        // 扫描sd文件目录是耗时任务, 因此通过线程池来操作
        AppHelper.run(new Runnable() {
            @Override
            public void run() {

                int frequency = subscriber.getFrequency();

                // 获取要扫描的目标文件夹
                File parentalPath = subscriber.getParentalPath();

                // 创建一个用于装扫描结果的集合;
                ArrayList<T> fss = new ArrayList<>();

                // 开始遍历指定路径的文件夹
                traverse(parentalPath, fss, frequency, subscriber, filter);

                // 扫描结束后, 回调扫描结果
                if (frequency <= 0) {
                    subscriber.onScanCompleted(fss);
                } else if (fss.size() > 0) {
                    subscriber.onScanProgress(fss);
                }

                // 回调显示扫描结束了, 可以执行对应的操作, 例如刷新显示数据
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        subscriber.onScanEnd();
                    }
                });
            }
        });


    }

    /**
     * 遍历文件夹
     * @param file 被遍历的文件夹
     * @param fss 装扫描数据的集合
     * @param fraquency
     * @param subscriber 订阅者
     * @param fun 将文件转成T的功能接口
     * @param <T> T表示期望扫描到的数据类型, 例如人脸数据, 这里是通过fun接口将一个文件转成一份对应的人脸数据
     */
    private static<T> void traverse(File file, List<T> fss,
                                    int fraquency,
                                    ImgSubscriber<T> subscriber,
                                    Function<File, T> fun)
    {
        // 获取文件夹下的子文件及子文件夹
        File[] files = file.listFiles();
        for (File f : files) {

            // 如果是文件夹, 则递归调用遍历该子文件夹
            if (f.isDirectory()) {
                traverse(f, fss, fraquency, subscriber, fun);
            } else {
                // 如果是文件, 则直接将文件对象传递给fun功能转换对象, 转换成对应的T数据,
                // 这里是将File对象传给ToImgFun对象, 转成FaceImgBean对象;
                T t = fun.applyAs(f);
                // 如果转出来的数据是null, 说明这个文件不是图片或图片未检出人脸
                if(t != null){
                    // 将人脸数据添加到集合,
                    fss.add(t);
                    if (fraquency > 0 && fss.size() == fraquency) {
                        subscriber.onScanProgress(fss);
                        fss.clear();
                    }
                }
            }
        }
    }
}
