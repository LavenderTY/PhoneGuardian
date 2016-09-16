package com.ty.phoneguardian.SetNavigation;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ty.phoneguardian.Activity.BaseNavigationActivity;
import com.ty.phoneguardian.Activity.ContactActivity;
import com.ty.phoneguardian.bean.ConstentValue;
import com.ty.phoneguardian.R;
import com.ty.phoneguardian.Utils.SpUtils;
import com.ty.phoneguardian.Utils.ToastUtils;

public class NavigationThreeActivity extends BaseNavigationActivity {
    private EditText et_sim_number;
    private Button but_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_three);
        initUI();
    }

    private void initUI() {
        et_sim_number = (EditText) findViewById(R.id.et_sim_number);
        but_content = (Button) findViewById(R.id.but_content);
        but_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NavigationThreeActivity.this, ContactActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        //获取电话
        String phone = SpUtils.getString(this, ConstentValue.CONTACT_PHONE, "");
        //回显电话号码
        et_sim_number.setText(phone);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //出错的情况就是，Activity没有携带数据，所以data为null
        if (data != null) {
            //1.返回当前界面时，接收结果的方法
            String phone = data.getStringExtra("phone");
            //2.过滤特殊字符
            phone = phone.replace("-", "").replace(" ", "").trim();
            et_sim_number.setText(phone);
            //3.保存密码
            SpUtils.putString(this, ConstentValue.CONTACT_PHONE, phone);
        }
    }

    @Override
    public void sendToNextPage() {
        //获得输入框中的联系人
        String phone = et_sim_number.getText().toString();
        if (!TextUtils.isEmpty(phone)) {
            Intent intent = new Intent(this, NavigationFourActivity.class);
            startActivity(intent);
            finish();

            //如果是输入的电话号码，则需要保存
            SpUtils.putString(this, ConstentValue.CONTACT_PHONE, phone);
        }else{
            ToastUtils.show(this,"请添加联系人号码");
        }
        //开启平移动画
        overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
    }

    @Override
    public void sendToPrePage() {
        Intent intent = new Intent(this, NavigationSecondActivity.class);
        startActivity(intent);
        finish();
        //开启平移动画
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }
}
