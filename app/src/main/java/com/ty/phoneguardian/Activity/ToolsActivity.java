package com.ty.phoneguardian.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ty.phoneguardian.Engine.SmsBackUp;
import com.ty.phoneguardian.R;
import com.ty.phoneguardian.ToolActivity.QueryAddressActivity;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ToolsActivity extends AppCompatActivity {
    @InjectView(R.id.address_query)
    TextView addressQuery;
    @InjectView(R.id.copy_message)
    TextView copyMessage;
    @InjectView(R.id.query_often_use_number)
    TextView queryOftenUseNumber;
    @InjectView(R.id.program_lock)
    TextView programLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
        ButterKnife.inject(this);
        addressQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ToolsActivity.this, QueryAddressActivity.class));
            }
        });
        initCopyMsg();
        initCommonNumberQuery();
        //初始化程序锁
        initAppLock();
    }

    /**
     * 程序锁
     */
    private void initAppLock() {
        programLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AppLockActivity.class));
            }
        });
    }

    /**
     * 常用号码查询
     */
    private void initCommonNumberQuery() {
        queryOftenUseNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CommonNumberQueryActivity.class));
            }
        });
    }

    /**
     * 短信备份
     */
    private void initCopyMsg() {
        copyMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSmsBackUpDialog();
            }
        });
    }

    private void showSmsBackUpDialog() {
        //1.创建一个带进度条的对话框
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIcon(R.mipmap.start);
        dialog.setTitle("短信备份");
        //2.指定进度条为水平
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //3.展示进度条
        dialog.show();
        //4.直接调用短信的方法
        new Thread(){
            @Override
            public void run() {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"sms.xml";
                SmsBackUp.backUp(getApplicationContext(), path, new SmsBackUp.CallBack() {
                    @Override
                    public void setMax(int max) {
                        dialog.setMax(max);
                    }

                    @Override
                    public void setProgress(int index) {
                        dialog.setProgress(index);
                    }
                });
                dialog.dismiss();
            }
        }.start();
    }
}
