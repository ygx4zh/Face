package com.example.facesample.adapters;

import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;


import java.io.File;
import java.util.List;

public class SimilarAdapter extends PagerAdapter {

    private List<File> files;
    private ImageView[] mIvs = new ImageView[6];

    public SimilarAdapter(List<File> files){
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
        int i = position % mIvs.length;
        ImageView iv = mIvs[i];
        if(iv == null){
            iv = mIvs[i] = new ImageView(container.getContext());
            /*Point screenSize = AppHelper.getScreenSize(container.getContext());
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams((int) (screenSize.x * 0.7),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            iv.setLayoutParams(params);*/
        }
        Log.e(TAG, "instantiateItem: "+i + " // "+position);

        Integer tag = (Integer) iv.getTag();
        if(tag == null){
            Log.e(TAG, "instantiateItem: tag null");
        }else{
            Log.e(TAG, "instantiateItem: tag: "+i);
        }
        iv.setTag(i);
        iv.setImageBitmap(BitmapFactory.decodeFile(files.get(position).getAbsolutePath()));
        // iv.setImageResource(files.get(position));
        ViewParent parent = iv.getParent();
        Log.e(TAG, "instantiateItem: "+parent);
        if(parent == null)
            container.addView(iv);
        return iv;
    }

    private static final String TAG = "SimilarAdapter";
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Class<?> aClass = object.getClass();
        Log.e(TAG, "destroyItem: "+aClass.getSimpleName()+" // "+position + (position/mIvs.length));
        // container.removeView((ImageView) object);

        // ViewParent parent = ((ImageView) object).getParent();
        // Log.e(TAG, "destroyItem:parent: "+parent);
    }
}
