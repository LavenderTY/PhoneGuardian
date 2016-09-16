package com.ty.phoneguardian.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Lavender on 2016/8/15.
 */
public class ProcessInfo {
    private String name;  //应用名称
    private Drawable icon;  //应用图标
    private long memSize;   //应用已使用的内存数
    public boolean isCheck;  //是否被选中
    private boolean isSystem;  //是否为系统应用
    private String packageName;  //如果进程没有名字，则将其所在应用的包名设置为名称

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMemSize() {
        return memSize;
    }

    public void setMemSize(long memSize) {
        this.memSize = memSize;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
