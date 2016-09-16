package com.ty.phoneguardian.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ty.phoneguardian.Dao.AppLockDao;
import com.ty.phoneguardian.Engine.AppInfoProvider;
import com.ty.phoneguardian.R;
import com.ty.phoneguardian.bean.AppInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AppLockActivity extends AppCompatActivity {
    @InjectView(R.id.but_app_unlock)
    Button butAppUnlock;
    @InjectView(R.id.but_app_lock)
    Button butAppLock;
    @InjectView(R.id.tv_unlock)
    TextView tvUnlock;
    @InjectView(R.id.lv_unlock)
    ListView lvUnlock;
    @InjectView(R.id.ll_unlock)
    LinearLayout llUnlock;
    @InjectView(R.id.tv_lock)
    TextView tvLock;
    @InjectView(R.id.lv_lock)
    ListView lvLock;
    @InjectView(R.id.ll_lock)
    LinearLayout llLock;
    private List<AppInfo> mAppInfo;
    private List<AppInfo> mUnLockList;
    private List<AppInfo> mLockList;
    private MyAdapter mLockAdapter;
    private MyAdapter mUnLockAdapter;
    private AppLockDao mDao;
    private TranslateAnimation mTa;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //接收消息，填充ListView，设置适配器
            mLockAdapter = new MyAdapter(true);
            lvLock.setAdapter(mLockAdapter);

            mUnLockAdapter = new MyAdapter(false);
            lvUnlock.setAdapter(mUnLockAdapter);
        }
    };

    private class MyAdapter extends BaseAdapter {
        private boolean isLock;

        /**
         * @param isLock 用于区分已加锁和未加锁应用  true --> 已加锁数据适配器   false --> 未加锁数据适配器
         */
        public MyAdapter(boolean isLock) {
            this.isLock = isLock;
        }

        @Override
        public int getCount() {
            if (isLock) {
                tvLock.setText("已加锁应用: " + mLockList.size());
                return mLockList.size();
            } else {
                tvUnlock.setText("未加锁应用: " + mUnLockList.size());
                return mUnLockList.size();
            }
        }

        @Override
        public AppInfo getItem(int i) {
            if (isLock) {
                return mLockList.get(i);
            } else {
                return mUnLockList.get(i);
            }
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View v = null;
            ViewHolder mViewHolder = null;
            if (view == null) {
                mViewHolder = new ViewHolder();
                v = View.inflate(getApplicationContext(), R.layout.listview_lock_item, null);
                mViewHolder.iv_lock_icon = (ImageView) v.findViewById(R.id.iv_lock_icon);
                mViewHolder.tv_lock_name = (TextView) v.findViewById(R.id.tv_lock_name);
                mViewHolder.iv_lock = (ImageView) v.findViewById(R.id.iv_lock);
                v.setTag(mViewHolder);
            } else {
                v = view;
                mViewHolder = (ViewHolder) v.getTag();
            }

            final AppInfo appInfo = getItem(i);
            mViewHolder.iv_lock_icon.setBackgroundDrawable(appInfo.getIcon());
            mViewHolder.tv_lock_name.setText(appInfo.getName());
            if (isLock) {
                mViewHolder.iv_lock.setImageResource(R.mipmap.lock);
            } else {
                mViewHolder.iv_lock.setImageResource(R.mipmap.unlock);
            }
            final View finalV = v;
            mViewHolder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //4.添加动画效果
                    finalV.startAnimation(mTa);
                    //对动画的执行过程做事件监听，监听到动画执行完成后，再移除对象，操作数据库，刷新Adapter
                    mTa.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            //动画开始时调用的方法
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //动画执行结束时调用的方法
                            if (isLock) {
                                //点中的是已加锁的界面的锁（已加锁 --> 未加锁）
                                //1.将已加锁集合中的数据删除一个，未加锁的集合的数据增加一个，对象是getItem方法获取的对象
                                mLockList.remove(appInfo);
                                mUnLockList.add(appInfo);
                                //2.操作数据库(已加锁数据库中删除一条数据)
                                mDao.delete(appInfo.getPackageName());
                            } else {
                                //点中的是未加锁的界面的锁（未加锁 --> 已加锁）
                                //1.将未加锁集合中的数据删除一个，已加锁的集合的数据增加一个，对象是getItem方法获取的对象
                                mLockList.add(appInfo);
                                mUnLockList.remove(appInfo);
                                //2.操作数据库(已加锁数据库中添加一条数据)
                                mDao.insert(appInfo.getPackageName());
                            }
                            //3.刷新数据适配器
                            if (mLockAdapter != null) {
                                mLockAdapter.notifyDataSetChanged();
                            }
                            //3.刷新数据适配器
                            if (mUnLockAdapter != null) {
                                mUnLockAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            //动画重复执行时调用的方法
                        }
                    });
                }
            });
            return v;
        }
    }

    private class ViewHolder {
        private ImageView iv_lock_icon;
        private ImageView iv_lock;
        private TextView tv_lock_name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        ButterKnife.inject(this);
        initData();
        initBut();
        //初始化动画
        initAnimation();
    }

    /**
     * 动画效果（平移自身的一个宽度大小）
     */
    private void initAnimation() {
        //平移自身的一个宽度大小，x 轴移动 100% ，y 轴不移动
        mTa = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        mTa.setDuration(500);
    }

    private void initBut() {
        butAppLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1.已加锁列表显示，未加锁列表隐藏
                llLock.setVisibility(View.VISIBLE);
                llUnlock.setVisibility(View.GONE);
                //2.图片显示交换
                butAppLock.setBackgroundResource(R.mipmap.tab_right_pressed);
                butAppUnlock.setBackgroundResource(R.mipmap.tab_left_default);
            }
        });

        butAppUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1.已加锁列表隐藏，未加锁列表显示
                llLock.setVisibility(View.GONE);
                llUnlock.setVisibility(View.VISIBLE);
                //2.图片显示交换
                butAppLock.setBackgroundResource(R.mipmap.tab_right_default);
                butAppUnlock.setBackgroundResource(R.mipmap.tab_left_pressed);
            }
        });
    }

    /**
     * 初始化数据，区分已加锁和未加锁应用的集合
     */
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                //1.获取所有的应用
                mAppInfo = AppInfoProvider.getAppInfoList(getApplicationContext());
                //2.区分已加锁应用和未加锁应用
                mUnLockList = new ArrayList<AppInfo>();
                mLockList = new ArrayList<AppInfo>();
                //3.获取已加锁应用的集合
                mDao = AppLockDao.getInstence(getApplicationContext());
                List<String> lockPackageList = mDao.findAll();
                for (AppInfo info : mAppInfo) {
                    //4.判断是否是已加锁应用，是的话就添加到已加锁应用的集合
                    if (lockPackageList.contains(info.getPackageName())) {
                        mLockList.add(info);
                    } else {
                        mUnLockList.add(info);
                    }
                }
                //5.通知主线程，刷新UI
                mHandler.sendEmptyMessage(1);
            }
        }.start();
    }
}
