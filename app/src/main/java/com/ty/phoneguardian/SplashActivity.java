package com.ty.phoneguardian;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.ty.phoneguardian.Activity.MainActivity;
import com.ty.phoneguardian.bean.ConstentValue;
import com.ty.phoneguardian.Utils.SpUtils;
import com.ty.phoneguardian.Utils.StreamUtil;
import com.ty.phoneguardian.Utils.ToastUtils;
import com.ty.phoneguardian.bean.JsonBean;

import net.youmi.android.AdManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {
    /**
     * 更新新版本的状态码
     */
    private static final int UPDATE_VERSION = 101;
    /**
     * 进入应用程序主界面的状态码
     */
    private static final int ENTER_HOME = 102;
    /**
     * url错误的状态码
     */
    private static final int URL_ERROR = 103;
    /**
     * io错误的状态码
     */
    private static final int IO_ERROR = 104;
    private TextView tv_version_name;
    private RelativeLayout rel_splash;
    private int mLocalVersionCode;
    private static final String TAG = "SplashActivity";
    private String mVersionName;
    private String mVersionDes;
    private String mVersionCode;
    private String mDownloadUrl;
    private String mPath;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION:
                    //弹出对话框，提示更新
                    ShowDialogUpdate();
                    break;
                case ENTER_HOME:
                    //进入主界面
                    enterMain();
                    break;
                case URL_ERROR:
                    //URL异常
                    ToastUtils.show(SplashActivity.this, "url异常");
                    enterMain();   //异常不能阻止用户进入主界面
                    break;
                case IO_ERROR:
                    //io异常
                    ToastUtils.show(SplashActivity.this, "io异常");
                    enterMain();   //异常不能阻止用户进入主界面
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉当前Activity的头title
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        //初始化组件
        initUI();
        //初始化数据
        initDate();
        //设置渐隐动画
        initAnimation();
        //初始化数据库
        initDB();
        if (!SpUtils.getBoolean(this, ConstentValue.HAVE_SHORTCUT, false)) {
            //初始化生成快捷方式
            initShortcut();
        }

        //添加广告   true 表示测试，false 表示上线
        AdManager.getInstance(this).init("2f2c31cc7b078bd0", "bbb521418b4757dd",true);
    }

    /**
     * 生成快捷方式
     */
    private void initShortcut() {
        //1.给intent设置图标和名称
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //设置图片
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, android.graphics.BitmapFactory.decodeResource(getResources(), R.mipmap.title));
        //名称
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机卫士");
        //2.点击快捷方式后跳转的Activity
        //创建开启的意图对象
        Intent mainIntent = new Intent("android.intent.action.HOME");
        mainIntent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, mainIntent);
        //3.发送广播
        sendBroadcast(intent);
        //4.告訴sp已經生成了快捷方式
        SpUtils.putBoolean(this, ConstentValue.HAVE_SHORTCUT, true);
    }

    /**
     * 初始化数据库
     */
    private void initDB() {
        //归属地数据库的拷贝过程
        initAddressDB("address.db");
        //常用电话号码数据库的拷贝过程
        initAddressDB("commonnum.db");
        //病毒数据库
        initAddressDB("antivirus.db");
    }

    /**
     * 拷贝数据库至files文件夹下
     *
     * @param dbName 数据库名称
     */
    private void initAddressDB(String dbName) {
        //1.在files文件夹下创建同名(dbName)数据库文件
        File files = getFilesDir();
        File file = new File(files, dbName);
        if (file.exists()) {
            return;
        }
        FileOutputStream ous = null;
        InputStream is = null;
        try {
            //2.输入流读取第三方目录下的文件
            is = getAssets().open(dbName);
            //3.将读取的内容写到指定文件夹的文件中
            ous = new FileOutputStream(file);
            //4.每次读取内容大小
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = is.read(b)) != -1) {
                ous.write(b, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ous != null) {
                try {
                    ous.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 设置渐隐动画
     */
    private void initAnimation() {
        AlphaAnimation aa = new AlphaAnimation(1, 0);
        aa.setDuration(5000); //5秒钟消失
        aa.setFillAfter(true);
        rel_splash.startAnimation(aa);
    }

    /**
     * 弹出对话框,提示用户更新
     */
    private void ShowDialogUpdate() {
        //对话框,是依赖于activity存在的
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        //设置左上角的图标
        builder.setIcon(android.R.drawable.stat_sys_warning);
        builder.setTitle("版本更新");
        //设置描述内容
        builder.setMessage(mVersionDes);
        //确定按钮
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //获取下载的apk链接
                downloadApk();
            }
        });
        //取消按钮
        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                enterMain();
                dialogInterface.dismiss();
            }
        });
        //点击回退的按钮监听
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                //点击回退键，也要让用户进入到主界面
                enterMain();
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 下载APK
     */
    private void downloadApk() {
        //apk下载链接地址，放置apk的所在路径
        //1.判断SD卡是否可用
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //2.获取SD卡的路径
            String pathSD = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getApkName(mPath);
            //3.发送请求，获取APK
            HttpUtils httpUtils = new HttpUtils();
            //4.发送请求，传递参数   pathSD：下载完成后文件的位置
            httpUtils.download(mDownloadUrl, pathSD, new RequestCallBack<File>() {
                //下载成功时调用
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    Log.i(TAG, "下载成功");
                    File file = responseInfo.result;
                    //提示用户安装
                    installApk(file);
                }

                //下载失败时调用
                @Override
                public void onFailure(HttpException e, String s) {
                    Log.i(TAG, "下载失败");
                }

                //刚刚开始下载的方法
                @Override
                public void onStart() {
                    super.onStart();
                    Log.i(TAG, "开始下载");
                }

                //下载过程中的方法(下载文件的总大小，下载的当前进度，是否在下载)
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    Log.i(TAG, "正在下载");
                    Log.i(TAG, "total = " + total);
                    Log.i(TAG, "current = " + current);
                }
            });
        }
    }

    /**
     * 安装对应的APK
     *
     * @param file 安装文件
     */
    private void installApk(File file) {
        //系统应用界面，源码，apk安装入口
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        //文件作为数据源  设置安装类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivityForResult(intent, 0);
    }

    //开启一个activity后,返回结果调用的方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterMain();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 从路径中获得apk的名字
     *
     * @param path 路径
     * @return 返回的是apk的名字
     */
    private static String getApkName(String path) {
        int index = path.lastIndexOf("/");
        return path.substring(index + 1);
    }

    /**
     * 跳转至主界面
     */
    private void enterMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //使欢迎界面只显示一次
        finish();
    }

    /**
     * 初始化数据
     */
    private void initDate() {
        //1.设置版本号
        tv_version_name.setText("版本名称: " + getVersionName());
        //检测（本地版本号和服务器版本号对比）是否有更新，如果有更新，提示用户下载（member）
        //2.获取本地版本号
        mLocalVersionCode = getLocalVersionCode();
        //3.获取服务器的版本号（客户端发请求，服务器给响应（json/xml））
        //http://www.oxxx.com/update74.json?key=value  返回200 请求成功,流的方式将数据读取下来
        //json中内容包含:
        /* 更新版本的版本名称
         * 新版本的描述信息
		 * 服务器版本号
		 * 新版本apk下载地址*/
        if (SpUtils.getBoolean(this, ConstentValue.OPEN_UPDATE, false)) {
            checkVersion();
        } else {
            //发送消息延时5秒后处理进入主界面时间
            mHandler.sendEmptyMessageDelayed(ENTER_HOME, 5000);
        }
    }

    /**
     * 检测版本号
     */
    private void checkVersion() {
        new Thread() {
            @Override
            public void run() {
                mPath = "http://192.168.56.1:8080/UpdateJson.json";
                Message msg = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {
                    //1.发送请求获取数据,参数则为请求json的链接地址
                    //http://192.168.56.1:8080/update74.json	测试阶段不是最优
                    //仅限于模拟器访问电脑tomcat   10.0.2.2
                    URL url = new URL(mPath);
                    //2.打开连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //3.设置常见请求参数(请求头)
                    //默认为get请求
                    conn.setRequestMethod("GET");
                    //读取超时
                    conn.setReadTimeout(2000);
                    //连接超时
                    conn.setConnectTimeout(3000);
                    //连接，可以不要这一句，conn.getResponseCode()运行的时候就会执行conn.connect()
                    //conn.connect();
                    //4.判断返回码，如果为200表示请求成功
                    if (conn.getResponseCode() == 200) {
                        //5.获取输入流，用于从服务器读数据
                        InputStream is = conn.getInputStream();
                        //6.将流转换成字符串（工具类封装）
                        String json = StreamUtil.streamToString(is);
                        Log.i(TAG, json);
                        //7.解析Json
                        //请求成功  接收JSON数据
                        String str = new String(json);
                        //使用GSON来解析json
                        Gson gson = new Gson();
                        JsonBean jb = gson.fromJson(str, JsonBean.class);
                        mVersionName = jb.versionName;
                        mVersionDes = jb.versionDes;
                        mVersionCode = jb.versionCode;
                        mDownloadUrl = jb.downloadUrl;
                        Log.i(TAG, mVersionName);
                        Log.i(TAG, mVersionDes);
                        Log.i(TAG, mVersionCode);
                        Log.i(TAG, mDownloadUrl);
                        //8.对比版本号（服务器版本号>本地版本号,提示用户更新）
                        if (mLocalVersionCode < Integer.parseInt(mVersionCode)) {
                            //提示用户更新（刷新UI）
                            msg.what = UPDATE_VERSION;
                        } else {
                            //进入应用程序
                            msg.what = ENTER_HOME;
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    msg.what = URL_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = IO_ERROR;
                } finally {
                    //发送消息（无论如何，都会发送消息）
                    //指定睡眠时间，请求网络的时间大有或等于5s，则不处理
                    //不足5s，则强制睡眠4s
                    long endTime = System.currentTimeMillis();
                    if ((endTime - startTime) < 5000) {
                        try {
                            Thread.sleep(5000 - (endTime - startTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 初始化组件(UI)
     */
    private void initUI() {
        tv_version_name = (TextView) findViewById(R.id.tv_version_name);
        rel_splash = (RelativeLayout) findViewById(R.id.rel_splash);
    }

    /**
     * 获取版本号：从清单文件中得到
     *
     * @return 返回null的话表示有异常
     */
    public String getVersionName() {
        //1.获取 PackageManager（包管理者对象）
        PackageManager pm = getPackageManager();
        try {
            //2.获取指定包中的基本信息（版本号，版本名称）,传0代表基本信息
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            //3.从基本信息中获取版本名称
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取本地的版本号
     *
     * @return 返回非0表示已经获取到
     */
    public int getLocalVersionCode() {
        //1.获取 PackageManager（包管理者对象）
        PackageManager pm = getPackageManager();
        try {
            //2.获取指定包中的基本信息（版本号，版本名称）,传0代表基本信息
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            //3.从基本信息中获取版本名称
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
