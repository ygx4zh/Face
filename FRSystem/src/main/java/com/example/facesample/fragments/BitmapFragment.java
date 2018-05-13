package com.example.facesample.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.facesample.R;

public class BitmapFragment extends Fragment implements View.OnClickListener {

    private View mRightV;
    private View mLeftV;
    private ImageView mIv;
    private Bitmap bitmap;
    private Callback mCb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bitmap, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(view);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        if (mIv != null) {
            mIv.setImageBitmap(bitmap);
        }
    }

    private void findView(View view) {
        mIv = (ImageView) view.findViewById(R.id.bitmap_iv);
        mRightV = view.findViewById(R.id.bitmap_v_right);
        mLeftV = view.findViewById(R.id.bitmap_v_left);

        mRightV.setOnClickListener(this);
        mLeftV.setOnClickListener(this);

        if (bitmap != null) mIv.setImageBitmap(bitmap);
    }

    public void setCallback(Callback cb) {
        mCb = cb;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bitmap_v_left:
                if (mCb != null) {
                    mCb.onAction(this, true, bitmap);
                }
                break;
            case R.id.bitmap_v_right:
                if (mCb != null) {
                    mCb.onAction(this, false, bitmap);
                }
                break;
        }
    }

    public interface Callback {

        void onAction(BitmapFragment fragment, boolean useBitmap, Bitmap bitmap);
    }
}
