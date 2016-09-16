package com.ty.phoneguardian.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ty.phoneguardian.bean.AppInfo;
import com.ty.phoneguardian.Engine.AppInfoProvider;
import com.ty.phoneguardian.R;
import com.ty.phoneguardian.Utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AppManagerActivity extends Activity implements View.OnClickListener {
    @InjectView(R.id.lv_show_app)
    ListView lvShowApp;
    @InjectView(R.id.tv_memory)
    TextView tvMemory;
    @InjectView(R.id.tv_sd_memory)
    TextView tvSdMemory;
    @InjectView(R.id.tv_show_des)
    TextView tvShowDes;
    private List<AppInfo> appList = null;
    private List<AppInfo> mSysList = null;  //系统应用集合
    private List<AppInfo> mCurList = null;  //用户应用集合
    private MyAdapter adapter;
    private AppInfo mAppinfo;
    private PopupWindow mPw;

    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter = new MyAdapter();
            lvShowApp.setAdapter(adapter);
            if (tvShowDes != null && mCurList != null) {
                tvShowDes.setText("用户应用(" + mCurList.size() + ")");
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
        @Override
        public int getCount() {
            return mSysList.size() + mCurList.size() + 2;
        }

        @Override
        public AppInfo getItem(int i) {
            if (i == 0 || i == mCurList.size() + 1) {
                return null;
            } else {
                if (i < mCurList.size() + 1) {
                    return mCurList.get(i - 1);
                } else {
                    //返回系统应用对应的条目
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
                    mViewTitle.tv_show_app.setText("用户应用(" + mCurList.size() + ")");
                } else {
                    mViewTitle.tv_show_app.setText("系统应用(" + mSysList.size() + ")");
                }
                return v;
            } else {
                //展示图片+文字条目
                View v = null;
                ViewHolder mViewHolder = null;
                if (view == null) {
                    v = View.inflate(getApplicationContext(), R.layout.show_app_item, null);
                    mViewHolder = new ViewHolder();
                    mViewHolder.tv_app_name = (TextView) v.findViewById(R.id.tv_app_name);
                    mViewHolder.tv_path = (TextView) v.findViewById(R.id.tv_path);
                    mViewHolder.iv_app_icon = (ImageView) v.findViewById(R.id.iv_app_icon);
                    v.setTag(mViewHolder);
                } else {
                    v = view;
                    mViewHolder = (ViewHolder) v.getTag();
                }
                mViewHolder.iv_app_icon.setBackgroundDrawable(getItem(i).getIcon());
                mViewHolder.tv_app_name.setText(getItem(i).getName());
                if (getItem(i).isSdCard()) {
                    mViewHolder.tv_path.setText("sd卡应用");
                } else {
                    mViewHolder.tv_path.setText("手机应用");
                }
                return v;
            }
        }
    }

    private static class ViewHolder {
        private TextView tv_app_name;
        private TextView tv_path;
        private ImageView iv_app_icon;
    }

    private static class ViewTitleHolder {
        private TextView tv_show_app;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        ButterKnife.inject(this);
        initTile();
        initList();
    }

    /**
     * 初始化集合
     */
    private void initList() {
        new Thread() {
            @Override
            public void run() {
                appList = AppInfoProvider.getAppInfoList(getApplicationContext());
                mSysList = new ArrayList<AppInfo>();
                mCurList = new ArrayList<AppInfo>();
                for (AppInfo info : appList) {
                    if (info.isSystem()) {
                        //系统应用
                        mSysList.add(info);
                    } else {
                        //非系统应用
                        mCurList.add(info);
                    }
                }
                mHanlder.sendEmptyMessage(1);
            }
        }.start();
        lvShowApp.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                        tvShowDes.setText("系统应用(" + mSysList.size() + ")");
                    } else {
                        //滚动到了用户应用条目
                        tvShowDes.setText("用户应用(" + mCurList.size() + ")");
                    }
                }
            }
        });
        lvShowApp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0 || i == mCurList.size() + 1) {
                    return;
                } else {
                    if (i < mCurList.size() + 1) {
                        mAppinfo = mCurList.get(i - 1);
                    } else {
                        //返回系统应用对应的条目
                        mAppinfo = mSysList.get(i - mCurList.size() - 2);
                    }
                    showPopupWindow(view);
                }
            }
        });
    }

    private void showPopupWindow(View view) {
        View v = View.inflate(this, R.layout.popupwindow_layout, null);
        TextView tv_app_uninstall = (TextView) v.findViewById(R.id.tv_app_uninstall);
        TextView tv_app_start = (TextView) v.findViewById(R.id.tv_app_start);
        TextView tv_app_shall = (TextView) v.findViewById(R.id.tv_app_shall);
        tv_app_uninstall.setOnClickListener(this);
        tv_app_shall.setOnClickListener(this);
        tv_app_start.setOnClickListener(this);
        //由透明 -->  不透明
        AlphaAnimation aa = new AlphaAnimation(0, 1);
        aa.setDuration(1000);
        aa.setFillAfter(true);
        //缩放动画
        ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(1000);
        sa.setFillAfter(true);

        //动画集合
        AnimationSet as = new AnimationSet(true);
        as.addAnimation(aa);
        as.addAnimation(sa);

        //true 表示可以获得焦点
        //1.创建窗体对象
        mPw = new PopupWindow(v, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        //2.设置一个透明背景(目的是为了可以回退)
        mPw.setBackgroundDrawable(new ColorDrawable());
        //3.指定窗体的位置
        //第一个参数：挂载在哪个view上    第二/三个参数：x与y轴的偏移量
        mPw.showAsDropDown(view, 100, -view.getHeight());
        //4,popupView执行动画
        v.startAnimation(as);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_app_uninstall:
                //卸载应用
                if (mAppinfo.isSystem()) {
                    ToastUtils.show(getApplicationContext(), "此应用不能卸载");
                } else {
                    Intent intent = new Intent("android.intent.action.DELETE");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:" + mAppinfo.getPackageName()));
                    startActivity(intent);
                }
                break;
            case R.id.tv_app_start:
                //通过桌面去启动指定包名应用
                PackageManager pm = getPackageManager();
                //通过Launch开启制定包名的意图,去开启应用
                Intent launchIntentForPackage = pm.getLaunchIntentForPackage(mAppinfo.getPackageName());
                if (launchIntentForPackage != null) {
                    startActivity(launchIntentForPackage);
                } else {
                    ToastUtils.show(getApplicationContext(), "此应用不能被开启");
                }
                break;
            //分享(第三方(微信,新浪,腾讯)平台),智慧北京
            //拍照-->分享:将图片上传到微信服务器,微信提供接口api,推广
            //查看朋友圈的时候:从服务器上获取数据(你上传的图片)
            case R.id.tv_app_shall:
                //通过短信应用,向外发送短信
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "分享一个应用,应用名称为" + mAppinfo.getName());
                intent.setType("text/plain");
                startActivity(intent);
                break;
        }
        //点击窗体后使窗体消失
        if (mPw != null) {
            mPw.dismiss();
        }
    }

    /**
     * 获取sd卡和内存大小
     */
    private void initTile() {
        //1.获取内存路径
        String path = Environment.getDataDirectory().getAbsolutePath();
        //2.获取sd卡的路径
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //3.获取两个路径的可以空间大小
        String memoryAvailSpace = Formatter.formatFileSize(this, getAvailSpace(path));
        String sdMemoryAvailSpace = Formatter.formatFileSize(this, getAvailSpace(sdPath));
        tvMemory.setText("磁盘可用: " + memoryAvailSpace);
        tvSdMemory.setText("SD卡可用: " + sdMemoryAvailSpace);
    }

    /**
     * 返回的结果为byte = 8bit，所以long的十进制不用除以8
     *
     * @param path 路径
     * @return 返回可用空间的大小
     */
    private long getAvailSpace(String path) {
        //1.获取可用磁盘大小的类
        StatFs statFs = new StatFs(path);
        //2.获取用区块个数
        long count = statFs.getAvailableBlocks();
        //3.获取区块大小
        long size = statFs.getBlockSize();
        //4.获取磁盘可用空间大小
        return count * size;
    }

    //重新获取焦点，刷新界面
    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    public void getData() {
        new Thread() {
            public void run() {
                appList = AppInfoProvider.getAppInfoList(getApplicationContext());
                mSysList = new ArrayList<AppInfo>();
                mCurList = new ArrayList<AppInfo>();
                for (AppInfo info : appList) {
                    if (info.isSystem()) {
                        //系统应用
                        mSysList.add(info);
                    } else {
                        //非系统应用
                        mCurList.add(info);
                    }
                }
                mHanlder.sendEmptyMessage(1);
            }
        }.start();
    }
}
