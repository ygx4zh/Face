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
    public static<T> void scanSDCard(
                                     final Function<File,T> fun,
                                     final ImgSubscriber<T> subscriber) {
        subscriber.onScanStart();
        AppHelper.run(new Runnable() {
            @Override
            public void run() {
                int frequency = subscriber.getFrequency();
                File parentalPath = subscriber.getParentalPath();

                ArrayList<T> fss = new ArrayList<>();

                traverse(parentalPath, fss, frequency, subscriber, fun);
                if (frequency <= 0) {
                    subscriber.onScanCompleted(fss);
                } else if (frequency > 0 && fss.size() > 0) {
                    subscriber.onScanProgress(fss);
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        subscriber.onScanEnd();
                    }
                });
            }
        });


    }

    private static<T> void traverse(File file, List<T> fss,
                                    int fraquency,
                                    ImgSubscriber<T> subscriber,
                                    Function<File, T> fun)
    {
        File[] files = file.listFiles();
        Log.e(TAG, "traverse: "+file.getAbsolutePath());
        for (File f : files) {
            if (f.isDirectory()) {
                traverse(f, fss, fraquency, subscriber, fun);
            } else {
                T t = fun.applyAs(f);
                if(t != null){
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
