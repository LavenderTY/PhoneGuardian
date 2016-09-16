package com.ty.phoneguardian.Service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ty.phoneguardian.Activity.EnterPwdActivity;
import com.ty.phoneguardian.Dao.AppLockDao;

import java.util.List;

/**
 * Created by Lavender on 2016/8/17.
 */
public class AppLockService extends Service {
    private boolean isWatch;
    private AppLockDao mDao;
    private String mSkipPackageName;
    private BroadcastReceiver mInnerSKIPReceiver;
    private List<String> mPackageNameList;
    private MyContentObserver mContentObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        mDao = AppLockDao.getInstence(this);
        isWatch = true;
        //1.看门狗死循环，让其时刻监听现在正在运行的任务，是否是程序锁中拦截的任务
        watch();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SKIP");
        mInnerSKIPReceiver = new InnerSKIPReceiver();
        registerReceiver(mInnerSKIPReceiver, filter);

        //注册一个内容观察者，观察内容的变化，一旦数据有改动，则集合需要重新获取数据
        mContentObserver = new MyContentObserver(new Handler());
        //false的话AppLockDao里面的Uri必须一致，true的话，可以不一样
        getContentResolver().registerContentObserver(Uri.parse("content://applock/change"), true, mContentObserver);
    }

    private class MyContentObserver extends ContentObserver {
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        //数据库发生改变的时候调用
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new Thread() {
                @Override
                public void run() {
                    mPackageNameList = mDao.findAll();
                }
            }.start();
        }
    }

    private class InnerSKIPReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取广播发送过来的包名
            mSkipPackageName = intent.getStringExtra("packageName");
        }
    }

    private void watch() {
        new Thread() {
            @Override
            public void run() {
                mPackageNameList = mDao.findAll();
                //1.子线程中，开启一个可控的死循环
                while (isWatch) {
                    //2.获取Activity的管理者
                    ActivityManager activity = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    //3.得到正在开启的应用的任务栈
                    List<ActivityManager.RunningTaskInfo> taskList = activity.getRunningTasks(1);
                    ActivityManager.RunningTaskInfo runningTaskInfo = taskList.get(0);
                    //4.获取当前应用的包名(即获取栈顶的Activity)
                    String packageName = runningTaskInfo.topActivity.getPackageName();
                    //5.判断程序锁的集合中是否包含当前应用的包名
                    if (mPackageNameList.contains(packageName)) {
                        //如果此应用的包名和需要过滤掉的包名不一致，则拦截
                        if (!packageName.equals(mSkipPackageName)) {
                            //6.弹出拦截一个界面
                            Intent intent = new Intent(getApplicationContext(), EnterPwdActivity.class);
                            //开启一个新的任务栈
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("packageName", packageName);
                            startActivity(intent);
                        }
                    }
                }
                //如果没毫秒都要检测的话，对内存资源消耗比较大，cpu的效率也会下降，所以要睡眠一段时间
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销广播接收者
        if (mInnerSKIPReceiver != null) {
            unregisterReceiver(mInnerSKIPReceiver);
        }
        //停止看门狗循环
        isWatch = false;

        //注销内容观察者
        if (mContentObserver != null) {
            getContentResolver().unregisterContentObserver(mContentObserver);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
