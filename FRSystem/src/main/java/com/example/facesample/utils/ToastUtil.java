package com.example.facesample.utils;


import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

public class ToastUtil {

    private static Toast sToastObj;

    public static void show(Context ctx, String msg){
        if(sToastObj == null)
            sToastObj = Toast.makeText(ctx, "", Toast.LENGTH_SHORT);
        sToastObj.setText(msg);
        sToastObj.show();
    }

    public static void show(Context ctx, @StringRes int res){
        if(sToastObj == null)
            sToastObj = Toast.makeText(ctx, "", Toast.LENGTH_SHORT);
        sToastObj.setText(res);
        sToastObj.show();
    }
}
