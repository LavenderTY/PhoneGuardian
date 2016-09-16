package com.ty.phoneguardian.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ty.phoneguardian.bean.ConstentValue;
import com.ty.phoneguardian.R;
import com.ty.phoneguardian.Service.LockScreenService;
import com.ty.phoneguardian.Utils.ServiceUtils;
import com.ty.phoneguardian.Utils.SpUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProgressSettingActivity extends AppCompatActivity {

    @InjectView(R.id.hidden_sys_progress)
    CheckBox hiddenSysProgress;
    @InjectView(R.id.screen_clear_close)
    CheckBox screenClearClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_setting);
        ButterKnife.inject(this);
        initSysHidden();
        initLockScreenClear();
    }

    /**
     * 锁屏清理
     */
    private void initLockScreenClear() {
        //回显状态
        boolean isRunning = ServiceUtils.isRunning(this, "com.ty.phoneguardian.Service.LockScreenService");
        if (isRunning) {
            screenClearClose.setText("锁屏清理已开启");
        } else {
            screenClearClose.setText("锁屏清理已关闭");
        }
        //设置screenClearClose的状态
        screenClearClose.setChecked(isRunning);

        //设置是否选中
        screenClearClose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //根据选中状态设置值
                if (b) {
                    screenClearClose.setText("锁屏清理已开启");
                    //开启的话就要打开服务
                    startService(new Intent(getApplicationContext(), LockScreenService.class));
                } else {
                    screenClearClose.setText("锁屏清理已关闭");
                    //关闭的话就要关闭服务
                    stopService(new Intent(getApplicationContext(), LockScreenService.class));
                }
            }
        });
    }

    /**
     * 初始化隐藏系统进程
     */
    private void initSysHidden() {
        //回显显示或隐藏状态
        boolean isChick = SpUtils.getBoolean(this, ConstentValue.SHOW_SYSTEM, false);
        //设置选择框得状态
        hiddenSysProgress.setChecked(isChick);
        if (isChick) {
            hiddenSysProgress.setText("显示系统进程");
        } else {
            hiddenSysProgress.setText("隐藏系统进程");
        }
        //设置是否选中
        hiddenSysProgress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //根据选中状态设置值
                if (b) {
                    hiddenSysProgress.setText("显示系统进程");
                } else {
                    hiddenSysProgress.setText("隐藏系统进程");
                }
                //将是否选中的状态保存下来
                SpUtils.putBoolean(getApplicationContext(), ConstentValue.SHOW_SYSTEM, b);
            }
        });
    }
}
