package com.example.facesample.holders;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.facesample.R;
import com.example.facesample.activities.VerifyActivity;
import com.example.facesample.bean.ImgBean;


public class ImgHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public static BitmapFactory.Options opts = new BitmapFactory.Options();
    static {
        opts.inSampleSize = 2;
    }
    private ImageView mIv;
    private TextView mTv;
    private ImgBean img;

    public ImgHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mIv = itemView.findViewById(R.id.img_iv);
        mTv = itemView.findViewById(R.id.img_tv);
    }

    public void bindData(ImgBean img){
        this.img = img;
        mIv.setImageBitmap(BitmapFactory.decodeFile(img.getImgFile().getAbsolutePath(),opts));
        mTv.setText(img.getImgFile().getName());
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), VerifyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("type",1);
        intent.putExtra("path",img.getImgFile().getAbsolutePath());
        v.getContext().startActivity(intent);
    }
}
