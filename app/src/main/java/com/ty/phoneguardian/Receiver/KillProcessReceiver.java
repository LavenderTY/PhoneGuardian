package com.ty.phoneguardian.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ty.phoneguardian.Engine.ProgressInfoProvider;

/**
 * Created by Lavender on 2016/8/16.
 */
public class KillProcessReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //杀死进程
        ProgressInfoProvider.killAllProgress(context);
    }
}
