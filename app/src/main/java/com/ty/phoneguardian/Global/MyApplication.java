package com.ty.phoneguardian.Global;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by Lavender on 2016/8/18.
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        //捕获全局的(应用任意模块)异常
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                //获取到了未捕获的异常后，处理的方法
                throwable.printStackTrace();
                Log.i(TAG, "捕获到了异常");

                //将捕获的异常存储到sd卡上面
                String path = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + ".errortylog";
                System.out.println(path);
                File file = new File(path);
                try {
                    PrintWriter pw = new PrintWriter(file);
                    throwable.printStackTrace(pw);
                    pw.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                //上传到服务器
                //退出应用
                System.exit(0);
            }
        });
    }
}
