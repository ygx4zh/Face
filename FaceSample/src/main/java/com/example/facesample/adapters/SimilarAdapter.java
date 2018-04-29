package com.example.facesample.adapters;

import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;


import com.example.facesample.R;
import com.example.facesample.db.bean.FaceImageBean;
import com.example.facesample.holders.ImgHolder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class SimilarAdapter extends PagerAdapter {

    private List<FaceImageBean> files;
    private LinkedList<ImageView> mIvs = new LinkedList<>();

    private static final int KEY_POSITION = 1;
    private static final int KEY_DATA = 2;

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FaceImageBean tag = (FaceImageBean) v.getTag(R.id.key_data);
            int position = (int) v.getTag(R.id.key_position);
            if (onItemClickListener != null) {
                onItemClickListener.onClick(v, position, tag);
            }
        }
    };
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onClick(View view, int position, FaceImageBean bean);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public SimilarAdapter(List<FaceImageBean> files) {
        this.files = files;
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView iv = null;
        if (mIvs.size() == 0) {
            iv = new ImageView(container.getContext());
            iv.setOnClickListener(mClickListener);
        } else {
            iv = mIvs.removeFirst();
        }
        iv.setTag(R.id.key_position, position);
        FaceImageBean bean = files.get(position);
        iv.setTag(R.id.key_data, bean);
        Picasso.get().load(new File(bean.getPath())).into(iv);
        container.addView(iv);
        return iv;
    }

    private static final String TAG = "SimilarAdapter";

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ImageView contentView = (ImageView) object;
        container.removeView(contentView);
        mIvs.addLast(contentView);
    }
}
