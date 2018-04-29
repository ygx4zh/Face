package com.example.facesample.db;


import android.text.TextUtils;
import android.util.Log;

import com.example.facesample.App;
import com.example.facesample.db.bean.FaceImageBean;
import com.example.facesample.db.greendao.FaceImageBeanDao;

import org.greenrobot.greendao.query.Query;

import java.util.List;

public class DBManager {
    private static final String TAG = "DBManager";
    public static void insertFaceImages(List<FaceImageBean> list) {
        FaceImageBeanDao dao = App.getDaoSession().getFaceImageBeanDao();
        for (FaceImageBean bean : list) {
            try {
                if (bean != null) {
                Log.e(TAG, "insertFaceImages: " + bean);
                    dao.insert(bean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static List<FaceImageBean> queryFaceImages(){
        FaceImageBeanDao dao = App.getDaoSession().getFaceImageBeanDao();
        return dao.loadAll();
    }

    public static void clearAllFaceImages(){
        FaceImageBeanDao dao = App.getDaoSession().getFaceImageBeanDao();
        dao.deleteAll();
    }

    public static FaceImageBean queryFaceImgBeanByFilePath(String path){
        if(TextUtils.isEmpty(path)) return null;

        FaceImageBeanDao dao = App.getDaoSession().getFaceImageBeanDao();
        Query<FaceImageBean> build = dao.queryBuilder()
                .where(FaceImageBeanDao.Properties.Path.eq(path)).build();
        List<FaceImageBean> list = build.list();
        if(list.size() <= 0) return null;
        return list.get(0);
    }
}
