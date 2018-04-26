package com.example.facesample;


import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppHelper {

    private static ExecutorService pool = Executors.newCachedThreadPool();

    public static void run(Runnable r){
        if (r == null) {
            return;
        }

        pool.execute(r);
    }

    public static int dp2px(Context ctx, float dpValue) {

        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
