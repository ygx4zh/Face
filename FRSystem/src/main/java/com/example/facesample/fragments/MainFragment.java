package com.example.facesample.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.database.ContentObserver;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.facesample.R;
import com.example.facesample.activities.Camera2Activity;
import com.example.facesample.activities.SamplingActivity;
import com.example.facesample.activities.VerifyActivity;
import com.example.facesample.adapters.ImgAdapter;
import com.example.facesample.db.DBManager;
import com.example.facesample.db.bean.FaceImageBean;
import com.example.facesample.engine.imgscan.ImgScanner;
import com.example.facesample.engine.imgscan.ImgSubscriber;
import com.example.facesample.engine.imgscan.ToImgFun;
import com.example.facesample.ui.dialogs.LoadingDialog;
import com.example.facesample.ui.dialogs.MenuDialog;
import com.example.facesample.ui.dialogs.SelectFolderDialog;
import com.example.facesample.utils.AppHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainFragment extends Fragment implements View.OnClickListener, ImgAdapter.OnItemLongClickListener {

    private RecyclerView mRecyView;
    private ImgAdapter adapter;
    private List<FaceImageBean> imgBeans;
    private View mIv;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    // 照相
    public static final int TAKE_PHOTO     = 2;

    // 录入图片
    public static final int ENTER_PHOTO    = 1;
    private String takePhotPath;
    private LoadingDialog loadingDialog;

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

        Uri uri = Uri.parse("content://face");
        getContext().getContentResolver().registerContentObserver(uri,false,mObserver);
    }

    private ContentObserver mObserver = new ContentObserver(mHandler) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            initData();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().getContentResolver().unregisterContentObserver(mObserver);
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
        mRecyView = (RecyclerView) v.findViewById(R.id.test_recyV);
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
        adapter.setOnItemLongClickListener(this);
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
                // 弹出 录入人脸 | 导入人脸 菜单对话框
                showActionMenu(v);
                break;
            case R.id.test_fab_contrast:
                showCompare(v);
                break;
        }
    }

    private void showCompare(View v) {
        Snackbar.make(v, "人脸识别", Snackbar.LENGTH_LONG)
                .setAction("点我", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // startCameraAty(TAKE_PHOTO);
                        callSystemCameraCapture(TAKE_PHOTO);
                    }
                }).show();
    }

    private void startCameraAty(int type) {
        Intent intent = new Intent(getActivity(), Camera2Activity.class);
        intent.putExtra("type",type);
        File dir = new File(Environment.getExternalStorageDirectory(), "face");
        if(!dir.exists() || dir.isFile()){
            dir.mkdir();
        }
        File photo = new File(dir, System.currentTimeMillis() + "_face.png");
        takePhotPath = photo.getAbsolutePath();
        intent.putExtra("path",takePhotPath);
        startActivity(intent);
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

    private void showActionMenu(View v) {
        // 弹出对话框,
        MenuDialog dialog = new MenuDialog(getActivity(),new String[]{"录入人脸","导入人脸"});
        dialog.setCallback(new MenuDialog.Callback() {
            @Override
            public void onAction(Dialog dialog, int index) {
                switch (index) {
                    case 1:                 // 如果对话框选中的是索引为1(也就是导入人脸)选项,
                        loadFaces();        // 执行导入人脸的方法;
                        break;
                    case 0:
                        // startCameraAty(ENTER_PHOTO);
                        callSystemCameraCapture(ENTER_PHOTO);
                        break;
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 弹出框选择sd卡文件夹, 并导入所选择文件夹下的文件
     */
    void loadFaces(){

        // 创建选择文件的对话框,
        SelectFolderDialog selectFolderDialog = new SelectFolderDialog(getActivity(), new SelectFolderDialog.Callback() {
            @Override
            public void onSelected(Dialog dialog, boolean selected, @Nullable String folderPath) {

                dialog.dismiss();
                // 如果文件夹被选中, 并且文件夹路径不为空,
                if(selected && !TextUtils.isEmpty(folderPath)) {

                    // 扫描sd卡上指定路径的文件夹,
                    // 将该文件夹下满足条件的图片通过ImgSubscriber方法回调到onScanCompleted方法
                    // 参数一是文件转成人脸数据的功能接口对象,
                    // 参数二是扫描的进度以及结果订阅者
                    ImgScanner.scanSDCard(new ToImgFun(), new ImgSubscriber<FaceImageBean>(new File(folderPath)) {
                        @Override
                        public void onScanStart() {
                            // 开始扫描的时候, 用于弹出进度对话框loading dialog
                            showLoadingDialog();
                        }

                        @Override
                        public void onScanEnd() {
                            // 扫描结束的时候, 用于执行取消进度对话框和刷新扫描到的数据
                            dismissLoadingDialog();
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onScanCompleted(List<FaceImageBean> files) {
                            // 扫描完成的时候, 所扫描到的人脸数据通过files接口传递过来
                            // 将扫描到的人脸数据插入数据库
                            DBManager.insertFaceImages(files);
                            // 将扫描到的人脸数据添加到显示的集合中;
                            imgBeans.addAll(files);
                        }
                    });
                }
            }
        });
        selectFolderDialog.show();
    }

    private void showLoadingDialog(){
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.show();
    }
    private void dismissLoadingDialog(){
        if(loadingDialog != null){
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    private static final String TAG = "MainFragment";


    @Override
    public void onItemLongClick(View itemView, final int position, final FaceImageBean obj) {
        MenuDialog dialog = new MenuDialog(getActivity(),new String[]{"隐藏","删除"});
        dialog.setCallback(new MenuDialog.Callback() {
            @Override
            public void onAction(Dialog dialog, int action) {
                switch (action) {
                    case 1:
                        delete(position,obj);
                        break;
                    case 0:
                        hidden(position,obj);
                        break;
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void hidden(int position,FaceImageBean obj) {
        FaceImageBean bean = imgBeans.remove(position);
        adapter.notifyItemRemoved(position);
    }

    private void delete(int position,FaceImageBean obj) {

        int index = imgBeans.indexOf(obj);
        imgBeans.remove(index);
        DBManager.deleteFaceImageBean(obj);

         adapter.notifyItemRemoved(index);
    }
}
