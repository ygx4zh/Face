package com.example.facesample.compute;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.guo.android_extend.image.ImageConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

 public class FaceVerify {

    private static final String TAG = "Comput";
    public static String appid = "EDVRFWJi1ZQkCx4Fg8PwECc6SETFXgwv5QuuUNojkvNY";
    public static String fd_key = "6RGDMq3XiLeb3TkRCSG1h3LQXdwDxnA5fdGHxBVunYAZ";
    public static String fr_key = "6RGDMq3XiLeb3TkRCSG1h3LXh3CQdtMVtyDs4hgdaM6U";


    public static final ExecutorService executorService = Executors.newFixedThreadPool(1);//两条线程处理


     /**
      * 返回总分为1.0的分数
      * @param b
      * @param mIDCardpic
      * @return
      */
    public static double cump(AFR_FSDKFace b, AFR_FSDKFace mIDCardpic) {
        AFR_FSDKMatching score = new AFR_FSDKMatching();
        engine1.AFR_FSDK_FacePairMatching(b,mIDCardpic,score);
        return score.getScore();
    }


    public static AFR_FSDKEngine engine1 = new AFR_FSDKEngine();//转换图片为特征码的引擎
    public static AFD_FSDKEngine engine3 = new AFD_FSDKEngine();


    static {

        AFR_FSDKError err1 = engine1.AFR_FSDK_InitialEngine(appid, fr_key);
        Log.d("init", "AFR_FSDK_InitialFaceEngine =" + err1.getCode());

        AFD_FSDKError error2 = engine3.AFD_FSDK_InitialFaceEngine(appid, fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);
        Log.d("init", "AFD_FSDK_InitialFaceEngine =" + error2.getCode());

    }


    /**
     * 转换bitmap信息
     * @param mBitmap
     * @param listener
     * 返回检测到的AFR_FSDKFace对象或者返回null
     */
    public static void extraBitmapFeature(final Bitmap mBitmap, final OnAFRFaceListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight() * 3 / 2];
                ImageConverter convert = new ImageConverter();
                convert.initial(mBitmap.getWidth(), mBitmap.getHeight(), ImageConverter.CP_PAF_NV21);
                if (convert.convert(mBitmap, data)) {
                    Log.d(TAG, "convert ok!");
                }
                convert.destroy();

                List<AFD_FSDKFace> result = new ArrayList<AFD_FSDKFace>();
                engine3.AFD_FSDK_StillImageFaceDetection(data, mBitmap.getWidth(), mBitmap.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, result);
                if (result.size() == 0){//未检测到可供转换的图像
                    listener.onBack(null);
                }else {
                    AFR_FSDKFace result1 = new AFR_FSDKFace();
                    engine1.AFR_FSDK_ExtractFRFeature(data, mBitmap.getWidth(), mBitmap.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, new Rect(result.get(0).getRect()), result.get(0).getDegree(), result1);
                    listener.onBack(result1);
                }
            }
        });
    }


    public interface OnAFRFaceListener{
        void onBack(AFR_FSDKFace afr_fsdkFace);
    }


}
