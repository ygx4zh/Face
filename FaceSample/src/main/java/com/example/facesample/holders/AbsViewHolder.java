package com.example.facesample.holders;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public abstract class AbsViewHolder<T> {

    private Context ctx;
    private final View mView;

    public AbsViewHolder(Context ctx){
        this.ctx = ctx;
        mView = onCreateView(LayoutInflater.from(ctx));
        mView.setTag(this);
        onViewCreated(mView);
    }

    protected Context getContext(){
        return ctx;
    }

    public View getView(){
        return mView;
    }

    protected void onViewCreated(View v){

    }
    protected abstract View onCreateView(LayoutInflater inflater);

    public abstract void bindData(T data);

}
