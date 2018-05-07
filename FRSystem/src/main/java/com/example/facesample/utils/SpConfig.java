package com.example.facesample.utils;


import android.content.Context;
import android.content.SharedPreferences;

public class SpConfig {

    public static final String PATH = "";

    private static final String SP_NAME = "config";
    private static SharedPreferences sp;

    public static void init(Context ctx)
    {
        sp = ctx.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static void put(String key,String value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(String key,String defaultValue){
        return sp.getString(key, defaultValue);
    }
}
