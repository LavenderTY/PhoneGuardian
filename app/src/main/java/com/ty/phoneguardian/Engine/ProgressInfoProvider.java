package com.ty.phoneguardian.Engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Debug;

import com.ty.phoneguardian.bean.ProcessInfo;
import com.ty.phoneguardian.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lavender on 2016/8/15.
 */
public class ProgressInfoProvider {

    //获取进程总数的方法
    public static int getProgressCount(Context context) {
        //1.获取ActivityManager对象
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //2.获取正在运行的进程的集合
        List<ActivityManager.RunningAppProcessInfo> list = manager.getRunningAppProcesses();
        //3.返回进程总数
        return list.size();
    }

    /**
     * @param context 上下文对象
     * @return 返回可用的内存数  bytes
     */
    public static long getAvailSpace(Context context) {
        //1.获取ActivityManager
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //2.构建存储可用内存对象
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        //3.给对象赋值
        manager.getMemoryInfo(info);
        //4.获取对象中相应可用内存大小
        return info.availMem;
    }

    /**
     * @param context 上下文对象
     * @return 返回总的内存数  bytes   返回0说明异常
     */
    public static long getTotalSpace(Context context) {
        //内存大小写入文件中，读取 proc/meminfo 文件，读取第一行，获取数字字符，转换成bytes返回
        BufferedReader br = null;
        try {
            FileReader fr = new FileReader("proc/meminfo");
            br = new BufferedReader(fr);
            String firstLine = br.readLine();
            //将字符串转为字符数组
            char[] cArray = firstLine.toCharArray();
            //遍历所有字符，ASCII在0-9之间，说明有效
            StringBuffer buffer = new StringBuffer();
            for (char c : cArray) {
                if (c >= '0' && c <= '9') {
                    buffer.append(c);
                }
            }
            return Long.parseLong(buffer.toString()) * 1024;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    /**
     * 获取进程的相关信息
     *
     * @param context 上下文对象
     * @return 包含当前正在运行的进程信息的集合
     */

    public static List<ProcessInfo> getPrgressInfo(Context context) {
        List<ProcessInfo> listInfo = new ArrayList<ProcessInfo>();
        //1.ActivityManager管理者对象,PackageManager管理者对象
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        //2.获取正在运行的进程的集合
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
        //3.遍历集合，获取相关信息(名称，包名，图标，使用内存大小，是否为系统进程)
        for (ActivityManager.RunningAppProcessInfo info : list) {
            ProcessInfo processInfo = new ProcessInfo();
            //4.获取进程的名称 == 应用名称
            processInfo.setPackageName(info.processName);
            //5.获取进程占用的内存大小（传递一个进程对应的pid数组）
            Debug.MemoryInfo[] mInfo = am.getProcessMemoryInfo(new int[]{info.pid});
            //6.数组中索引为0的对象，为当前进程的内存信息对象
            Debug.MemoryInfo memoryInfo = mInfo[0];
            //7.获取已使用的大小   返回的单位为 kb
            processInfo.setMemSize(memoryInfo.getTotalPrivateDirty() * 1024);
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.getPackageName(), 0);
                //8.获取应用名称
                processInfo.setName(applicationInfo.loadLabel(pm).toString());
                //9.获取应用图标
                processInfo.setIcon(applicationInfo.loadIcon(pm));
                //10.判断是否为系统进程
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    //系统进程
                    processInfo.setSystem(true);
                } else {
                    processInfo.setSystem(false);
                }
            } catch (PackageManager.NameNotFoundException e) {
                //如果没有名称和图标的话也要处理
                processInfo.setName(info.processName);
                processInfo.setIcon(context.getResources().getDrawable(R.mipmap.ic_launcher));
                processInfo.setSystem(true);
                e.printStackTrace();
            }
            listInfo.add(processInfo);
        }
        return listInfo;
    }

    /**
     * 杀死进程的方法
     *
     * @param ctx  上下文对象
     * @param info 杀死进程所有的javabean对象
     */
    public static void killProgress(Context ctx, ProcessInfo info) {
        //1.获得 ActivityManager
        ActivityManager manage = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //2.杀死这个进程
        manage.killBackgroundProcesses(info.getPackageName());
    }

    /**
     * 杀死所有的进程的方法
     *
     * @param ctx 上下文对象
     */
    public static void killAllProgress(Context ctx) {
        //1.获得 ActivityManager
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //2.获取正在运行的进程的集合
        List<ActivityManager.RunningAppProcessInfo> list = manager.getRunningAppProcesses();
        //3.遍历集合，杀死所有的继承
        for (ActivityManager.RunningAppProcessInfo info : list) {
            if (info.processName.equals(ctx.getPackageName())) {
                //如果这个进程就是当前应用，则跳出本次循环
                continue;
            }
            manager.killBackgroundProcesses(info.processName);
        }
    }
}
