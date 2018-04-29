package com.example.facesample.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.facesample.R;
import com.example.facesample.bean.ImgBean;
import com.example.facesample.db.bean.FaceImageBean;
import com.example.facesample.holders.ImgHolder;

import java.util.List;

public class ImgAdapter extends RecyclerView.Adapter<ImgHolder> {

    private List<FaceImageBean> imgs;

    public ImgAdapter(List<FaceImageBean> imgs){
        this.imgs = imgs;
    }

    @Override
    public ImgHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImgHolder(View.inflate(parent.getContext(), R.layout.item_imgs,null));
    }

    @Override
    public void onBindViewHolder(ImgHolder holder, int position) {
        holder.bindData(imgs.get(position));
    }

    @Override
    public int getItemCount() {
        return imgs.size();
    }
}
