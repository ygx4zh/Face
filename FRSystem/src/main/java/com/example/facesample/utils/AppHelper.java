package com.example.facesample.utils;


import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.view.Surface;
import android.view.WindowManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppHelper {

    private static ExecutorService pool = Executors.newCachedThreadPool();

    public static void run(Runnable r){
        if (r == null) {
            return;
        }

        pool.execute(r);
    }

    public static int dp2px(Context ctx, float dpValue) {

        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public static int getCameraDisplayRotation(Context ctx,int cameraId) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);

        int rotation = wm.getDefaultDisplay().getRotation();

        int degree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;
        }
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return (720 - (cameraInfo.orientation + degree)) % 360;
        } else {
            return (360 - degree + cameraInfo.orientation) % 360;
        }
    }

    private static Point outSize = new Point();
    public static Point getScreenSize(Context ctx){
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);

        wm.getDefaultDisplay().getSize(outSize);
        return outSize;
    }

    public static int getSupportCameras(){
        return Camera.getNumberOfCameras();
    }

    private static Random sRandom;
    public static Random random(){
        if(sRandom == null){
            sRandom = new Random();
        }
        return sRandom;
    }

    public static String createRandomString(int len){
        if(len < 0 ) throw new IllegalArgumentException("string length can't less than 0");

        if(len == 0) return "";

        boolean upperCase;
        char start_c;
        StringBuilder sBuf = new StringBuilder();
        for (int i = 0; i < len; i++) {
            upperCase = random().nextBoolean();
            start_c = upperCase ? 'A':'a';
            start_c += random().nextInt(26);
            sBuf.append(start_c);
        }

        return sBuf.toString();
    }


    public static String createFaceToken(){
        return createRandomString(32);
    }

    public static String curTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.CHINA);

        return sdf.format(new Date(System.currentTimeMillis()));
    }
}
