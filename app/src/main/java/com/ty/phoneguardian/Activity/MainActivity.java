package com.ty.phoneguardian.Activity;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ty.phoneguardian.Utils.ServiceUtils;
import com.ty.phoneguardian.bean.ConstentValue;
import com.ty.phoneguardian.R;
import com.ty.phoneguardian.Utils.Md5Utils;
import com.ty.phoneguardian.Utils.SpUtils;
import com.ty.phoneguardian.Utils.ToastUtils;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;

public class MainActivity extends AppCompatActivity {
    private GridView gv_home;
    private String[] mTitleStr;
    private int[] mDrablePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 实例化广告条
        AdView adView = new AdView(this, AdSize.FIT_SCREEN);
        // 获取要嵌入广告条的布局
        LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
        // 将广告条加入到布局中
        adLayout.addView(adView);

        //初始化组件
        initUI();
        //初始化数据
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mTitleStr = new String[]{"手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};
        mDrablePic = new int[]{
                R.mipmap.home_safe, R.mipmap.home_callmsgsafe, R.mipmap.home_apps,
                R.mipmap.home_taskmanager, R.mipmap.home_netmanager, R.mipmap.home_trojan,
                R.mipmap.home_sysoptimize, R.mipmap.home_tools, R.mipmap.home_settings
        };
        //给GridView设置适配器
        gv_home.setAdapter(new MyAdapter());
        //给GridView设置条目监听
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        showDialog();
                        break;
                    case 1:
                        startActivity(new Intent(getApplicationContext(), BlacklistInterceptActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(getApplicationContext(), AppManagerActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(getApplicationContext(), ProgressManagerActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(getApplicationContext(), TrafficActivity.class));
                        break;
                    case 5:
                        //手机杀毒
                        startActivity(new Intent(getApplicationContext(), AntVirusActivity.class));
                        break;
                    case 6:
                        //清理缓存
                        startActivity(new Intent(getApplicationContext(), BaseCacheClearActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(getApplicationContext(), ToolsActivity.class));
                        break;
                    case 8:
                        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                        break;
                }
            }
        });
    }

    private void showDialog() {
        //判断本地是否有存储密码
        String pass = SpUtils.getString(this, ConstentValue.MOBILE_SAFE_PSD, "");
        if (TextUtils.isEmpty(pass)) {
            //如果密码为空,则弹出设置密码对话框
            showSetPsdDialog();
        } else {
            //密码不为空,则弹出确认密码对话框
            showConfirmPsdDialog();
        }
    }

    /**
     * 确认密码对话框
     */
    private void showConfirmPsdDialog() {
        //这里要用自己定义的对话框样式
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View v = View.inflate(this, R.layout.confirm_psd_dialog, null);
        //让对话框显示自己定义的View样式
        dialog.setView(v);
        //为兼容低版本，给布局文件设置外边距的时候，忽略内边距（系统设置的）
        dialog.setView(v, 0, 0, 0, 0);
        dialog.show();
        Button but_yes = (Button) v.findViewById(R.id.but_yes);
        Button but_no = (Button) v.findViewById(R.id.but_no);

        //确认按钮事件
        but_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et_confirms_psd = (EditText) v.findViewById(R.id.et_confirms_psd);
                String psd = et_confirms_psd.getText().toString();
                if (!TextUtils.isEmpty(psd)) {
                    //将存储在sp中的32位密码获取出来
                    String getPsd = SpUtils.getString(getApplicationContext(), ConstentValue.MOBILE_SAFE_PSD, "");
                    //判断两次密码是否一致，将输入的密码进行MD5加密
                    if (Md5Utils.encoder(psd).equals(getPsd)) {
                        //进入防盗模式
                        Intent intent = new Intent(MainActivity.this, SetOverActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    } else {
                        ToastUtils.show(MainActivity.this, "确认密码错误");
                    }
                } else {
                    ToastUtils.show(MainActivity.this, "密码不能为空");
                }
            }
        });

        //取消按钮事件
        but_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 设置密码对话框
     */
    private void showSetPsdDialog() {
        //这里要用自己定义的对话框样式
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View v = View.inflate(this, R.layout.show_psd_dialog, null);
        //让对话框显示自己定义的View样式
        dialog.setView(v);
        //为兼容低版本，给布局文件设置外边距的时候，忽略内边距（系统设置的）
        dialog.setView(v, 0, 0, 0, 0);
        dialog.show();
        Button but_ok = (Button) v.findViewById(R.id.but_ok);
        Button but_cancel = (Button) v.findViewById(R.id.but_cancel);

        //确认按钮事件
        but_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et_set_psd = (EditText) v.findViewById(R.id.et_set_psd);
                EditText et_confirm_psd = (EditText) v.findViewById(R.id.et_confirm_psd);
                String psd = et_set_psd.getText().toString();
                String rpsd = et_confirm_psd.getText().toString();
                if (!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(rpsd)) {
                    //判断两次密码是否一致
                    if (psd.equals(rpsd)) {
                        //进入防盗模式
                        Intent intent = new Intent(MainActivity.this, SetOverActivity.class);
                        startActivity(intent);
                        dialog.dismiss();

                        SpUtils.putString(getApplicationContext(), ConstentValue.MOBILE_SAFE_PSD, Md5Utils.encoder(psd));
                    } else {
                        ToastUtils.show(MainActivity.this, "两次密码不一致");
                    }
                } else {
                    ToastUtils.show(MainActivity.this, "密码不能为空");
                }
            }
        });

        //取消按钮事件
        but_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 初始化组件
     */
    private void initUI() {
        gv_home = (GridView) findViewById(R.id.gv_home);
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            //条目的总数
            return mTitleStr.length;
        }

        @Override
        public Object getItem(int i) {
            //条目
            return mDrablePic[i];
        }

        @Override
        public long getItemId(int i) {
            //条目的id
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = null;
            ViewHolder mHolder;
            if (view == null) {
                v = View.inflate(MainActivity.this, R.layout.show_item, null);
                mHolder = new ViewHolder();
                mHolder.iv_show_pic = (ImageView) v.findViewById(R.id.iv_show_pic);
                mHolder.tv_show_title = (TextView) v.findViewById(R.id.tv_show_title);
                v.setTag(mHolder); //设置一个标签，将一个对象存到View中，是一个Object类型
            } else {
                v = view;
                mHolder = (ViewHolder) v.getTag();
            }
            mHolder.iv_show_pic.setBackgroundResource(mDrablePic[i]);
            mHolder.tv_show_title.setText(mTitleStr[i]);
            return v;
        }
    }

    /**
     * 把条目需要使用到的所有组件封装到这个类中
     */
    class ViewHolder {
        private ImageView iv_show_pic;
        private TextView tv_show_title;
    }

    @Override
    protected void onPause() {
        super.onPause();
        boolean isOpen = ServiceUtils.isRunning(this, "com.ty.phoneguardian.Service.RocketService");
        if (isOpen) {
            finish();
        }
    }
}
