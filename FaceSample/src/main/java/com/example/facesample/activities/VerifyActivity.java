package com.example.facesample.activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.example.facesample.R;
import com.example.facesample.adapters.FolderAdapter;
import com.example.facesample.adapters.SimilarAdapter;
import com.example.facesample.compute.FaceBean;
import com.example.facesample.compute.FaceVerify;
import com.example.facesample.db.DBManager;
import com.example.facesample.db.bean.FaceImageBean;
import com.example.facesample.engine.imgscan.GallyPageTransformer;
import com.example.facesample.ui.views.DisplayImageView;
import com.example.facesample.utils.AnimUtil;
import com.example.facesample.utils.AppHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VerifyActivity extends AppCompatActivity implements View.OnClickListener {
    public final static int FILE = 1;
    public final static int PHOTO = 2;
    private DisplayImageView mIv;
    private ViewPager mVp;
    private TextView mTv;
    private View mVSimilar;
    private View mLl;
    private View mFl;
    private Bitmap bitmap;


    private static final int UPDATE_FACE_COUNT = 1;
    private static final int REFRESHUI         = 2;
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_FACE_COUNT:
                    mTvCount.setText(String.format(Locale.CHINA,photosFormat,(int)msg.obj));
                    break;
                case REFRESHUI:
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private TextView mTvCount;
    private String photosFormat;
    private SimilarAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_verify);

        findView();

        initData();
    }

    private void initData() {

        photosFormat = getString(R.string.similar_photos_format);

        Intent intent = getIntent();

        int type = intent.getIntExtra("type", 0);
        switch (type){
            case FILE:
                String path = intent.getStringExtra("path");
                bitmap = BitmapFactory.decodeFile(path);
                mIv.setImageBitmap(bitmap);
                break;
        }

        AppHelper.run(searchSimilar);
    }

    private Runnable searchSimilar = new Runnable() {
        @Override
        public void run() {
            AFR_FSDKFace afr_fsdkFace = FaceVerify.extraBitmapFeature(bitmap);
            if(afr_fsdkFace == null){
                Log.e(TAG, "run: 未找到人脸");
                return;
            }
            List<FaceImageBean> faceImageBeans = DBManager.queryFaceImages();
            Log.e(TAG, "run: "+faceImageBeans.size());
            for (FaceImageBean bean : faceImageBeans) {
                String face_token = bean.getFace_token();
                if(TextUtils.isEmpty(face_token))
                {
                    Log.e(TAG, "run: face token is null");
                    continue;
                }

                FaceBean face = FaceBean.decodeFile(face_token);
                Log.e(TAG, "run: face_token "+face_token);
                if (face == null) {
                    Log.e(TAG, "run: local file is null: "+face_token);
                    continue;
                }
                double cump = FaceVerify.cump(afr_fsdkFace, face.getMface());
                Log.e(TAG, "run: cump: "+cump);
                if(cump > 0.5f){
                    mHandler.sendMessage(Message.obtain(mHandler,UPDATE_FACE_COUNT,++ count));
                    mFiles.add(bean);
                }
            }
            mHandler.sendMessage(Message.obtain(mHandler,REFRESHUI));
        }
    };

    private int count;


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

    private List<FaceImageBean> mFiles = new ArrayList<>();

    void findView(){
        mIv = findViewById(R.id.verify_iv);
        mVp = findViewById(R.id.verify_vp);
        mTv = findViewById(R.id.verify_tv_info);
        mVSimilar = findViewById(R.id.verify_tv_similar);
        mLl = findViewById(R.id.verify_ll);
        mFl = findViewById(R.id.verify_fl);
        mVSimilar.setOnClickListener(this);
        mTvCount = findViewById(R.id.verify_tv_count);
        mVp.setOffscreenPageLimit(3);
        int pagerWidth = (int) (getResources().getDisplayMetrics().widthPixels * 3.0f / 5.0f);
        ViewGroup.LayoutParams lp = mVp.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(pagerWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            lp.width = pagerWidth;
        }
        mVp.setLayoutParams(lp);
        mVp.setPageMargin(-50);
        mVp.setPageTransformer(true, new GallyPageTransformer());
        adapter = new SimilarAdapter(mFiles);
        mVp.setAdapter(adapter);
    }


    private static final String TAG = "VerifyActivity";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verify_tv_similar:
                // 相似图片
                switchViewpagerVisibility();
                break;
        }
    }

    private int mSwitchMode = VISIBLITY;

    /* 切换中 */
    private static final int SWITCHING      = 1;
    /* 已显示 */
    private static final int VISIBLITY      = 2;
    /* 未显示 */
    private static final int INVISIBLITY    = 3;
    private void switchViewpagerVisibility() {
        if(mSwitchMode == SWITCHING) return;
        if(mSwitchMode == VISIBLITY){
            mSwitchMode = SWITCHING;
            final int[] position = new int[2];
            mFl.getLocationOnScreen(position);
            AnimUtil.playTranslationYOut(mFl, mLl,
                    new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    // mIv.updateBitmapScale(true, position[1],(Float) animation.getAnimatedValue());
                }
            });
            mSwitchMode = INVISIBLITY;
        }else{
            mSwitchMode = SWITCHING;
            final int[] position = new int[2];
            mFl.getLocationOnScreen(position);
            AnimUtil.playTranslationYIn(mFl, mLl,
                    new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    // mIv.updateBitmapScale(false, position[1], (Float) animation.getAnimatedValue());
                }
            });
            mSwitchMode = VISIBLITY;
        }
    }
}
