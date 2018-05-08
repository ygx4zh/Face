package com.example.facesample.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.facesample.R;
import com.example.facesample.bean.ImgBean;
import com.example.facesample.db.bean.FaceImageBean;
import com.example.facesample.holders.ImgHolder;

import java.util.List;

public class ImgAdapter extends RecyclerView.Adapter<ImgHolder> implements ImgHolder.OnHolderLongClickListener {

    private List<FaceImageBean> imgs;
    private OnItemLongClickListener l;

    public ImgAdapter(List<FaceImageBean> imgs){
        this.imgs = imgs;
    }

    @Override
    public ImgHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImgHolder holder = new ImgHolder(View.inflate(parent.getContext(), R.layout.item_imgs, null));
        holder.setOnHolderLongClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ImgHolder holder, int position) {
        holder.bindData(imgs.get(position),position);
    }

    @Override
    public int getItemCount() {
        return imgs.size();
    }

    @Override
    public void onLongClick(View itemView, int position) {
        if (l != null) {
            l.onItemLongClick(itemView, position);
        }
    }

    public void setOnItemLongClickListener(OnItemLongClickListener l){
        this.l = l;
    }
    public interface OnItemLongClickListener{
        void onItemLongClick(View itemView,int position);
    }
}
