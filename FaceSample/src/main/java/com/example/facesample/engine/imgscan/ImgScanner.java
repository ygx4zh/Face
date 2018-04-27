package com.example.facesample.engine.imgscan;


import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.example.facesample.AppHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImgScanner {
    private static final String TAG = "ImgScanner";
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    public static<T> void scanSDCard(final ImgSubscriber<T> subscriber) {
        subscriber.onScanStart();
        AppHelper.run(new Runnable() {
            @Override
            public void run() {
                int frequency = subscriber.getFrequency();
                File parentalPath = subscriber.getParentalPath();

                ArrayList<T> fss = new ArrayList<>();

                traverse(parentalPath, fss, frequency, subscriber);
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

    private static<T> void traverse(File file, List<T> fss, int fraquency, ImgSubscriber<T> subscriber) {
        File[] files = file.listFiles();
        Log.e(TAG, "traverse: "+file.getAbsolutePath());
        for (File f : files) {

            if (f.isDirectory()) {
                traverse(f, fss, fraquency, subscriber);
            } else {
                if (isImage(f)) {
                    fss.add(subscriber.getFun().applyAs(f));
                    String name = f.getName();
                    Log.e(TAG, "traverse: "+name);
                    if (fraquency > 0 && fss.size() == fraquency) {
                        subscriber.onScanProgress(fss);
                        fss.clear();
                    }
                }
            }
        }
    }

    private static boolean isImage(File file) {
        if (file == null) return false;

        String name = file.getName();
        String[] split = name.split("\\.");
        if (split.length < 2) return false;

        String suffix = split[split.length - 1];
        if (TextUtils.isEmpty(name)) {
            return false;
        }

        switch (suffix.toLowerCase()) {
            case "jpeg":
            case "jpg":
            case "png":
                return true;
            default:
                return false;
        }
    }
}
