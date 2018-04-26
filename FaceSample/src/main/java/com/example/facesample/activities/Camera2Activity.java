package com.example.facesample.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.example.facesample.R;
import com.example.facesample.fragments.CameraFragment;
import com.example.facesample.fragments.BitmapFragment;

public class Camera2Activity extends AppCompatActivity implements CameraFragment.Callback, BitmapFragment.Callback {


    private CameraFragment mDisplayCameraFragment;
    private BitmapFragment mDisplayBitmapFragment;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_camera2);
        initFragment();
    }


    private void initFragment(){

        mDisplayCameraFragment = new CameraFragment();
        mDisplayCameraFragment.setCallback(this);
        mDisplayBitmapFragment = new BitmapFragment();
        mDisplayBitmapFragment.setCallback(this);

        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.camera_fl,mDisplayCameraFragment);
        ft.commit();
    }

    private void setFullScreen() {
           /*set it to be no title*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    public void onTakePhoto(CameraFragment fragment, Bitmap bitmap) {
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        mDisplayBitmapFragment.setBitmap(bitmap);
        ft.add(R.id.camera_fl,mDisplayBitmapFragment);
        ft.commit();
    }

    @Override
    public void onFinishAty() {
        finish();
    }

    @Override
    public void onAction(BitmapFragment fragment, boolean useBitmap, Bitmap bitmap) {
        if(!useBitmap){
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(fragment);
            ft.add(R.id.camera_fl,mDisplayCameraFragment);
            ft.commit();
        }
    }
}
