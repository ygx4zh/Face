package com.example.facesample;

import android.app.Application;
import android.content.Context;

import com.example.facesample.utils.SpConfig;
import com.vondear.rxtools.RxTool;
import com.vondear.rxtools.view.dialog.RxDialog;


public class App extends Application {
    private static Context sCtx;
    @Override
    public void onCreate() {
        super.onCreate();
        sCtx = this;
        RxTool.init(this);
        SpConfig.init(this);
    }

    public static Context getCtx(){
        return sCtx;
    }
}
