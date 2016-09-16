package com.ty.phoneguardian.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ty.phoneguardian.Service.AppLockService;
import com.ty.phoneguardian.Service.RocketService;
import com.ty.phoneguardian.bean.ConstentValue;
import com.ty.phoneguardian.R;
import com.ty.phoneguardian.Service.AddressService;
import com.ty.phoneguardian.Service.BlackListService;
import com.ty.phoneguardian.Utils.ServiceUtils;
import com.ty.phoneguardian.Utils.SpUtils;
import com.ty.phoneguardian.ui.SettingClickView;
import com.ty.phoneguardian.ui.SettingItemView;

public class SettingActivity extends AppCompatActivity {
    private SettingItemView siv_update;
    private SettingItemView siv_phone_show;
    private SettingClickView scv_toast_style;
    private SettingClickView scv_toast_position;
    private SettingItemView siv_blacklist_intercept;
    private SettingItemView siv_app_lock;
    private SettingItemView siv_app_rock;
    private String[] mStyle;
    private int mStyle_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //初始化更新
        initUpdate();
        //初始化号码归属地
        initAddress();
        //初始化归属地显示风格
        initAddressStyle();
        //初始化归属地提示框的位置
        initLocation();
        //初始化拦截黑名单
        initBlackList();
        //初始化程序锁
        initAppLock();
        //初始化小火箭
        initAppRock();
    }

    /**
     * 开启或者关闭小火箭
     */
    private void initAppRock() {
        siv_app_rock = (SettingItemView) findViewById(R.id.siv_app_rock);
        boolean isOpen = ServiceUtils.isRunning(this, "com.ty.phoneguardian.Service.RocketService");
        //根据ServiceUtils返回的方法，设置开启状态
        siv_app_rock.setCheck(isOpen);
        //点击过程中，状态(是否开启程序锁)的切换过程
        siv_app_rock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取开始的状态
                boolean isCheck = siv_app_rock.isCheck();
                //如果之前是选中的状态，点击后，设置为未选中状态
                //如果之前是未选中的状态，点击后，设置为选中状态
                siv_app_rock.setCheck(!isCheck);
                if (!isCheck) {
                    //如果是选中状态，就开启服务
                    startService(new Intent(SettingActivity.this, RocketService.class));
                    finish();
                } else {
                    //否则就关闭服务
                    stopService(new Intent(SettingActivity.this, RocketService.class));
                }
            }
        });
    }

    /**
     * 开启或者关闭程序锁
     */
    private void initAppLock() {
        siv_app_lock = (SettingItemView) findViewById(R.id.siv_app_lock);
        boolean isOpen = ServiceUtils.isRunning(this, "com.ty.phoneguardian.Service.AppLockService");
        //根据ServiceUtils返回的方法，设置开启状态
        siv_app_lock.setCheck(isOpen);
        //点击过程中，状态(是否开启程序锁)的切换过程
        siv_app_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取开始的状态
                boolean isCheck = siv_app_lock.isCheck();
                //如果之前是选中的状态，点击后，设置为未选中状态
                //如果之前是未选中的状态，点击后，设置为选中状态
                siv_app_lock.setCheck(!isCheck);

                /*相当于上面那一句
                if(isCheck){
                    siv_app_lock.setCheck(false);
                }else{
                    siv_app_lock.setCheck(true);
                }*/
                if (!isCheck) {
                    //如果是选中状态，就开启服务
                    startService(new Intent(SettingActivity.this, AppLockService.class));
                } else {
                    //否则就关闭服务
                    stopService(new Intent(SettingActivity.this, AppLockService.class));
                }
            }
        });
    }

    /**
     * 设置吐司显示位置
     */
    private void initLocation() {
        scv_toast_position = (SettingClickView) findViewById(R.id.scv_toast_position);
        scv_toast_position.setTitle("归属地提示框位置");
        scv_toast_position.setDes("设置归属地提示框位置");
        scv_toast_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ToastLocationActivity.class));
            }
        });
    }

    /**
     * 归属地显示风格
     */
    private void initAddressStyle() {
        scv_toast_style = (SettingClickView) findViewById(R.id.scv_toast_style);
        scv_toast_style.setTitle("设置归属地显示风格");
        //1.设置归属地风格显示内容
        mStyle = new String[]{"透明", "橙色", "蓝色", "灰色", "绿色"};
        //2.sp获取吐司显示样式的索引值，获取描述文字
        mStyle_index = SpUtils.getInt(this, ConstentValue.TOAST_STYLE, 0);
        //3.根据索引值，获取显示内容，并显示在相应的位置
        scv_toast_style.setDes(mStyle[mStyle_index]);
        //4.监听点击事件，弹出对话框
        scv_toast_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //5.显示吐司样式的对话框
                showToastStyle();
            }
        });
    }

    /**
     * 创建显示样式的对话框
     */
    private void showToastStyle() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.start);
        builder.setTitle("请选择归属地样式");
        //选择单个条目的监听
        /*
         *1.String类型的描述颜色文字数组
         * 2.弹出对话框的时候选中条目索引值
         * 3.点击某一个条目后触发的点击事件
         */
        builder.setSingleChoiceItems(mStyle, mStyle_index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //（选中的索引值，关闭对话框，选中的颜色的值）
                SpUtils.putInt(getApplicationContext(), ConstentValue.TOAST_STYLE, i);
                scv_toast_style.setDes(mStyle[i]);
                dialogInterface.dismiss();
            }
        });

        System.out.println(mStyle_index);

        //取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        //显示对话框
        builder.show();
    }

    /**
     * 是否显示号码归属地的方法
     */
    private void initAddress() {
        siv_phone_show = (SettingItemView) findViewById(R.id.siv_phone_show);
        boolean isOpen = ServiceUtils.isRunning(this, "com.ty.phoneguardian.Service.AddressService");
        //根据ServiceUtils返回的方法，设置开启状态
        siv_phone_show.setCheck(isOpen);
        //点击过程中，状态(是否开启电话号码归属地)的切换过程
        siv_phone_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取开始的状态
                boolean isCheck = siv_phone_show.isCheck();
                //如果之前是选中的状态，点击后，设置为未选中状态
                //如果之前是未选中的状态，点击后，设置为选中状态
                siv_phone_show.setCheck(!isCheck);
                if (!isCheck) {
                    //如果是选中状态，就开启服务
                    startService(new Intent(SettingActivity.this, AddressService.class));
                } else {
                    //否则就关闭服务
                    stopService(new Intent(SettingActivity.this, AddressService.class));
                }
            }
        });
    }

    /**
     * 版本更新开关
     */
    private void initUpdate() {
        siv_update = (SettingItemView) findViewById(R.id.siv_update);
        //获取已有的开关状态，用作显示
        boolean open_update = SpUtils.getBoolean(this, ConstentValue.OPEN_UPDATE, false);
        //是否选中，根据上一次存储的结果决定
        siv_update.setCheck(open_update);
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获得开始的状态
                boolean isCheck = siv_update.isCheck();
                //如果之前是选中的状态，点击后，设置为未选中状态
                //如果之前是未选中的状态，点击后，设置为选中状态
                siv_update.setCheck(!isCheck);
                //将取反后的值存入到SharedPreferences
                SpUtils.putBoolean(getApplicationContext(), ConstentValue.OPEN_UPDATE, !isCheck);
            }
        });
    }

    /**
     * 拦截黑名单
     */
    private void initBlackList() {
        siv_blacklist_intercept = (SettingItemView) findViewById(R.id.siv_blacklist_intercept);
        boolean isOpen = ServiceUtils.isRunning(this, "com.ty.phoneguardian.Service.BlackListService");
        //根据ServiceUtils返回的方法，设置开启状态
        siv_blacklist_intercept.setCheck(isOpen);
        //点击过程中，状态(是否开启电话号码归属地)的切换过程
        siv_blacklist_intercept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取开始的状态
                boolean isCheck = siv_blacklist_intercept.isCheck();
                //如果之前是选中的状态，点击后，设置为未选中状态
                //如果之前是未选中的状态，点击后，设置为选中状态
                siv_blacklist_intercept.setCheck(!isCheck);

                /*相当于上面那一句
                if(isCheck){
                    siv_blacklist_intercept.setCheck(false);
                }else{
                    siv_blacklist_intercept.setCheck(true);
                }*/
                if (!isCheck) {
                    //如果是选中状态，就开启服务
                    startService(new Intent(SettingActivity.this, BlackListService.class));
                } else {
                    //否则就关闭服务
                    stopService(new Intent(SettingActivity.this, BlackListService.class));
                }
            }
        });
    }
}
