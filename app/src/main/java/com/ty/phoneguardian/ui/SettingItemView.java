package com.ty.phoneguardian.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ty.phoneguardian.R;

/**
 * Created by Lavender on 2016/8/9.
 */
public class SettingItemView extends RelativeLayout {
    private static final String SPACENAME = "http://schemas.android.com/apk/res/com.ty.phoneguardian";
    private String TAG = "SettingItemView";
    private CheckBox cb_yes_no;
    private TextView tv_setting_update;
    private TextView tv_open_close;
    private String mDestitle;
    private String mDesoff;
    private String mDeson;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //xml 转为 view，将设置界面的一个条目转换成 view 对象，直接设置到当前 SettingItemView 对应的 view
        //这个this表示的是SettingItemView，这里设置this后表示要将这个view挂载在其他view上
        View.inflate(context, R.layout.setting_item_view, this);

        //自定义组合控件中的标题描述
        //this 表示SettingItemView，上面那句话执行后，表示SettingItemView中也有view对象了
        //也可以不用this,上面那句话执行后，表示SettingItemView中也有view对象了
        tv_setting_update = (TextView) this.findViewById(R.id.tv_setting_update);
        tv_open_close = (TextView) this.findViewById(R.id.tv_open_close);
        cb_yes_no = (CheckBox) this.findViewById(R.id.cb_yes_no);

        //获取自定义以及原生属性的操作，写在此处，AttributeSet attrs对象中获取
        initAttrs(attrs);
        //设置值
        tv_setting_update.setText(mDestitle);
        tv_setting_update.setTextColor(Color.BLACK);
    }

    /**
     * 返回属性集合中自定义属性的属性值
     *
     * @param attrs 构造方法中维护好的属性集合
     */
    private void initAttrs(AttributeSet attrs) {
        /*//获取属性的总个数
        Log.i(TAG, "属性的总个数 = " + attrs.getAttributeCount());
        //获取属性名称以及属性值
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            Log.i(TAG, "name = " + attrs.getAttributeName(i));
            Log.i(TAG, "value = " + attrs.getAttributeValue(i));
            Log.i(TAG, "分割线==============================");
        }*/
        mDestitle = attrs.getAttributeValue(SPACENAME, "destitle");
        mDesoff = attrs.getAttributeValue(SPACENAME, "desoff");
        mDeson = attrs.getAttributeValue(SPACENAME, "deson");
    }

    /**
     * 判断是否开启的方法
     *
     * @return 放回当前SettingItemView是否选中，true开启（CheckBox返回true）  false关闭（CheckBox返回false）
     */
    public boolean isCheck() {
        //由CheckBox的选中结果，决定当前条目是否开启
        return cb_yes_no.isChecked();
    }

    /**
     * 设置是否为开启状态方法
     *
     * @param isCheck 返回true,则设置为开启状态，返回false,则设置为关闭状态
     */
    public void setCheck(boolean isCheck) {
        //isCheck 决定 CheckBox 是否选中
        cb_yes_no.setChecked(isCheck);
        if (isCheck) {
            tv_open_close.setText(mDeson);
        } else {
            tv_open_close.setText(mDesoff);
        }
    }
}
