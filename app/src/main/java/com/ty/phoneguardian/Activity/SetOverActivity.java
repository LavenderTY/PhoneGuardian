package com.ty.phoneguardian.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ty.phoneguardian.bean.ConstentValue;
import com.ty.phoneguardian.R;
import com.ty.phoneguardian.SetNavigation.NavigationFirstActivity;
import com.ty.phoneguardian.Utils.SpUtils;

public class SetOverActivity extends AppCompatActivity {
    private TextView tv_phone_number;
    private TextView tv_setting_again;
    private ImageView iv_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean setup_over = SpUtils.getBoolean(this, ConstentValue.SETUP_OVER, false);
        if (setup_over) {
            //设置密码成功且导航界面设置完成，停在设置完成功能列表界面
            setContentView(R.layout.activity_set_over);
            initUI();
        } else {
            //若导航界面没有设置完成，则跳转到导航界面一
            Intent intent = new Intent(this, NavigationFirstActivity.class);
            startActivity(intent);
            //跳转完成后，结束当前界面
            finish();
        }
    }

    /**
     * 初始化UI界面
     */
    private void initUI() {
        iv_close = (ImageView) findViewById(R.id.iv_close);
        tv_phone_number = (TextView) findViewById(R.id.tv_phone_number);
        tv_setting_again = (TextView) findViewById(R.id.tv_setting_again);
        //从SP中获取设置的电话号码
        String phone = SpUtils.getString(this, ConstentValue.CONTACT_PHONE, "");
        if (!TextUtils.isEmpty(phone)) {
            tv_phone_number.setText(phone);
        }
        boolean isOpen_Close = SpUtils.getBoolean(this, ConstentValue.OPEN_SECURITY, false);
        if (isOpen_Close) {
            iv_close.setImageResource(R.mipmap.lock);
        } else {
            iv_close.setImageResource(R.mipmap.unlock);
        }
        tv_setting_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SetOverActivity.this,NavigationFirstActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
