package com.example.facesample.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.facesample.R;
import com.example.facesample.activities.SamplingActivity;
import com.example.facesample.activities.VerifyActivity;
import com.example.facesample.adapters.ImgAdapter;
import com.example.facesample.db.DBManager;
import com.example.facesample.db.bean.FaceImageBean;
import com.example.facesample.engine.imgscan.ImgScanner;
import com.example.facesample.engine.imgscan.ImgSubscriber;
import com.example.facesample.engine.imgscan.ToImgFun;
import com.example.facesample.ui.dialogs.LoadingDialog;
import com.example.facesample.ui.dialogs.SelectFolderDialog;
import com.example.facesample.utils.AppHelper;
import com.example.facesample.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainFragment extends Fragment implements View.OnClickListener, SelectFolderDialog.Callback {

    private RecyclerView mRecyView;
    private ImgAdapter adapter;
    private List<FaceImageBean> imgBeans;
    private LoadingDialog loadingDialog;
    private View mIv;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    // 照相
    private static final int TAKE_PHOTO     = 2;

    // 录入图片
    private static final int ENTER_PHOTO    = 1;
    private String takePhotPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(view);

        initData();
    }

    private void initData() {
        AppHelper.run(new Runnable() {
            @Override
            public void run() {
                List<FaceImageBean> list = DBManager.queryFaceImages();
                imgBeans.clear();
                Collections.sort(list, new Comparator<FaceImageBean>() {
                    @Override
                    public int compare(FaceImageBean o1, FaceImageBean o2) {
                        String face_token = o1.getFace_token();
                        String face_token2 = o2.getFace_token();
                        if(TextUtils.isEmpty(face_token) && TextUtils.isEmpty(face_token2))
                        {
                            return o1.getFname().compareTo(o2.getFname());
                        }

                        if(!TextUtils.isEmpty(face_token) && !TextUtils.isEmpty(face_token2))
                        {
                            return o1.getFname().compareTo(o2.getFname());
                        }

                        return TextUtils.isEmpty(face_token) ? 1 : -1;
                    }
                });
                imgBeans.addAll(list);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void findView(View v) {
        mRecyView = v.findViewById(R.id.test_recyV);
        mIv = v.findViewById(R.id.test_iv_empty);
        v.findViewById(R.id.test_fab_action).setOnClickListener(this);
        v.findViewById(R.id.test_fab_contrast).setOnClickListener(this);
        initRecyViewAdapter();
    }

    private void initRecyViewAdapter() {
        //设置layoutManager
        mRecyView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        2,
                        StaggeredGridLayoutManager.VERTICAL));
        //设置adapter
        imgBeans = new ArrayList<>();
        adapter = new ImgAdapter(imgBeans);
        mRecyView.setAdapter(adapter);
        //设置item之间的间隔
        mRecyView.addItemDecoration(new RecyclerView.ItemDecoration() {

            private int space = 20;

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = space;
                outRect.right = space;
                outRect.bottom = space;
                //第一个不设置间隔,否则顶部有空白
                if (parent.getChildPosition(view) != 0 || parent.getChildPosition(view) != 1) {
                    outRect.top = space;
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.test_fab_action:
                showSnake(v);
                break;
            case R.id.test_fab_contrast:
                showCompare(v);
                // startActivity(new Intent(getActivity(), Camera2Activity.class));
                break;
        }
    }

    private void showCompare(View v) {
        Snackbar.make(v, "对比图片", Snackbar.LENGTH_LONG)
                .setAction("点我", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // startActivity(new Intent(getActivity(), Camera2Activity.class));
                        callSystemCameraCapture(TAKE_PHOTO);
                    }
                }).show();
    }

    private void callSystemCameraCapture(int action){
        File dir = new File(Environment.getExternalStorageDirectory(), "face");
        if(!dir.exists() || dir.isFile()){
            dir.mkdir();
        }
        File photo = new File(dir, System.currentTimeMillis() + "_face.png");
        takePhotPath = photo.getAbsolutePath();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 启动系统相机
        Uri photoUri = Uri.fromFile(photo); // 传递路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);// 更改系统默认存储路径
        startActivityForResult(intent, action);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 0) return;
        if (requestCode == TAKE_PHOTO) {

            Intent intent = new Intent(getActivity(), VerifyActivity.class);
            intent.putExtra("type",VerifyActivity.PHOTO);
            intent.putExtra("path",takePhotPath);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getActivity(), SamplingActivity.class);
            intent.putExtra("path",takePhotPath);
            startActivity(intent);
        }
    }

    private void showSnake(View v) {
        Snackbar.make(v, "录入图片", Snackbar.LENGTH_LONG)
                .setAction("点我", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        showSelectDialog();
                        callSystemCameraCapture(ENTER_PHOTO);
                    }
                }).show();
    }


    @Override
    public void onSelected(Dialog dialog, boolean selected, @Nullable String folderPath) {

        if (dialog.isShowing())
            dialog.dismiss();

        if (selected && !TextUtils.isEmpty(folderPath)) {
            ToastUtil.show(getContext(), folderPath);
            loadImgs(folderPath);
            // mRecyView.setVisibility(View.VISIBLE);
        }
    }

    private static final String TAG = "TestFragment";

    private void loadImgs(String path) {
        ImgScanner.scanSDCard(
                new ToImgFun(),
                new ImgSubscriber<FaceImageBean>(new File(path)) {
                    @Override
                    public void onScanStart() {
                        imgBeans.clear();
                        startLoadingAnim();
                    }

                    @Override
                    public void onScanCompleted(List<FaceImageBean> files) {
                        imgBeans.clear();
                        imgBeans.addAll(files);
                        Collections.sort(imgBeans, new Comparator<FaceImageBean>() {
                            @Override
                            public int compare(FaceImageBean o1, FaceImageBean o2) {
                                String face_token = o1.getFace_token();
                                String face_token2 = o2.getFace_token();
                                if(TextUtils.isEmpty(face_token) && TextUtils.isEmpty(face_token2))
                                {
                                    return o1.getFname().compareTo(o2.getFname());
                                }

                                if(!TextUtils.isEmpty(face_token) && !TextUtils.isEmpty(face_token2))
                                {
                                    return o1.getFname().compareTo(o2.getFname());
                                }

                                return TextUtils.isEmpty(face_token) ? 1 : -1;
                            }
                        });
                        DBManager.clearAllFaceImages();
                        DBManager.insertFaceImages(files);
                        List<FaceImageBean> images = DBManager.queryFaceImages();
                        Log.e(TAG, "onScanCompleted: "+images.size());
                    }

                    @Override
                    public void onScanEnd() {
                        Log.e(TAG, "onScanEnd: ");
                        dismissDialog();
                        if (imgBeans.size() > 0) {
                            if (mIv.getVisibility() == View.VISIBLE) {
                                mIv.setVisibility(View.GONE);
                            }
                            adapter.notifyDataSetChanged();
                        }
                        else
                            mIv.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void startLoadingAnim() {
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.show();

    }

    private void dismissDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
}
