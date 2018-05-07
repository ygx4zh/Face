package com.example.facesample.fragments;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.example.facesample.utils.AppHelper;
import com.example.facesample.R;
import com.example.facesample.utils.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CameraFragment extends Fragment implements View.OnClickListener,
        TextureView.SurfaceTextureListener,
        Camera.AutoFocusCallback,
        View.OnTouchListener {

    private TextureView mTrv;
    private View mVTakePhoto;
    private View mVSwicthCamera;
    private SurfaceTexture mDisplaySurface;

    private int mCurCameraId = 0;
    private Camera mCamera;
    private boolean isCanSwitchCamera;
    private Callback mCb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(view);
    }

    private void findView(View v) {

        mTrv = v.findViewById(R.id.camera2_trv);
        // mTrv.setOnTouchListener(this);
        mVTakePhoto = v.findViewById(R.id.camera2_v_takePhoto);
        mVSwicthCamera = v.findViewById(R.id.camera2_v_switch);
        v.findViewById(R.id.camera2_v_back).setOnClickListener(this);

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
                            getContext(), mCurCameraId));

            // 开启预览
            try {
                camera.setPreviewTexture(mDisplaySurface);
                camera.startPreview();
                mCamera = camera;
                mCamera.autoFocus(CameraFragment.this);
                isCanSwitchCamera = true;
            } catch (IOException e) {
                e.printStackTrace();
                // 打开相机失败
            }
        }
    };

    private Rect rect = new Rect();

    protected void focusOnRect(Rect rect) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters(); // 先获取当前相机的参数配置对象
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO); // 设置聚焦模式
            if (parameters.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
                focusAreas.add(new Camera.Area(rect, 1000));
                parameters.setFocusAreas(focusAreas);
            }
            mCamera.cancelAutoFocus(); // 先要取消掉进程中所有的聚焦功能
            mCamera.setParameters(parameters); // 一定要记得把相应参数设置给相机
            mCamera.autoFocus(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera2_v_switch:
                if (!isCanSwitchCamera)
                    ToastUtil.show(getContext(), R.string.op_too_often);
                else switchCamera();
                break;
            case R.id.camera2_v_takePhoto:
                takePhoto();
                break;
            case R.id.camera2_v_back:
                if (mCb != null) {
                    mCb.onFinishAty();
                }
                break;
        }
    }

    private void takePhoto() {
        Bitmap bitmap = mTrv.getBitmap();
        if (mCb != null) {
            mCb.onTakePhoto(this, bitmap);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppHelper.run(new Runnable() {
            @Override
            public void run() {
                releaseCurCamera();
            }
        });
    }

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

    public void setCallback(Callback cb) {
        mCb = cb;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        Log.e(TAG, "onAutoFocus: " + success);
    }

    private static final String TAG = "CameraFragment";
    private static final int LEN = 200;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        Log.e(TAG, "onTouch: " + action);
        if (action == MotionEvent.ACTION_DOWN) return true;
        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (x - LEN < 0 || y - LEN < 0)
                return false;

            rect.set(
                    x - LEN / 2,
                    y - LEN / 2,
                    x + LEN / 2,
                    y + LEN / 2);
            Log.e(TAG, "onTouch: "+rect);
            // focusOnRect(rect);
            return true;
        }
        return false;
    }

    public interface Callback {

        void onTakePhoto(CameraFragment fragment, Bitmap bitmap);

        void onFinishAty();
    }
}
