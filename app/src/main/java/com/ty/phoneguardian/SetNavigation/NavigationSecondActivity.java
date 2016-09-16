package com.ty.phoneguardian.SetNavigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.ty.phoneguardian.Activity.BaseNavigationActivity;
import com.ty.phoneguardian.R;
import com.ty.phoneguardian.bean.ConstentValue;
import com.ty.phoneguardian.Utils.SpUtils;
import com.ty.phoneguardian.Utils.ToastUtils;
import com.ty.phoneguardian.ui.SettingItemView;

public class NavigationSecondActivity extends BaseNavigationActivity {
    private SettingItemView sim_bind;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_second);
        //初始化UI
        initUI();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        sim_bind = (SettingItemView) findViewById(R.id.sim_bind);
        //1.回显（读取已绑定的状态）
        final String sim_number = SpUtils.getString(this, ConstentValue.SIM_NUMBER, "");
        //2.判断序列号是否为空
        if (TextUtils.isEmpty(sim_number)) {
            //为空时，设置没有绑定
            sim_bind.setCheck(false);
        } else {
            //不为空是，设置为绑定
            sim_bind.setCheck(true);
        }
        //3.设置点击事件
        sim_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //4.获取原有的状态
                boolean isCheck = sim_bind.isCheck();
                //5.将原有状态取反（取反是因为开始如果没选择，那么取反后就相当于选中了），设置给当前条目，存储序列卡号
                sim_bind.setCheck(!isCheck);
                if (!isCheck) {
                    //6.如果点击后的是true的话，就存储
                    //1.获得TelephonyManager对象
                    TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    //2.获取sim卡的序列卡号
                    String simNumber = tm.getSimSerialNumber();
                    //3.存储
                    SpUtils.putString(getApplicationContext(), ConstentValue.SIM_NUMBER, simNumber);
                } else {
                    //7.如果点击后的值是false的话,就将存储的值和节点一起删除
                    SpUtils.remove(getApplicationContext(), ConstentValue.SIM_NUMBER);
                }
            }
        });
    }

    @Override
    public void sendToNextPage() {
        String simNumber = SpUtils.getString(this, ConstentValue.SIM_NUMBER, "");
        if (!TextUtils.isEmpty(simNumber)) {
            //选中的话就跳转至下一页
            Intent intent = new Intent(this, NavigationThreeActivity.class);
            startActivity(intent);
            finish();
        } else {
            ToastUtils.show(this, "必须绑定SIM卡");
        }
        //开启平移动画
        overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
    }

    @Override
    public void sendToPrePage() {
        Intent intent = new Intent(this, NavigationFirstActivity.class);
        startActivity(intent);
        finish();
        //开启平移动画
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }
}
