package com.ty.phoneguardian.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ty.phoneguardian.bean.ConstentValue;
import com.ty.phoneguardian.bean.ProcessInfo;
import com.ty.phoneguardian.Engine.ProgressInfoProvider;
import com.ty.phoneguardian.R;
import com.ty.phoneguardian.Utils.SpUtils;
import com.ty.phoneguardian.Utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProgressManagerActivity extends AppCompatActivity implements View.OnClickListener {
    @InjectView(R.id.tv_progress_total)
    TextView tvProgressTotal;
    @InjectView(R.id.tv_residue_memory)
    TextView tvResidueMemory;
    @InjectView(R.id.lv_progress_show)
    ListView lv_progress_show;
    @InjectView(R.id.progress_all)
    Button progressAll;
    @InjectView(R.id.progress_re_all)
    Button progressReAll;
    @InjectView(R.id.progress_clear)
    Button progressClear;
    @InjectView(R.id.progress_setting)
    Button progressSetting;
    @InjectView(R.id.tv_progress_des)
    TextView tvProgressDes;
    private int mProcessCount;
    private List<ProcessInfo> mProcessList;
    private List<ProcessInfo> mSysList;
    private List<ProcessInfo> mCurList;
    private MyAdapter adapter;
    private ProcessInfo mProcessInfo;
    private long mAvailSpace;
    private String mTotalSpaceStr;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter = new MyAdapter();
            lv_progress_show.setAdapter(adapter);
            if (tvProgressDes != null && mCurList != null) {
                tvProgressDes.setText("用户进程(" + mCurList.size() + ")");
            }
        }
    };

    private class MyAdapter extends BaseAdapter {
        //获取数据适配器中条目类型的总数，修改成两种(纯文本，图片+文字)
        @Override
        public int getViewTypeCount() {
            //条目类型数（这里有两种类型）
            return super.getViewTypeCount() + 1;
        }

        //指定索引指向的条目类型，条目类型指定(0（复用系统），1）
        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCurList.size() + 1) {
                //返回0，表示纯文本的状态码
                return 0;
            } else {
                //返回1，表示图片+文字的状态码
                return 1;
            }
        }

        //listView 中添加了两个描述栏
        //刷新adapter就可以再次执行这个方法
        @Override
        public int getCount() {
            if (SpUtils.getBoolean(getApplicationContext(), ConstentValue.SHOW_SYSTEM, false)) {
                return mSysList.size() + mCurList.size() + 2;
            } else {
                return mCurList.size() + 1;
            }
        }

        @Override
        public ProcessInfo getItem(int i) {
            if (i == 0 || i == mCurList.size() + 1) {
                return null;
            } else {
                if (i < mCurList.size() + 1) {
                    return mCurList.get(i - 1);
                } else {
                    //返回系统进程对应的条目
                    return mSysList.get(i - mCurList.size() - 2);
                }
            }
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            int type = getItemViewType(i);
            if (type == 0) {
                //展示灰色纯文本条目
                //展示图片+文字条目
                View v = null;
                ViewTitleHolder mViewTitle = null;
                if (view == null) {
                    v = View.inflate(getApplicationContext(), R.layout.show_app_title, null);
                    mViewTitle = new ViewTitleHolder();
                    mViewTitle.tv_show_app = (TextView) v.findViewById(R.id.tv_show_app);
                    v.setTag(mViewTitle);
                } else {
                    v = view;
                    mViewTitle = (ViewTitleHolder) v.getTag();
                }
                if (i == 0) {
                    mViewTitle.tv_show_app.setText("用户进程(" + mCurList.size() + ")");
                } else {
                    mViewTitle.tv_show_app.setText("系统进程(" + mSysList.size() + ")");
                }
                return v;
            } else {
                //展示图片+文字条目
                View v = null;
                ViewHolder mViewHolder = null;
                if (view == null) {
                    v = View.inflate(getApplicationContext(), R.layout.show_progress_item, null);
                    mViewHolder = new ViewHolder();
                    mViewHolder.tv_progress_name = (TextView) v.findViewById(R.id.tv_progress_name);
                    mViewHolder.tv_progress_memory = (TextView) v.findViewById(R.id.tv_progress_memory);
                    mViewHolder.iv_progress_icon = (ImageView) v.findViewById(R.id.iv_progress_icon);
                    mViewHolder.cb_progress_box = (CheckBox) v.findViewById(R.id.cb_progress_box);
                    v.setTag(mViewHolder);
                } else {
                    v = view;
                    mViewHolder = (ViewHolder) v.getTag();
                }
                mViewHolder.iv_progress_icon.setBackgroundDrawable(getItem(i).getIcon());
                mViewHolder.tv_progress_name.setText(getItem(i).getName());
                mViewHolder.tv_progress_memory.setText(Formatter.formatFileSize(getApplicationContext(), getItem(i).getMemSize()));
                //本应用不能被选中，要把cb_progress_box隐藏
                if (getItem(i).getPackageName().equals(getPackageName())) {
                    mViewHolder.cb_progress_box.setVisibility(View.GONE);
                } else {
                    mViewHolder.cb_progress_box.setVisibility(View.VISIBLE);
                }
                //选中或者不选中
                mViewHolder.cb_progress_box.setChecked(getItem(i).isCheck());
                return v;
            }
        }
    }


    private static class ViewHolder {
        private TextView tv_progress_name;
        private TextView tv_progress_memory;
        private ImageView iv_progress_icon;
        private CheckBox cb_progress_box;
    }

    private static class ViewTitleHolder {
        private TextView tv_show_app;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_manager);
        ButterKnife.inject(this);
        initBut();
        initData();
        initListViewData();
    }

    /**
     * 初始化ListView中的数据
     */
    private void initListViewData() {
        getData();
        lv_progress_show.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //滚动过程中调用方法
                //AbsListView中view就是listView对象
                //firstVisibleItem第一个可见条目索引值
                //visibleItemCount当前一个屏幕的可见条目数
                //总共条目总数
                if (mCurList != null && mSysList != null) {
                    if (firstVisibleItem >= mCurList.size() + 1) {
                        //滚动到了系统条目
                        tvProgressDes.setText("系统进程(" + mSysList.size() + ")");
                    } else {
                        //滚动到了用户应用条目
                        tvProgressDes.setText("用户进程(" + mCurList.size() + ")");
                    }
                }
            }
        });

        lv_progress_show.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0 || i == mCurList.size() + 1) {
                    return;
                } else {
                    if (i < mCurList.size() + 1) {
                        mProcessInfo = mCurList.get(i - 1);
                    } else {
                        //返回系统进程对应的条目
                        mProcessInfo = mSysList.get(i - mCurList.size() - 2);
                    }
                    if (mProcessInfo != null) {
                        if (!mProcessInfo.getPackageName().equals(getPackageName())) {
                            //选中的条目指向的对象和本应用包名不一致的时候，才修改单选框的状态
                            //cb_progress_box 用来显示选中状态
                            mProcessInfo.isCheck = !mProcessInfo.isCheck;
                            //通过选中的条目的view,来找到cb_progress_box
                            CheckBox cb_progress_box = (CheckBox) view.findViewById(R.id.cb_progress_box);
                            cb_progress_box.setChecked(mProcessInfo.isCheck);
                        }
                    }
                }
            }
        });
    }

    public void getData() {
        new Thread() {
            public void run() {
                mProcessList = ProgressInfoProvider.getPrgressInfo(getApplicationContext());
                mSysList = new ArrayList<ProcessInfo>();
                mCurList = new ArrayList<ProcessInfo>();
                for (ProcessInfo pInfo : mProcessList) {
                    if (pInfo.isSystem()) {
                        //系统进程
                        mSysList.add(pInfo);
                    } else {
                        //非系统进程
                        mCurList.add(pInfo);
                    }
                }
                mHandler.sendEmptyMessage(1);
            }
        }.start();
    }

    /**
     * 初始化进程个数和剩余空间
     */
    private void initData() {
        mProcessCount = ProgressInfoProvider.getProgressCount(this);
        tvProgressTotal.setText("进程总数: " + mProcessCount);

        //获取可用内存大小，并格式化
        mAvailSpace = ProgressInfoProvider.getAvailSpace(this);
        String availSpaceStr = Formatter.formatFileSize(this, mAvailSpace);
        //获取总内存大小，并格式化
        long totalSpace = ProgressInfoProvider.getTotalSpace(this);
        mTotalSpaceStr = Formatter.formatFileSize(this, totalSpace);
        tvResidueMemory.setText("剩余/总共: " + availSpaceStr + "/" + mTotalSpaceStr);
    }

    private void initBut() {
        progressAll.setOnClickListener(this);
        progressReAll.setOnClickListener(this);
        progressClear.setOnClickListener(this);
        progressSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.progress_all:
                //全选
                selectAll();
                break;
            case R.id.progress_re_all:
                //反选
                selectReverse();
                break;
            case R.id.progress_clear:
                //一键清理
                clearAll();
                break;
            case R.id.progress_setting:
                //设置
                Intent intent = new Intent(getApplicationContext(), ProgressSettingActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //通知刷新Adapter,根据返回值（这里是0）处理
        if (adapter != null) {
            //刷新后就可以决定显示的条目数
            adapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 清理选中进程
     */
    private void clearAll() {
        //2.创建一个需要杀死的进程的集合
        List<ProcessInfo> killProgressInfo = new ArrayList<ProcessInfo>();
        //1.获取选中的进程
        for (ProcessInfo info : mCurList) {
            if (info.getPackageName().equals(getPackageName())) {
                continue;
            }
            if (info.isCheck) {
                //不能在集合循环过程中，移除集合中的对象（否则集合安全就会出现危机）
                //mCurList.remove(info);
                //3.记录要杀死的用户进程
                killProgressInfo.add(info);
            }
        }
        for (ProcessInfo info : mSysList) {
            if (info.isCheck) {
                //4.记录要杀死的系统进程
                killProgressInfo.add(info);
            }
        }
        //5.遍历需要杀死的进程的集合
        int releaseSpace = 0;
        for (ProcessInfo info : killProgressInfo) {
            //6.如果系统与用户进程集合中包含要杀死的进程，则移除进程
            if (mCurList.contains(info)) {
                mCurList.remove(info);
            }
            if (mSysList.contains(info)) {
                mSysList.remove(info);
            }
            //7.杀死继承
            ProgressInfoProvider.killProgress(getApplicationContext(), info);
            releaseSpace += info.getMemSize();
        }

        //8.通知adapter刷新数据
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        //9.进程总数更新
        mProcessCount -= killProgressInfo.size();
        //10.更新可用剩余空间（原有剩余空间+释放空间）
        mAvailSpace += releaseSpace;
        //11.更新进程总数
        tvProgressTotal.setText("进程总数: " + mProcessCount);
        tvResidueMemory.setText("剩余/总共: " + Formatter.formatFileSize(getApplicationContext(), mAvailSpace) + "/" + mTotalSpaceStr);
        //12.弹吐司告诉用户释放了多少空间，杀死了多少进程
        String spaceRelease = Formatter.formatFileSize(getApplicationContext(), releaseSpace);
        ToastUtils.show(getApplicationContext(), String.format("杀死了%d进程,释放了%s空间", killProgressInfo.size(), spaceRelease));
    }

    /**
     * 反选
     */
    private void selectReverse() {
        //1.将集合中所有对象的ischick对象设置为true
        for (ProcessInfo info : mCurList) {
            if (info.getPackageName().equals(getPackageName())) {
                continue;  //结束本次循环
            }
            info.isCheck = !info.isCheck;
        }
        for (ProcessInfo info : mSysList) {
            info.isCheck = !info.isCheck;
        }
        //2.通知adapter刷新UI
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 全选
     */
    private void selectAll() {
        //1.将集合中所有对象的ischick对象设置为true
        for (ProcessInfo info : mCurList) {
            if (info.getPackageName().equals(getPackageName())) {
                continue;  //结束本次循环
            }
            info.isCheck = true;
        }
        for (ProcessInfo info : mSysList) {
            info.isCheck = true;
        }
        //2.通知adapter刷新UI
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
