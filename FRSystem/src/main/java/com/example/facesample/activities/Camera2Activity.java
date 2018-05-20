package com.example.facesample.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.facesample.R;
import com.example.facesample.fragments.CameraFragment;
import com.example.facesample.fragments.BitmapFragment;
import com.example.facesample.fragments.MainFragment;
import com.example.facesample.utils.AppHelper;
import com.example.facesample.utils.ToastUtil;

import java.io.File;

public class Camera2Activity extends AppCompatActivity implements CameraFragment.Callback, BitmapFragment.Callback {


    private CameraFragment mDisplayCameraFragment;
    private BitmapFragment mDisplayBitmapFragment;
    private FragmentManager fm;
    private int mType;
    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        initData();
        setContentView(R.layout.activity_camera2);
        initFragment();
    }

    private void initData() {
        Intent intent = getIntent();
        mType = intent.getIntExtra("type", MainFragment.ENTER_PHOTO);
        mPath = intent.getStringExtra("path");
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

    private static final String TAG = "Camera2Activity";
    @Override
    public void onAction(BitmapFragment fragment, boolean useBitmap, Bitmap bitmap) {
        if(!useBitmap){
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(fragment);
            ft.add(R.id.camera_fl,mDisplayCameraFragment);
            ft.commit();
        }else{

            File file = AppHelper.saveImage(bitmap, mPath);
            if(file == null) {
                ToastUtil.show(getApplicationContext(),"保存图片失败");
                return;
            }
            Log.e(TAG, "onAction: "+file.getAbsolutePath());
            if(mType == MainFragment.ENTER_PHOTO){
                Intent intent = new Intent(this, SamplingActivity.class);
                intent.putExtra("path",file.getAbsolutePath());
                startActivity(intent);
            }else{
                Intent intent = new Intent(this, VerifyActivity.class);
                intent.putExtra("path",file.getAbsolutePath());
                startActivity(intent);
            }

            finish();
        }
    }
}
