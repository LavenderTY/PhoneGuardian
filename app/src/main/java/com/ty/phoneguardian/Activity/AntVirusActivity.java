package com.ty.phoneguardian.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ty.phoneguardian.Engine.VirusDao;
import com.ty.phoneguardian.R;
import com.ty.phoneguardian.Utils.Md5Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class AntVirusActivity extends Activity {
    private ImageView iv_act_scanning;
    private TextView tv_scanning_name;
    private ProgressBar pb_scanning_bar;
    private LinearLayout ll_add_tv;
    private List<ScanInfo> mVirusList;
    private int index = 0;  //用于更新进度条
    private static final int SCANING = 100;
    private static final int SCAN_FINISH = 101;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCANING:
                    //1.显示正在扫描应用的名称
                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                    tv_scanning_name.setText(scanInfo.name);
                    //2.在线性布局中添加一个正在扫描应用的TextView
                    TextView tv = new TextView(getApplicationContext());
                    //判断是否是病毒
                    if (scanInfo.isVirus) {
                        //是病毒
                        tv.setTextColor(Color.RED);
                        tv.setText("发现病毒: " + scanInfo.name);
                    } else {
                        //不是病毒
                        tv.setTextColor(Color.BLACK);
                        tv.setText("扫描安全: " + scanInfo.name);
                    }
                    ll_add_tv.addView(tv, 0);
                    break;
                case SCAN_FINISH:
                    tv_scanning_name.setText("扫描完成");
                    //停止正在旋转的动画
                    iv_act_scanning.clearAnimation();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ant_virus);
        initUI();
        initAnimation();
        checkVirus();
        unInstallVirus();
    }

    /**
     * 卸载应用的方法
     */
    private void unInstallVirus() {
        if (mVirusList != null) {
            for (ScanInfo info : mVirusList) {
                String packageName = info.packageName;
                //卸载
                Intent intent = new Intent("android.intent.action.DELETE");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }
    }

    /**
     * 查看病毒的方法
     */
    private void checkVirus() {
        new Thread() {
            @Override
            public void run() {
                //获取数据库中所有的病毒的md5码
                List<String> virusList = VirusDao.getVirusList();
                //获取手机上的所有应用程序签名文件的md5码
                //1.获得包管理器
                PackageManager pm = getPackageManager();
                //2.获取所有应用程序的签名文件
                //PackageManager.GET_SIGNATURES  已安装应用的签名文件
                //PackageManager.GET_UNINSTALLED_PACKAGES  卸载完的应用，残留文件
                List<PackageInfo> packageInfoList = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
                //创建病毒的集合
                mVirusList = new ArrayList<ScanInfo>();
                //记录所有应用的集合
                List<ScanInfo> screenInfoList = new ArrayList<ScanInfo>();

                //设置进度条的最大值
                pb_scanning_bar.setMax(packageInfoList.size());
                System.out.println(packageInfoList.size());
                //3.遍历应用集合
                for (PackageInfo info : packageInfoList) {
                    ScanInfo scanInfo = new ScanInfo();
                    //获取签名文件的数组
                    Signature[] signatures = info.signatures;
                    //获取签名文件数组的第一位,然后进行md5,将此md5和数据库中的md5比对
                    Signature signature = signatures[0];
                    String string = signature.toCharsString();
                    //32位字符串,16进制字符(0-f)
                    String encoder = Md5Utils.encoder(string);
                    //4,比对应用是否为病毒
                    if (virusList.contains(encoder)) {
                        //5.记录病毒
                        scanInfo.isVirus = true;
                        mVirusList.add(scanInfo);
                    } else {
                        scanInfo.isVirus = false;
                    }

                    //6.获取对象的名字和包名
                    scanInfo.packageName = info.packageName;
                    scanInfo.name = info.applicationInfo.loadLabel(pm).toString();
                    screenInfoList.add(scanInfo);

                    //7.扫描的过程中，更新进度条
                    index++;
                    pb_scanning_bar.setProgress(index);
                    try {
                        Thread.sleep(50 + new Random().nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //8.子线程中发消息，更新UI
                    Message message = Message.obtain();
                    message.obj = scanInfo;
                    message.what = SCANING;
                    mHandler.sendMessage(message);
                }
                Message message = Message.obtain();
                message.what = SCAN_FINISH;
                mHandler.sendMessage(message);
            }
        }.start();
    }

    class ScanInfo {
        public boolean isVirus;
        public String packageName;
        private String name;
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(1000);
        //指定动画一直旋转
        ra.setRepeatCount(RotateAnimation.INFINITE);
        ra.setFillAfter(true);
        iv_act_scanning.startAnimation(ra);
    }

    /**
     * 初始化控件
     */
    private void initUI() {
        tv_scanning_name = (TextView) findViewById(R.id.tv_scanning_name);
        iv_act_scanning = (ImageView) findViewById(R.id.iv_act_scanning);
        ll_add_tv = (LinearLayout) findViewById(R.id.ll_add_tv);
        pb_scanning_bar = (ProgressBar) findViewById(R.id.pb_scanning_bar);
    }
}
