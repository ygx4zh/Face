package com.example.facesample.utils;


import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    private static Toast sToastObj;

    public static void show(Context ctx, String msg){
        if(sToastObj == null)
            sToastObj = Toast.makeText(ctx, "", Toast.LENGTH_SHORT);
        sToastObj.setText(msg);
        sToastObj.show();
    }
}
