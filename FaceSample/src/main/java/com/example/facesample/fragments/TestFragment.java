package com.example.facesample.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.example.facesample.activities.Camera2Activity;
import com.example.facesample.activities.VerifyActivity;
import com.example.facesample.adapters.ImgAdapter;
import com.example.facesample.bean.ImgBean;
import com.example.facesample.db.DBManager;
import com.example.facesample.db.bean.FaceImageBean;
import com.example.facesample.engine.imgscan.FaceFilterImpl;
import com.example.facesample.engine.imgscan.Function;
import com.example.facesample.engine.imgscan.ImgScanner;
import com.example.facesample.engine.imgscan.ImgSubscriber;
import com.example.facesample.engine.imgscan.ToImgFun;
import com.example.facesample.ui.dialogs.LoadingDialog;
import com.example.facesample.ui.dialogs.SelectFolderDialog;
import com.example.facesample.utils.AppHelper;
import com.example.facesample.utils.SpConfig;
import com.example.facesample.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class TestFragment extends Fragment implements View.OnClickListener, SelectFolderDialog.Callback {

    private RecyclerView mRecyView;
    private ImgAdapter adapter;
    private List<FaceImageBean> imgBeans;
    private LoadingDialog loadingDialog;
    private View mIv;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, null);
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
                        startActivity(new Intent(getActivity(), Camera2Activity.class));
                    }
                }).show();
    }

    private void showSnake(View v) {
        Snackbar.make(v, "导入图片", Snackbar.LENGTH_LONG)
                .setAction("点我", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSelectDialog();
                    }
                }).show();
    }

    private void showSelectDialog() {
        SelectFolderDialog dialog = new SelectFolderDialog(getActivity(), this);
        dialog.show();
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
