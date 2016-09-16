package com.ty.phoneguardian.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Lavender on 2016/8/15.
 */
public class AppInfo {
    //名称，包名，图标，（内存，sd卡），（系统，用户）
    private String name;
    private String packageName;
    private Drawable icon;
    private boolean isSdCard;
    private boolean isSystem;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isSdCard() {
        return isSdCard;
    }

    public void setSdCard(boolean sdCard) {
        isSdCard = sdCard;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }
}