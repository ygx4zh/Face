package com.example.facesample.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.facesample.R;

public class MenuDialog extends Dialog implements View.OnClickListener {

    private final String[] actions;
    private Callback cb;

    public MenuDialog(@NonNull Context context, String[] actions) {
        super(context);
        this.actions = actions;
        initView();
    }


    void initView(){
        View view = View.inflate(getContext(), R.layout.dialog_bottom_menu, null);
        view.findViewById(R.id.bmenu_tv_cancle).setOnClickListener(this);
        TextView tvSecond = (TextView) view.findViewById(R.id.bmenu_tv_second);
        tvSecond.setOnClickListener(this);
        TextView tvFirst = (TextView) view.findViewById(R.id.bmenu_tv_first);
        tvFirst.setOnClickListener(this);

        tvFirst.setText(actions[0]);
        tvSecond.setText(actions[1]);

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
            case R.id.bmenu_tv_second:    onAction(1);  break;
            case R.id.bmenu_tv_first:  onAction(0);  break;
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

        int CANCLE = - 1;
        int HIDDEN =   2;
        int DELETE =   3;

        void onAction(Dialog dialog, int index);
    }
}
