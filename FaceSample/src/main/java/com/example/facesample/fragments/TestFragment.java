package com.example.facesample.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.facesample.R;
import com.example.facesample.ui.dialogs.SelectFolderDialog;
import com.example.facesample.utils.ToastUtils;


public class TestFragment extends Fragment implements View.OnClickListener, SelectFolderDialog.Callback {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(view);

    }

    private void findView(View v){
        v.findViewById(R.id.test_btn_upload).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test_btn_upload:
                showSelectDialog();
                break;
        }
    }

    private void showSelectDialog(){
        SelectFolderDialog dialog = new SelectFolderDialog(getActivity(),this);
        dialog.show();
    }

    @Override
    public void onSelected(Dialog dialog, boolean selected, @Nullable String folderPath) {

        if(dialog.isShowing())
            dialog.dismiss();

        if(selected && !TextUtils.isEmpty(folderPath)){
            ToastUtils.show(getContext(),folderPath);
        }
    }
}
