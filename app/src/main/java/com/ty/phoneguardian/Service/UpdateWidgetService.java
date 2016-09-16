package com.ty.phoneguardian.Service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.ty.phoneguardian.Engine.ProgressInfoProvider;
import com.ty.phoneguardian.R;
import com.ty.phoneguardian.Receiver.MyAppWidgetProvider;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Lavender on 2016/8/16.
 */
public class UpdateWidgetService extends Service {
    private final String TAG = "UpdateWidgetService";
    private Timer mTimer;
    private InnerTimerReceiver mInnerTimerReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //处理窗体小部件，因为这个方法开启服务的时候只会调用一次
        //管理进程总数和可用内存数更新（定时器）
        startTimer();
        //注册锁屏或者开锁的广播接收者
        //注册开锁,解锁广播接受者
        IntentFilter intentFilter = new IntentFilter();
        //开锁action
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        //解锁action
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mInnerTimerReceiver = new InnerTimerReceiver();
        registerReceiver(mInnerTimerReceiver, intentFilter);
    }

    private class InnerTimerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                //开启定时器
                startTimer();
            } else {
                //关闭定时器
                cancelTimerTask();
            }
        }
    }

    /**
     * 关闭定时器
     */
    private void cancelTimerTask() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 开启定时器
     */
    private void startTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //ui定时刷新
                updateAppWidget();
            }
        }, 0, 5000);
    }

    /**
     * 更新AppWidget上View
     */
    private void updateAppWidget() {
        //1.获取AppWidget的对象
        AppWidgetManager mAWM = AppWidgetManager.getInstance(this);
        //2.获取窗体小部件布局转换成的View对象
        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.process_widget);
        //3.给窗体小部件的view内部控件赋值
        rv.setTextViewText(R.id.process_count, "进程总数: " + ProgressInfoProvider.getProgressCount(this));
        rv.setTextColor(R.id.process_count, Color.BLACK);
        //4.显示可用内存大小
        String strASpace = Formatter.formatFileSize(this, ProgressInfoProvider.getAvailSpace(this));
        rv.setTextViewText(R.id.process_memory, "可用内存: " + strASpace);
        rv.setTextColor(R.id.process_memory, Color.BLACK);

        //创建一个跳转的intent
        Intent intent = new Intent("android.intent.action.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        //点击窗体小部件，进入应用（响应点击事件的控件，延期的意图）
        rv.setOnClickPendingIntent(R.id.ll_widget, pIntent);

        Intent broadIntent = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, broadIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        //通过延期意图发送广播，在广播接收者中杀死进程
        rv.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

        //上下文环境，窗体小部件对应的广播接收者的字节码文件
        ComponentName cName = new ComponentName(this, MyAppWidgetProvider.class);
        //5.更新窗体小部件
        mAWM.updateAppWidget(cName, rv);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mInnerTimerReceiver != null) {
            unregisterReceiver(mInnerTimerReceiver);
        }
        //调用onDestroy即关闭服务,关闭服务的方法在移除最后一个窗体小部件的时调用,定时任务也没必要维护
        cancelTimerTask();
    }
}
