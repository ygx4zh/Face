package com.example.facesample;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.facesample.db.greendao.DaoMaster;
import com.example.facesample.db.greendao.DaoSession;
import com.example.facesample.utils.SpConfig;
import com.vondear.rxtools.RxTool;
import com.vondear.rxtools.view.dialog.RxDialog;


public class App extends Application {
    private static final String DB_NAME = "face";
    private static Context sCtx;
    private static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        sCtx = this;
        RxTool.init(this);
        SpConfig.init(this);

        initGreenDao();
    }

    private void initGreenDao(){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DB_NAME);
        SQLiteDatabase database = helper.getWritableDatabase();
        daoSession = new DaoMaster(database).newSession();
    }

    public static DaoSession getDaoSession(){
        return daoSession;
    }

    public static Context getCtx(){
        return sCtx;
    }
}
