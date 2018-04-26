package com.example.facesample.holders;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.facesample.R;
import com.example.facesample.bean.ImgBean;


public class ImgHolder extends RecyclerView.ViewHolder {
    private static BitmapFactory.Options opts = new BitmapFactory.Options();
    static {
        opts.inSampleSize = 3;
    }
    private ImageView mIv;
    private TextView mTv;

    public ImgHolder(View itemView) {
        super(itemView);
        mIv = itemView.findViewById(R.id.img_iv);
        mTv = itemView.findViewById(R.id.img_tv);
    }

    public void bindData(ImgBean img){
        mIv.setImageBitmap(BitmapFactory.decodeFile(img.getImgFile().getAbsolutePath(),opts));
        mTv.setText(img.getImgFile().getName());
    }
}
