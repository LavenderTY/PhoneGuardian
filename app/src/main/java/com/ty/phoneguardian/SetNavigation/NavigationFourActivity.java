package com.ty.phoneguardian.SetNavigation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ty.phoneguardian.Activity.BaseNavigationActivity;
import com.ty.phoneguardian.Activity.SetOverActivity;
import com.ty.phoneguardian.bean.ConstentValue;
import com.ty.phoneguardian.R;
import com.ty.phoneguardian.Utils.SpUtils;
import com.ty.phoneguardian.Utils.ToastUtils;

public class NavigationFourActivity extends BaseNavigationActivity {
    private CheckBox cb_is_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_four);
        initUI();
    }

    private void initUI() {
        cb_is_ok = (CheckBox) findViewById(R.id.cb_is_ok);
        //1.是否选中状态的回显
        boolean open_security = SpUtils.getBoolean(this, ConstentValue.OPEN_SECURITY, false);
        //2.根据选中状态设置文字
        cb_is_ok.setChecked(open_security);
        if (open_security) {
            cb_is_ok.setText("安全设置已开启");
        } else {
            cb_is_ok.setText("安全设置已关闭");
        }
        //3.监听cb_is_ok的状态改变过程
        cb_is_ok.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //4.存储点击后的状态,这里已经自动做好了状态取反的操作
                SpUtils.putBoolean(getApplicationContext(), ConstentValue.OPEN_SECURITY, b);
                //5.根据状态改变设置
                if (b) {
                    cb_is_ok.setText("安全设置已开启");
                } else {
                    cb_is_ok.setText("安全设置已关闭");
                }
            }
        });
    }

    @Override
    public void sendToNextPage() {
        boolean open_security = SpUtils.getBoolean(this, ConstentValue.OPEN_SECURITY, false);
        if (open_security) {
            Intent intent = new Intent(this, SetOverActivity.class);
            startActivity(intent);
            finish();
            SpUtils.putBoolean(this, ConstentValue.SETUP_OVER, true);
        } else {
            ToastUtils.show(this, "请开启防盗模式");
        }

        //开启平移动画
        overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
    }

    @Override
    public void sendToPrePage() {
        Intent intent = new Intent(this, NavigationThreeActivity.class);
        startActivity(intent);
        finish();
        //开启平移动画
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }
}
