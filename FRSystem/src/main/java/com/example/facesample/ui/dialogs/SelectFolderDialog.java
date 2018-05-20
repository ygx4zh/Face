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

import com.example.facesample.utils.AppHelper;
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
 *
 * 长按选中文件夹, 点击则是进入该目录
 *
 * 要先选中文件夹, 才能点OK
 *
 */

public class SelectFolderDialog extends Dialog implements
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener,
        View.OnClickListener {

    /**
     * 回调对象
     */
    private final Callback cb;
    /**
     * 显示文件夹列表的listview
     */
    private ListView mLv;

    /**
     * 当前展示的文件夹目录
     */
    private File mCurDir = Environment.getExternalStorageDirectory();

    /**
     * 展示文件夹列表ListView的适配器
     */
    private FolderAdapter mAdapter;

    /**
     * 显示路径导航的控件
     */
    private LinearLayout mPathGuide;

    /**
     * 路径树的树根
     */
    private Node<File> mPathTreeRoot;

    /**
     * 取消按键
     */
    private View mBtnCancle;

    /**
     * OK按键
     */
    private View mBtnOk;

    /**
     * 长按选中的文件夹条目索引
     */
    private int mLastSelectPosition = -1;


    public SelectFolderDialog(@NonNull Context context, Callback cb) {
        super(context);
        this.cb = cb;

        // 初始化要显示的控件
        initView();

        // 创建一个路径根节点对象
        mPathTreeRoot = new Node<>(mCurDir);

        // 加载当前文件夹路径下的文件
        loadFiles(mCurDir);
    }


    /**
     * 初始化控件
     */
    private void initView() {
        initDialogSize();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_folder, null);
        setContentView(view);
        findView(view);
    }

    /**
     * 导入当前
     * @param parent
     */
    private void loadFiles(File parent) {

        // 将选中的文件夹索引置为-1,
        mLastSelectPosition = -1;

        // 将OK按键置为不可点击状态
        switchOkButtonEnable(false);

        // 获取当前文件夹下的子文件及子文件夹
        File[] files = parent.listFiles();

        // 排序文件,
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o2.getName().compareTo(o1.getName());
            }
        });

        // 清空显示中的文件夹集合
        mFiles.clear();

        // 将当前文件夹下的文件夹导入到集合中
        for (File f : files) {
            if (f.isDirectory())
                mFiles.add(new FileBean(f, false));
        }

        // 更新路径导航显示
        updatePathView();

        // 刷新显示的数据
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 更新显示导航路径的控件;
     *
     */
    private void updatePathView() {
        // 先移除原有的路径控件
        mPathGuide.removeAllViews();

        // 拿到文件夹目录根节点
        Node<File> cur = mPathTreeRoot;

        /*
         * 假如路径为: 0 / yunzhixun / image
         * 其中 0 和 yunxzhixun 为蓝色, 可点击状态, 且0后有 / 分割符
         *
         * image为灰色状态, 不可点击, 表示的是当前正展示的目录;
         *
         * 这里的 0 和 yunzhixun 和 image 用Node对象来表示,
         * Node对象表示一个节点, 具有三个属性: 上一级文件夹pre, 下一级文件夹next, 自己所表示的文件路径value
         *
         * 例如yunzhixun的pre为 0 , next为image, 只要next不为null, 字体要显蓝色, 并且后面要跟 / 路径分割符,
         * 并给yunzhixun添加对应的点击事件
         *
         * 下面while循环就是做的这个操作;
         */
        while (cur.getNext() != null) {
            // 添加路径中表示文件夹的控件
            TextView v = getParentalPathView(cur);
            mPathGuide.addView(v);
            // 添加分隔符
            TextView spliteView = getSpliteView();
            mPathGuide.addView(spliteView);
            cur = cur.getNext();
        }

        // 添加表示当前展示的文件夹的控件
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

        mPathGuide = (LinearLayout) v.findViewById(R.id.folder_ll_pathGuide);
        mBtnCancle = v.findViewById(R.id.folder_tv_cancle);


        mBtnOk = v.findViewById(R.id.folder_tv_ok);

        mLv = (ListView) v.findViewById(R.id.folder_lv);
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
        /**
         * 当文件夹被选中的时候;
         *
         * @param dialog 对话框
         * @param selected true, 文件夹被选中
         * @param folderPath 文件夹的路径; selected为false时为null
         */
        void onSelected(Dialog dialog, boolean selected, @Nullable String folderPath);
    }
}
