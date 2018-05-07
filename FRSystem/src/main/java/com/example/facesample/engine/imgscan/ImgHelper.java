package com.example.facesample.engine.imgscan;


import android.graphics.Bitmap;

import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.example.facesample.compute.FaceVerify;
import com.example.facesample.db.bean.FaceImageBean;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ImgHelper {

    public static JSONObject readExtra(String path){
        JSONObject jsonObj = new JSONObject();
        InputStream is = null;
        try {
            File file = new File(path);
            if(!file.exists() || file.isDirectory()) return jsonObj;
            is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String name = reader.readLine();
            String sex = reader.readLine();
            StringBuilder sBuf = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null){
                sBuf.append(line.trim());
            }
            jsonObj.put("name",name);
            jsonObj.put("sex",sex);
            jsonObj.put("desc",sBuf.toString());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }

        return jsonObj;
    }

}
