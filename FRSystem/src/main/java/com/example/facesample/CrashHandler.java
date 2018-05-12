package com.example.facesample;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.example.facesample.utils.AppHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Arrays;

public class CrashHandler implements UncaughtExceptionHandler{
    private static final String TAG = "CrashHandler";
    private final UncaughtExceptionHandler handler;

    public CrashHandler(){
        handler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        StringBuilder sBuf = new StringBuilder();

        String curTime = AppHelper.curTime();
        sBuf.append("time").append("=").append(curTime).append("\n");

        injectBuildConfig(sBuf);

        File file = new File(Environment.getExternalStorageDirectory(), "face_log");
        if(!file.exists() || file.isFile()) file.mkdir();

        File log = new File(file, curTime.replace(" ","_") + ".log");
        try {
            sBuf.append("----------------------------------------------------------\n");
            PrintStream ps = new PrintStream(new FileOutputStream(log));
            ps.print(sBuf.toString());
            throwable.printStackTrace(ps);
            Log.e(TAG, "uncaughtException: write log success");
        } catch (Exception e) {
            Log.e(TAG, "uncaughtException: write log fail");

        }

        if (handler != null) {
            handler.uncaughtException(thread, throwable);
        }else{
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private void injectBuildConfig(StringBuilder buffer){
        Field[] fields = Build.class.getDeclaredFields();

        String name = "";
        Object value = "";
        for (Field f: fields){
            name = f.getName();
            buffer.append(name).append("=");
            try {
                value = f.get(null);
                if(value instanceof String[]){
                    buffer.append(Arrays.toString((String[]) value));
                }else {
                    buffer.append(value.toString());
                }
            } catch (Exception e) {

            }
            buffer.append("\n");
        }
    }
}
