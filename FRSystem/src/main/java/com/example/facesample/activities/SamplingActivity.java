package com.example.facesample.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.example.facesample.R;
import com.example.facesample.compute.FaceVerify;
import com.example.facesample.db.DBManager;
import com.example.facesample.db.bean.FaceImageBean;
import com.example.facesample.engine.imgscan.FaceFeature2FilePathHandler;
import com.example.facesample.engine.imgscan.FaceFeatureHandler;
import com.example.facesample.ui.dialogs.LoadingDialog;
import com.example.facesample.utils.AppHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;

public class SamplingActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mIv;
    private EditText mEtName;
    private EditText mEtSex;
    private EditText mEtDesc;
    private LoadingDialog loading;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hiddenActionBar();
        setContentView(R.layout.activity_sampling);

        findView();
    }

    void hiddenActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    void findView(){
        mIv = findViewById(R.id.sampling_iv);

        findViewById(R.id.sampling_btn_commit).setOnClickListener(this);

        mEtName = findViewById(R.id.sampling_et_name);
        mEtSex = findViewById(R.id.sampling_et_sex);
        mEtDesc = findViewById(R.id.sampling_et_desc);

        Picasso.get().load(new File(getIntent().getStringExtra("path"))).into(mIv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sampling_btn_commit:
                commit();
                break;
        }
    }

    final void commit(){
        loading = new LoadingDialog(this);
        loading.show();

        AppHelper.run(mCommit);
    }

    private Runnable mCommit = new Runnable() {
        @Override
        public void run() {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 2;
            String path = getIntent().getStringExtra("path");
            Bitmap bmp = BitmapFactory.decodeFile(path, opts);
            AFR_FSDKFace afr_fsdkFace = FaceVerify.extraBitmapFeature(bmp);
            String faceToken = DBManager.searchFace(afr_fsdkFace);
            Log.e(TAG, "run: "+faceToken);
            if(TextUtils.isEmpty(faceToken)){
                faceToken = AppHelper.createFaceToken();
            Log.e(TAG, "run2: "+faceToken);
            }
            File file = new File(path);
            FaceFeatureHandler<String> sFaceHandler
                    = new FaceFeature2FilePathHandler();
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("name", mEtName.getText().toString().trim());
                jsonObj.put("sex", mEtSex.getText().toString().trim());
                jsonObj.put("desc", mEtDesc.getText().toString().trim());
            }catch (Exception e){}

            FaceImageBean bean = new FaceImageBean(
                    file.getName(),
                    faceToken,
                    sFaceHandler.handle(afr_fsdkFace.getFeatureData()),
                    file.getAbsolutePath(), jsonObj.toString(), FaceImageBean.DEFAULT);
            Log.e(TAG, "run:3 "+bean.toString());

            DBManager.insertFceImageBean(bean);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (loading != null) {
                        if (loading.isShowing()) {
                            loading.dismiss();
                        }
                        loading = null;

                        finish();
                    }
                }
            });
        }
    };
    private static final String TAG = "SamplingActivity";
}
