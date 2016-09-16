package com.ty.phoneguardian.Engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.ty.phoneguardian.bean.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lavender on 2016/8/15.
 */
public class AppInfoProvider {
    /**
     * 返回当前手机所有的应用的相关信息（名称，包名，图标，（内存，sd卡），（系统，用户））
     *
     * @param context 获取包管理者的上下文对象
     * @return 包含手机安装的应用的相关信息的集合
     */
    public static List<AppInfo> getAppInfoList(Context context) {
        List<AppInfo> appList = new ArrayList<AppInfo>();
        //1.包管理者对象
        PackageManager manager = context.getPackageManager();
        //2.获取安装在手机上应用相关信息的集合
        List<PackageInfo> list = manager.getInstalledPackages(0);
        //3.循环遍历集合信息
        for (PackageInfo info : list) {
            AppInfo appInfo = new AppInfo();
            //4.获取应用包名
            appInfo.setPackageName(info.packageName);
            //5.获取应用名称
            ApplicationInfo applicationInfo = info.applicationInfo;
            //applicationInfo.uid   获取每一个应用的唯一性标识
            appInfo.setName(applicationInfo.loadLabel(manager).toString() + applicationInfo.uid);
            //6.获取图标
            appInfo.setIcon(applicationInfo.loadIcon(manager));
            //7.系统，用户 (每一个手机上的应用对应的flag都不一样)
            //& 是位与
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                //系统应用
                appInfo.setSystem(true);
            } else {
                //用户应用
                appInfo.setSystem(false);
            }
            //内存，sd卡
            if ((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
                //sd卡
                appInfo.setSdCard(true);
            } else {
                //内存
                appInfo.setSdCard(false);
            }
            appList.add(appInfo);
        }
        return appList;
    }
}
