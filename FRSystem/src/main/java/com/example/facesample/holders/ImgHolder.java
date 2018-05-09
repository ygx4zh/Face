package com.example.facesample.holders;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.facesample.R;
import com.example.facesample.activities.VerifyActivity;
import com.example.facesample.bean.ImgBean;
import com.example.facesample.db.bean.FaceImageBean;
import com.squareup.picasso.Picasso;

import java.io.File;


public class ImgHolder extends RecyclerView.ViewHolder implements  View.OnLongClickListener {
    public static BitmapFactory.Options opts = new BitmapFactory.Options();
    static {
        opts.inSampleSize = 2;
    }
    private ImageView mIv;
    private ImageView mIvSmile;
    private TextView mTv;
    private FaceImageBean img;
    private int position;
    private OnHolderLongClickListener listener;

    public ImgHolder(View itemView) {
        super(itemView);
        itemView.setOnLongClickListener(this);
        mIv = itemView.findViewById(R.id.img_iv);
        mTv = itemView.findViewById(R.id.img_tv);
        mIvSmile = itemView.findViewById(R.id.img_iv_smile);
    }

    public void bindData(FaceImageBean img, int position){
        this.position = position;
        this.img = img;
        Picasso.get().load(new File(img.getPath()))

                .into(mIv);
        mTv.setText(img.getFname());
        mIvSmile.setImageResource(
                TextUtils.isEmpty(img.getFace_token()) ? R.mipmap.ic_smile_g:R.mipmap.ic_smile_a);
    }

    @Override
    public boolean onLongClick(View v) {
        if (listener != null) {
            listener.onLongClick(v,position, img);
        }
        return true;
    }

    public void setOnHolderLongClickListener(OnHolderLongClickListener listener){
        this.listener = listener;
    }

    public interface OnHolderLongClickListener{
        void onLongClick(View itemView, int position, FaceImageBean obj);
    }
}
