package com.example.facesample.activities;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.facesample.AppHelper;
import com.example.facesample.R;
import com.example.facesample.utils.ToastUtil;

import java.io.IOException;

public class Camera2Activity extends AppCompatActivity implements TextureView.SurfaceTextureListener, View.OnClickListener {

    private TextureView mTrv;
    private View mVTakePhoto;
    private View mVSwicthCamera;
    private SurfaceTexture mDisplaySurface;

    private int mCurCameraId = 0;
    private Camera mCamera;
    private boolean isCanSwitchCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_camera2);
        findView();
    }

    private void setFullScreen(){
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

    private void findView(){

        mTrv = findViewById(R.id.camera2_trv);
        mVTakePhoto = findViewById(R.id.camera2_v_takePhoto);
        mVSwicthCamera = findViewById(R.id.camera2_v_switch);

        mVSwicthCamera.setOnClickListener(this);
        mVTakePhoto.setOnClickListener(this);

        mTrv.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mDisplaySurface = surface;
        AppHelper.run(mOpenCameraRunnable);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    private Runnable mOpenCameraRunnable = new Runnable() {
        @Override
        public void run() {
            // 打开相机
            Camera camera = Camera.open(mCurCameraId);

            // 设置预览角度
            camera.setDisplayOrientation(
                    AppHelper.getCameraDisplayRotation(
                            getApplicationContext(), mCurCameraId));

            // 开启预览
            try {
                camera.setPreviewTexture(mDisplaySurface);
                camera.startPreview();
                mCamera = camera;
                isCanSwitchCamera = true;
            } catch (IOException e) {
                e.printStackTrace();
                // 打开相机失败
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera2_v_switch:
                if (!isCanSwitchCamera) ToastUtil.show(getApplicationContext(), R.string.op_too_often);
                else switchCamera();
                break;
            case R.id.camera2_v_takePhoto: break;
        }
    }

    void switchCamera() {
        if (mCamera != null) {
            isCanSwitchCamera = false;
            AppHelper.run(mReleaseCameraAndSwitchCameraRunnable);
        }
    }

    private Runnable mReleaseCameraAndSwitchCameraRunnable = new Runnable() {
        @Override
        public void run() {
            releaseCurCamera();
            int supportCameras = AppHelper.getSupportCameras();

            if (mCurCameraId == supportCameras - 1)
                mCurCameraId = 0;
            else
                mCurCameraId++;

            mOpenCameraRunnable.run();
        }
    };

    private void releaseCurCamera() {
        try {
            mCamera.setPreviewTexture(null);
        } catch (IOException ignored) {
        }
        try {
            mCamera.stopPreview();
        } catch (Exception ignored) {
        }

        try {
            mCamera.release();
        } catch (Exception ignored) {
        }

        mCamera = null;
    }
}
