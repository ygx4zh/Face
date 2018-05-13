package com.example.facesample.ui.dialogs;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.example.facesample.R;
import com.vondear.rxtools.view.progressing.style.ThreeBounce;


public class LoadingDialog extends BaseDialog {
    public LoadingDialog(@NonNull Context context) {
        super(context);
        initDialogSize();
        initView();
    }

    private void initView(){

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_loading, null);
        ProgressBar mPb = (ProgressBar) view.findViewById(R.id.loading_pb);
        mPb.setIndeterminateDrawable(new ThreeBounce());
        setContentView(view);

        setCancelable(false);
    }

    private void initDialogSize() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = getContext().getResources().getDisplayMetrics(); // 获取屏幕宽、高用

        lp.width = (int) (d.widthPixels * 0.8); // 宽度设置为屏幕的0.8
        lp.height = (int) (d.heightPixels * 0.8); // 宽度设置为屏幕的0.8
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        lp.gravity = Gravity.CENTER;
        lp.alpha = 1f;
        dialogWindow.setAttributes(lp);
    }
}
