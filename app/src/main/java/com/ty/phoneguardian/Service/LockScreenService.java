package com.ty.phoneguardian.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ty.phoneguardian.Activity.ProgressManagerActivity;
import com.ty.phoneguardian.Engine.ProgressInfoProvider;

/**
 * Created by Lavender on 2016/8/16.
 */
public class LockScreenService extends Service {

    private IntentFilter mFilter;
    private InnerReceiver mReceiver;

    @Override
    public void onCreate() {
        //发送屏幕锁屏的广播
        //锁屏Action
        mFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        //创建广播接收者对应的类
        mReceiver = new InnerReceiver();
        //注册广播接收者
        registerReceiver(mReceiver, mFilter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        //反注册广播接收者
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ProgressInfoProvider.killAllProgress(context);
        }
    }
}
