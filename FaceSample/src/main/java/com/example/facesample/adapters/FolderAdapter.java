package com.example.facesample.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.facesample.bean.FileBean;
import com.example.facesample.holders.FolderHolder;

import java.io.File;
import java.util.List;


public class FolderAdapter extends BaseAdapter {

    private List<FileBean> mFolders;

    public FolderAdapter(List<FileBean> folders){
        this.mFolders = folders;
    }

    @Override
    public int getCount() {
        return mFolders.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FolderHolder holder = null;
        if(convertView == null){
            holder = new FolderHolder(parent.getContext());
        }else{
            holder = (FolderHolder) convertView.getTag();
        }

        holder.bindData(mFolders.get(position));

        return holder.getView();
    }
}
