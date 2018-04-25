package com.example.facesample.holders;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.facesample.R;
import com.example.facesample.bean.FileBean;

public class FolderHolder extends AbsViewHolder<FileBean> {

    private TextView mTvName;
    private View mFl;

    public FolderHolder(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.item_folder, null);
    }

    @Override
    protected void onViewCreated(View v) {
        mTvName = v.findViewById(R.id.ifolder_tv_name);
        mFl = v.findViewById(R.id.ifolder_ll);
    }

    @Override
    public void bindData(FileBean data) {
        mTvName.setText(data.getFile().getName());
        if (data.isSelected())
            mFl.setBackgroundColor(0x66000000);
        else
            mFl.setBackgroundColor(0x00000000);
    }
}
