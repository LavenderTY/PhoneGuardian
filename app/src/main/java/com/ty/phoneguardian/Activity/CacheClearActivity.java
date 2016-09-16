package com.ty.phoneguardian.Activity;

import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ty.phoneguardian.R;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

public class CacheClearActivity extends AppCompatActivity {
    private Button cache_clear_now;
    private ProgressBar pb_cache_bar;
    private TextView tv_cache_name;
    private LinearLayout ll_add_cache;
    private PackageManager mPm;
    private static final int UPDATE_CACHE_APP = 100;
    private static final int CHICK_CACHE_APP = 101;
    private static final int SCAN_FINISH = 102;
    private static final int CLEAR_CACHE = 103;
    private int index = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_CACHE_APP:
                    View view = View.inflate(getApplicationContext(), R.layout.cache_linearlayout_item, null);
                    ImageView iv_cache_icon = (ImageView) view.findViewById(R.id.iv_cache_icon);
                    TextView tv_clear_name = (TextView) view.findViewById(R.id.tv_clear_name);
                    TextView tv_cache_memory = (TextView) view.findViewById(R.id.tv_cache_memory);
                    Button but_cache_delete = (Button) view.findViewById(R.id.but_cache_delete);

                    final CacheInfo cacheInfo = (CacheInfo) msg.obj;
                    iv_cache_icon.setBackgroundDrawable(cacheInfo.icon);
                    tv_clear_name.setText(cacheInfo.name);
                    tv_cache_memory.setText(Formatter.formatFileSize(getApplicationContext(), cacheInfo.cacheSize));
                    ll_add_cache.addView(view, 0);

                    but_cache_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //源码开发课程(源码(handler机制,AsyncTask(异步请求,手机启动流程)源码))
                            //通过查看系统日志,获取开启清理缓存activity中action和data
                            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.parse("package:" + cacheInfo.packageName));
                            startActivity(intent);
                        }
                    });
                    break;
                case CHICK_CACHE_APP:
                    tv_cache_name.setText((String) msg.obj);
                    break;
                case SCAN_FINISH:
                    tv_cache_name.setText("扫描完成");
                    break;
                case CLEAR_CACHE:
                    //从线性布局中移除所有条目
                    ll_add_cache.removeAllViews();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clear);
        initUI();
        initData();
    }

    /**
     * 遍历手机所有的应用，获取有缓存的应用，用作显示
     */
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                //1.获取包管理者
                mPm = getPackageManager();
                //2.获取安装在手机上的所有的应用
                List<PackageInfo> packageInfoList = mPm.getInstalledPackages(0);
                //3.给进度条设置最大值（手机中所有应用的总数）
                pb_cache_bar.setMax(packageInfoList.size());
                //4.遍历应用，获取缓存的应用信息（应用名称，图标，缓存大小，包名）
                for (PackageInfo info : packageInfoList) {
                    //包名作为缓存信息的条件
                    String packageName = info.packageName;
                    getPackageCache(packageName);
                    try {
                        Thread.sleep(100 + new Random().nextInt(50));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    index++;
                    pb_cache_bar.setProgress(index);

                    //每循环一次就将检测应用的名称发给主线程
                    Message msg = Message.obtain();
                    String name = null;
                    try {
                        name = mPm.getApplicationInfo(packageName, 0).loadLabel(mPm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    msg.what = CHICK_CACHE_APP;
                    msg.obj = name;
                    mHandler.sendMessage(msg);
                }
                //扫描完成
                Message msg = Message.obtain();
                msg.what = SCAN_FINISH;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    class CacheInfo {
        private String name;
        private String packageName;
        private Drawable icon;
        private long cacheSize;
    }

    /**
     * 通过包名获取此包名指向应用的缓存信息
     *
     * @param packageName 应用包名
     */
    private void getPackageCache(String packageName) {
        IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
            @Override
            public void onGetStatsCompleted(PackageStats pStats,
                                            boolean succeeded) throws RemoteException {
                //4.获取指定包名的缓存大小
                long cacheSize = pStats.cacheSize;
                //5.判断是否有缓存
                if (cacheSize > 0) {
                    //6.通知主线程更新UI
                    Message msg = Message.obtain();
                    msg.what = UPDATE_CACHE_APP;
                    CacheInfo cacheInfo = null;
                    //7.维护有缓存的javabean对象
                    try {
                        cacheInfo = new CacheInfo();
                        cacheInfo.name = mPm.getApplicationInfo(pStats.packageName, 0).loadLabel(mPm).toString();
                        cacheInfo.icon = mPm.getApplicationInfo(pStats.packageName, 0).loadIcon(mPm);
                        cacheInfo.cacheSize = cacheSize;
                        cacheInfo.packageName = pStats.packageName;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    msg.obj = cacheInfo;
                    mHandler.sendMessage(msg);
                }
            }
        };
        //1.获取指定类的字节码文件
        try {
            Class<?> clazz = Class.forName("android.content.pm.PackageManager");
            //2.获取调用方法对象
            Method method = clazz.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            //3.获取对象调用的方法
            method.invoke(mPm, packageName, mStatsObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initUI() {
        cache_clear_now = (Button) findViewById(R.id.cache_clear_now);
        pb_cache_bar = (ProgressBar) findViewById(R.id.pb_cache_bar);
        tv_cache_name = (TextView) findViewById(R.id.tv_cache_name);
        ll_add_cache = (LinearLayout) findViewById(R.id.ll_add_cache);

        cache_clear_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1.获取指定类的字节码文件
                try {
                    Class<?> clazz = Class.forName("android.content.pm.PackageManager");
                    //2.获取调用方法对象    long.class, IPackageDataObserver.class 两个参数
                    Method method = clazz.getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
                    //3.获取对象调用的方法
                    method.invoke(mPm, Long.MAX_VALUE, new IPackageDataObserver.Stub() {
                        @Override
                        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                            //清除缓存完成后调用
                            Message msg = Message.obtain();
                            msg.what = CLEAR_CACHE;
                            mHandler.sendMessage(msg);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

