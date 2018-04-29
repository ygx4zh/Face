package com.example.facesample.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;


public class DisplayImageView extends ImageView {

    private Paint mPaint;
    private Matrix mMatrix;
    private int mW;
    private int mH;
    private int mWidth;
    private int mHeight;
    private float cw;

    public DisplayImageView(Context context) {
        super(context);
        init();
    }

    public DisplayImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DisplayImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DisplayImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mMatrix = new Matrix();
    }

    private static final String TAG = "DisplayImageView";

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        calcMatrix(bm);
        super.setImageBitmap(bm);
    }

    private void calcMatrix(Bitmap bm) {
        mWidth = bm.getWidth();
        mHeight = bm.getHeight();
        float cw = mW * 1.0f / mWidth;
        mMatrix.setScale(cw, cw);
        setImageMatrix(mMatrix);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mW == 0) {
            cw = w * 1.0f / mWidth;
            mMatrix.setScale(cw, cw);
            setImageMatrix(mMatrix);
        }
        mW = w;
        mH = h;
    }

    public void updateBitmapScale(boolean out, float transY, float value) {
        if (out) {
            onOut(value, transY);
        } else {
            onIn(value, transY);
        }
    }

    private void onOut(float value, float transY) {
        Log.e(TAG, "onOut: "+(mHeight * cw) + " // "+((transY+value)));
        if (mHeight * cw < (transY+value)) {
            float v = (transY + value) / (mHeight * cw);
            cw = v;
            mMatrix.setScale(v, v);
            Log.e(TAG, "onOut: v");
            setImageMatrix(mMatrix);
        }
    }

    private void onIn(float value, float transY) {

        // 如果图片的伸缩宽度大于控件的宽度
        if(mWidth * cw > mW){
            float v = (value + transY) / (mHeight * cw);
            cw = v;
            mMatrix.setScale(v,v);
            setImageMatrix(mMatrix);
        }
    }
}
