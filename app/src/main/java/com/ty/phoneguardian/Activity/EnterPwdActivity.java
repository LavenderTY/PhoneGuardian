package com.ty.phoneguardian.Activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ty.phoneguardian.R;
import com.ty.phoneguardian.Utils.Md5Utils;
import com.ty.phoneguardian.Utils.SpUtils;
import com.ty.phoneguardian.Utils.ToastUtils;
import com.ty.phoneguardian.bean.ConstentValue;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EnterPwdActivity extends AppCompatActivity {
    @InjectView(R.id.tv_intercept_name)
    TextView tvInterceptName;
    @InjectView(R.id.iv_intercept_icon)
    ImageView ivInterceptIcon;
    @InjectView(R.id.ed_intercept_pwd)
    EditText edInterceptPwd;
    @InjectView(R.id.but_intercept)
    Button butIntercept;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pwd);
        ButterKnife.inject(this);
        packageName = getIntent().getStringExtra("packageName");
        initDate();
    }

    /**
     * 初始化数据
     */
    private void initDate() {
        //通过传递过来的包名获取拦截对象的包名和图标
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            Drawable icon = applicationInfo.loadIcon(pm);
            ivInterceptIcon.setBackgroundDrawable(icon);
            tvInterceptName.setText(applicationInfo.loadLabel(pm).toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        butIntercept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass = edInterceptPwd.getText().toString();
                if (!TextUtils.isEmpty(pass)) {
                    String pwd = SpUtils.getString(getApplicationContext(), ConstentValue.MOBILE_SAFE_PSD, "");
                    if (Md5Utils.encoder(pass).equals(pwd)) {
                        //解锁，进入应用
                        //解锁后告诉看门狗不监听这个应用（发广播实现）
                        Intent intent = new Intent("android.intent.action.SKIP");
                        intent.putExtra("packageName", packageName);
                        sendBroadcast(intent);
                        finish();
                    } else {
                        ToastUtils.show(getApplicationContext(), "密码输入错误");
                    }
                } else {
                    ToastUtils.show(getApplicationContext(), "请输入密码");
                }
            }
        });
    }

    //重写回退按钮
    @Override
    public void onBackPressed() {
        //回退按钮，点击直接跳转到桌面
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
