package com.example.facesample;


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
}
