package com.example.facesample.activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.facesample.adapters.SimilarAdapter;
import com.example.facesample.compute.FaceBean;
import com.example.facesample.compute.FaceVerify;
import com.example.facesample.db.DBManager;
import com.example.facesample.db.bean.FaceImageBean;
import com.example.facesample.engine.imgscan.GallyPageTransformer;
import com.example.facesample.holders.ImgHolder;
import com.example.facesample.ui.views.DisplayImageView;
import com.example.facesample.utils.AnimUtil;
import com.example.facesample.utils.AppHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VerifyActivity extends AppCompatActivity implements View.OnClickListener, SimilarAdapter.OnItemClickListener {

    public static Bitmap sBitmap;

    public final static int FILE = 1;
    public final static int PHOTO = 2;
    private ImageView mIv;
    private ViewPager mVp;
    private TextView mTv;
    private View mVSimilar;
    private View mLl;
    private View mFl;
    private Bitmap bitmap;


    private static final int UPDATE_FACE_COUNT = 1;
    private static final int REFRESHUI         = 2;
    private static final int SRC_NO_FACE       = 3;
    private static final int SIMILAR_EMPTY     = 4;
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
                case SIMILAR_EMPTY:
                    mTvCount.setText(R.string.no_similar_face);
                    break;
                case SRC_NO_FACE:
                    mTvCount.setText(R.string.src_no_face);
                    break;
            }
        }
    };
    private TextView mTvCount;
    private String photosFormat;
    private String infoFormat;
    private SimilarAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        hiddenAction();
        setContentView(R.layout.activity_verify);

        findView();

        initData();
    }

    private void hiddenAction(){
        ActionBar bar = getSupportActionBar();
        if(bar != null){
            bar.hide();
        }
    }

    private void initData() {

        photosFormat = getString(R.string.similar_photos_format);
        infoFormat = getString(R.string.info_format);

        Intent intent = getIntent();

        int type = intent.getIntExtra("type", 0);
        String path = intent.getStringExtra("path");
        bitmap = BitmapFactory.decodeFile(path);
        FaceImageBean image = createBitmapFaceImage(bitmap);
        mFiles.add(image);
        adapter.notifyDataSetChanged();
        mIv.setImageBitmap(bitmap);
        AppHelper.run(searchSimilar);
    }

    private void showInfo(FaceImageBean faceImageBean){
        String extra;
        if(faceImageBean == null || TextUtils.isEmpty(extra = faceImageBean.getExtra())){
            return;
        }
        try {
            JSONObject jsonObj = new JSONObject(extra);
            String name = jsonObj.getString("name");
            String sex = jsonObj.getString("sex");
            String desc = jsonObj.getString("desc");
            mTv.setText(
                    Html.fromHtml(
                            String.format(
                                    Locale.CHINA,
                                    infoFormat,
                                    name,
                                    sex,
                                    desc)));
        } catch (JSONException e) {

        }
    }

    private Runnable searchSimilar = new Runnable() {
        @Override
        public void run() {
            AFR_FSDKFace afr_fsdkFace = FaceVerify.extraBitmapFeature(bitmap);
            if(afr_fsdkFace == null){
                mHandler.sendEmptyMessage(SRC_NO_FACE);
                return;
            }
            String faceToken = DBManager.searchFace(afr_fsdkFace);
            if(TextUtils.isEmpty(faceToken)){
                mHandler.sendEmptyMessage(SIMILAR_EMPTY);
            }else{
                List<FaceImageBean> list = DBManager.queryFaceImagesByFaceToken(faceToken);
                mFiles.addAll(list);
                mHandler.sendMessage(Message.obtain(mHandler,UPDATE_FACE_COUNT,list.size()));
                mHandler.sendEmptyMessage(REFRESHUI);
            }
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

        mVp.setOnClickListener(this);
        adapter.setOnItemClickListener(this);
    }

    private FaceImageBean createBitmapFaceImage(Bitmap bmp){
        FaceImageBean bean = new FaceImageBean();
        bean.setmBmp(bmp);
        bean.setType(FaceImageBean.FACE_BITMAP);
        return bean;
    }
    private static final String TAG = "VerifyActivity";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verify_tv_similar:
                // 相似图片
                switchViewpagerVisibility();
                break;
            case R.id.verify_vp:
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

    @Override
    public void onClick(View view, int position, FaceImageBean bean) {
        showSimilar(bean);
    }

    private void showSimilar(FaceImageBean bean){
        if(bean.getType() == FaceImageBean.FACE_BITMAP){
            mTv.setText("");
            mIv.setImageBitmap(bean.getmBmp());
        }else {
            String path = bean.getPath();
            File file = new File(path);
            // Picasso.get().load(file).into(mIv);
            mIv.setImageBitmap(BitmapFactory.decodeFile(path, ImgHolder.opts));
            showInfo(bean);
        }
    }
}
