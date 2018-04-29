package com.example.facesample.activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.facesample.R;
import com.example.facesample.adapters.SimilarAdapter;
import com.example.facesample.engine.imgscan.GallyPageTransformer;
import com.example.facesample.ui.views.DisplayImageView;
import com.example.facesample.utils.AnimUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VerifyActivity extends AppCompatActivity implements View.OnClickListener {
    public final static int FILE = 1;
    public final static int PHOTO = 2;
    private DisplayImageView mIv;
    private ViewPager mVp;
    private TextView mTv;
    private View mVSimilar;
    private View mLl;
    private View mFl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_verify);

        findView();

        initData();
    }

    private void initData() {
        Intent intent = getIntent();

        int type = intent.getIntExtra("type", 0);
        switch (type){
            case FILE:
                String path = intent.getStringExtra("path");
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                mIv.setImageBitmap(bitmap);
                break;
        }
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

    private List<File> mFiles = new ArrayList<>();

    void findView(){
        mIv = findViewById(R.id.verify_iv);
        mIv.setImageResource(R.mipmap.ai_2);
        mVp = findViewById(R.id.verify_vp);
        mTv = findViewById(R.id.verify_tv_info);
        mVSimilar = findViewById(R.id.verify_tv_similar);
        mLl = findViewById(R.id.verify_ll);
        mFl = findViewById(R.id.verify_fl);
        mVSimilar.setOnClickListener(this);
        mTv.setText(Html.fromHtml(getString(R.string.info_format)));
        initFilesData();
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
        mVp.setAdapter(new SimilarAdapter(mFiles));
    }
    void initFilesData(){

        File res = new File(Environment.getExternalStorageDirectory(), "res");
        File[] files = res.listFiles();
        for (File f : files) {
            boolean b = f.isFile() && f.getName().endsWith(".jpg");
            if(b){
                mFiles.add(f);
                Log.e(TAG, "initFilesData: "+f.getName());
            }
        }
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
//            Log.e(TAG, "switchViewpagerVisibility: "+translationY+" // "+top);
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
