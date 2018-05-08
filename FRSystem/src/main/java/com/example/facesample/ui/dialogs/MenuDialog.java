package com.example.facesample.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;

import com.example.facesample.R;

public class MenuDialog extends Dialog implements View.OnClickListener {

    private Callback cb;

    public MenuDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    public MenuDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    protected MenuDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    void initView(){
        View view = View.inflate(getContext(), R.layout.dialog_bottom_menu, null);
        view.findViewById(R.id.bmenu_tv_cancle).setOnClickListener(this);
        view.findViewById(R.id.bmenu_tv_hide).setOnClickListener(this);
        view.findViewById(R.id.bmenu_tv_delete).setOnClickListener(this);

        setContentView(view);

        setWindow();
    }

    void setWindow(){
        getWindow().setGravity(Gravity.BOTTOM);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        getWindow().setWindowAnimations(R.style.BottomDialog_Animation);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bmenu_tv_cancle:  onAction(Callback.CANCLE);  break;
            case R.id.bmenu_tv_hide:    onAction(Callback.HIDDEN);  break;
            case R.id.bmenu_tv_delete:  onAction(Callback.DELETE);  break;
        }
    }

    void onAction(int action){



        if(action == Callback.CANCLE){
            dismiss();
        }else{
            if (cb != null) {
                cb.onAction(this, action);
            }
        }

    }

    public void setCallback(Callback cb){
        this.cb = cb;
    }

    public interface Callback{

        int CANCLE = 1;
        int HIDDEN = 2;
        int DELETE = 3;

        void onAction(Dialog dialog, int action);
    }
}
