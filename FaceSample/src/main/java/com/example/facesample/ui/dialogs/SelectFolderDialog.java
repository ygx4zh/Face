package com.example.facesample.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.facesample.AppHelper;
import com.example.facesample.R;
import com.example.facesample.adapters.FolderAdapter;
import com.example.facesample.bean.FileBean;
import com.example.facesample.engine.imgscan.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 选择文件夹对话框
 */

public class SelectFolderDialog extends Dialog implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener {

    private final Callback cb;
    private ListView mLv;

    private File mLoadPath = Environment.getExternalStorageDirectory();
    private FolderAdapter mAdapter;
    private LinearLayout mPathGuide;

    /**
     * 路径树的树根
     */
    private Node<File> mPathTreeRoot;
    private View mBtnCancle;
    private View mBtnOk;


    public SelectFolderDialog(@NonNull Context context, Callback cb) {
        super(context);
        this.cb = cb;
        initView();
    }


    private void initView() {
        initDialogSize();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_folder, null);
        setContentView(view);
        findView(view);

        mPathTreeRoot = new Node<>(mLoadPath);
        loadFiles(mLoadPath);
    }

    private void loadFiles(File parent) {
        mLastSelectPosition = -1;
        switchOkButtonEnable(false);
        File[] files = parent.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o2.getName().compareTo(o1.getName());
            }
        });
        mFiles.clear();
        for (File f : files) {
            if (f.isDirectory())
                mFiles.add(new FileBean(f, false));
        }
        addPathView();
        mAdapter.notifyDataSetChanged();
    }

    private void addPathView() {
        // 先移除所有
        mPathGuide.removeAllViews();
        Node<File> cur = mPathTreeRoot;
        while (cur.getNext() != null) {
            TextView v = getParentalPathView(cur);
            mPathGuide.addView(v);
            TextView spliteView = getSpliteView();
            mPathGuide.addView(spliteView);
            cur = cur.getNext();
        }


        mPathGuide.addView(getActivePathView(cur.getValue().getName()));
    }

    private TextView mActivePathView;

    private TextView getActivePathView(String name) {
        if (mActivePathView == null) {
            mActivePathView = new TextView(getContext());
            mActivePathView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        }
        mActivePathView.setText(name);
        return mActivePathView;
    }

    private LinearLayout.LayoutParams mSpliteViewParams =
            new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

    private TextView getSpliteView() {
        TextView tv = new TextView(getContext());
        tv.setText("/");
        int _5dp = AppHelper.dp2px(getContext(), 5);
        mSpliteViewParams.leftMargin = _5dp;
        mSpliteViewParams.rightMargin = _5dp;
        tv.setLayoutParams(mSpliteViewParams);
        tv.setTextColor(0x99000000);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        return tv;
    }

    private TextView getParentalPathView(Node<File> pathNode) {
        TextView tv = new TextView(getContext());
        tv.setText(pathNode.getValue().getName());
        tv.setTag(pathNode);
        tv.setTextColor(0xFF3f95d5);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tv.setOnClickListener(mPathNodeClickListener);
        return tv;
    }

    private List<FileBean> mFiles = new ArrayList<>();

    private void findView(View v) {

        mPathGuide = v.findViewById(R.id.folder_ll_pathGuide);
        mBtnCancle = v.findViewById(R.id.folder_tv_cancle);


        mBtnOk = v.findViewById(R.id.folder_tv_ok);

        mLv = v.findViewById(R.id.folder_lv);
        mBtnCancle.setOnClickListener(this);
        mBtnOk.setOnClickListener(this);
        mLv.setOnItemClickListener(this);
        mLv.setOnItemLongClickListener(this);
        mAdapter = new FolderAdapter(mFiles);
        mLv.setAdapter(mAdapter);

    }

    private View.OnClickListener mPathNodeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Node<File> node = null;
            try {
                node = (Node<File>) v.getTag();
            } catch (ClassCastException e) {
                Log.d(TAG, "onClick this click callback onle use for Path Node TextView");
                return;
            }

            if (node == null) {
                Log.d(TAG, "not bind tag");
                return;
            }

            node.setNext(null);
            File cur = node.getValue();
            loadFiles(cur);
        }
    };

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

    private static final String TAG = "SelectFolderDialog";

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File file = mFiles.get(position).getFile();
        mPathTreeRoot.append2Link(new Node<File>(file));
        loadFiles(file);
    }

    private int mLastSelectPosition = -1;

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        if (mLastSelectPosition == position) return true;

        if (mLastSelectPosition >= 0 && mLastSelectPosition < mFiles.size()) {
            mFiles.get(mLastSelectPosition).setSelected(false);
        }
        mFiles.get(position).setSelected(true);
        mAdapter.notifyDataSetChanged();
        mLastSelectPosition = position;
        switchOkButtonEnable(true);
        return true;
    }

    private void switchOkButtonEnable(boolean enable) {
        mBtnOk.setEnabled(enable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.folder_tv_cancle:
                if (cb != null) {
                    cb.onSelected(this, false, null);
                }
                break;
            case R.id.folder_tv_ok:
                if (cb != null) {
                    cb.onSelected(this, true, mFiles.get(mLastSelectPosition).getFile().getAbsolutePath());
                }
                break;
        }
    }

    public interface Callback {
        void onSelected(Dialog dialog, boolean selected, @Nullable String folderPath);
    }
}
