package com.ty.phoneguardian.Receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ty.phoneguardian.Service.UpdateWidgetService;

/**
 * Created by Lavender on 2016/8/16.
 */
public class MyAppWidgetProvider extends AppWidgetProvider {
    private final String TAG = "MyAppWidgetProvider";
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        //创建第一个窗体小部件的调用的方法
        Log.i(TAG,"onEnabled 创建第一个窗体小部件的调用的方法");
        //开启服务(onCreate方法只会调用一次)
        context.startService(new Intent(context,UpdateWidgetService.class));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        //创建多一个窗体小部件调用的方法
        Log.i(TAG,"onUpdate 创建多一个窗体小部件调用的方法");
        //创建第一个的时候也会调用这个方法
        //开启服务
        context.startService(new Intent(context,UpdateWidgetService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        //当窗体小部件高宽发生改变的时候调用的方法，创建小部件的时候，也调用此方法
        Log.i(TAG,"onAppWidgetOptionsChanged 当窗体小部件高宽发生改变的时候调用的方法");
        //创建的时候也会调用
        //开启服务
        context.startService(new Intent(context,UpdateWidgetService.class));
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        //删除一个窗体小部件调用的方法
        Log.i(TAG,"onDeleted 删除一个窗体小部件调用的方法");
        //删除最后一个窗体小部件的时候也会调用此方法
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        //删除最后一个窗体小部件的时候调用的方法
        Log.i(TAG,"onDisabled 删除最后一个窗体小部件的时候调用的方法");
        //关闭服务
        context.stopService(new Intent(context,UpdateWidgetService.class));
    }
}
