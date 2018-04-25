package com.example.facesample;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.facesample.engine.imgscan.ImgScanner;
import com.example.facesample.engine.imgscan.ImgSubscriber;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImgScanner.scanSDCard(
                new ImgSubscriber(Environment.getExternalStorageDirectory()) {

            @Override
            public void onScanProgress(List<File> files) {
                Log.e(TAG, "onScanProgress: "+files.size());
            }

            @Override
            public void onScanCompleted(List<File> files) {
                Log.e(TAG, "onScanCompleted: "+files.size());
            }

            @Override
            public void onScanStart() {
                Log.e(TAG, "onScanStart: ");
            }

            @Override
            public void onScanEnd() {
                Log.e(TAG, "onScanEnd: ");
            }
        });
    }
}
