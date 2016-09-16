package com.ty.phoneguardian.Utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Lavender on 2016/8/8.
 */
public class ToastUtils {
    /**
     * 打印吐司
     * @param context  上下文对象
     * @param msg      弹出信息
     */
    public static void show(Context context,String msg){
        Toast.makeText(context,msg,0).show();
    }
}
