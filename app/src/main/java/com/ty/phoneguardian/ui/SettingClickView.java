package com.ty.phoneguardian.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ty.phoneguardian.R;

/**
 * Created by Lavender on 2016/8/9.
 */
public class SettingClickView extends RelativeLayout {
    private TextView tv_color_click;
    private TextView tv_title_click;

    public SettingClickView(Context context) {
        this(context, null);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //xml 转为 view，将设置界面的一个条目转换成 view 对象，直接设置到当前 SettingItemView 对应的 view
        //这个this表示的是SettingItemView，这里设置this后表示要将这个view挂载在其他view上
        View.inflate(context, R.layout.setting_click_view, this);
        tv_title_click = (TextView) findViewById(R.id.tv_title_click);
        tv_color_click = (TextView) findViewById(R.id.tv_color_click);
    }

    /**
     * 设置标题的方法
     *
     * @param title 标题的名字
     */
    public void setTitle(String title) {
        tv_title_click.setText(title);
    }

    /**
     * 设置颜色描述内容的方法
     *
     * @param des tv_color_click
     */
    public void setDes(String des) {
        tv_color_click.setText(des);
    }
}
