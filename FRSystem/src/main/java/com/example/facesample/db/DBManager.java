package com.example.facesample.db;


import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.example.facesample.App;
import com.example.facesample.activities.VerifyActivity;
import com.example.facesample.compute.FaceBean;
import com.example.facesample.compute.FaceVerify;
import com.example.facesample.db.bean.FaceImageBean;
import com.example.frsystem.db.greendao.DaoSession;
import com.example.frsystem.db.greendao.FaceImageBeanDao;

import org.greenrobot.greendao.query.Query;

import java.io.File;
import java.util.ArrayList;
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

    public static List<FaceImageBean> queryFaceImagesByFaceToken(String faceToken){
        ArrayList<FaceImageBean> list = new ArrayList<>();
        if(TextUtils.isEmpty(faceToken)) return list;

        FaceImageBeanDao dao = App.getDaoSession().getFaceImageBeanDao();
        List<FaceImageBean> beanList = dao.queryBuilder()
                .where(FaceImageBeanDao.Properties.Face_token.eq(faceToken))
                .build()
                .list();
        return beanList;
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

    public static String searchFace(AFR_FSDKFace afr_fsdkFace){
        DaoSession daoSession = App.getDaoSession();
        final String sql  = "SELECT DISTINCT "
                + FaceImageBeanDao.Properties.Fname.columnName + ", "
                + FaceImageBeanDao.Properties.Face_token.columnName + ", "
                + FaceImageBeanDao.Properties.Face_feature.columnName + ", "
                + FaceImageBeanDao.Properties.Path.columnName + ", "
                + FaceImageBeanDao.Properties.Extra.columnName + ", "
                + FaceImageBeanDao.Properties.Type.columnName
                + " FROM "+FaceImageBeanDao.TABLENAME;
        Cursor cursor = daoSession.getDatabase().rawQuery(sql, null);

        ArrayList<FaceImageBean> list = new ArrayList<>();

        if(cursor.moveToFirst()){
            do {
                list.add(new FaceImageBean(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getInt(5)));
            }while (cursor.moveToNext());
        }

        cursor.close();

        // --------------------------------------

        for (FaceImageBean bean : list) {
            FaceBean faceBean = FaceBean.decodeFile(bean.getFace_feature());

            double cump = FaceVerify.cump(faceBean.getMface(), afr_fsdkFace);

            if(cump > 0.5f){
                return bean.getFace_token();
            }
        }

        return null;
    }

    public static void insertFceImageBean(FaceImageBean bean){
        FaceImageBeanDao dao = App.getDaoSession().getFaceImageBeanDao();
        try{
            dao.insert(bean);
        }catch (Exception ignored){
        }
    }

    public static void deleteFaceImageBean(FaceImageBean faceImageBean) {
        if(faceImageBean == null) return;
        FaceImageBeanDao dao = App.getDaoSession().getFaceImageBeanDao();
        dao.delete(faceImageBean);

        File file = new File(faceImageBean.getPath());
        if(file.exists()){
            boolean delete = file.delete();
            Log.e(TAG, "deleteFaceImageBean1: "+delete);
        }

        File face_fwature_path = new File(faceImageBean.getFace_feature());
        if(face_fwature_path.exists()){
            boolean delete2 = face_fwature_path.delete();
            Log.e(TAG, "deleteFaceImageBean2: "+delete2);
        }
    }
}
