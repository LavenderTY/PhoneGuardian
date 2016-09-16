package com.ty.phoneguardian.Utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Lavender on 2016/8/12.
 */
public class ServiceUtils {
    /**
     * 判断服务是否开启
     *
     * @param ctx         上下文对象
     * @param serviceName 服务的名字
     * @return 返回true表示在运行，false表示没有运行
     */
    public static boolean isRunning(Context ctx, String serviceName) {
        //1.获取ActivityManager，可以获得当前手机正在运行的所有服务
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //2.可以获得当前手机正在运行的所有服务
        List<ActivityManager.RunningServiceInfo> lists = am.getRunningServices(1000);
        //3.遍历所有的服务
        for (ActivityManager.RunningServiceInfo list : lists) {
            //4.若创建来的服务匹配成功，则返回true
            if (serviceName.equals(list.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
