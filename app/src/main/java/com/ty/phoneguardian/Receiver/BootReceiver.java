package com.ty.phoneguardian.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ty.phoneguardian.bean.ConstentValue;
import com.ty.phoneguardian.Utils.SpUtils;

/**
 * Created by Lavender on 2016/8/11.
 * 发送短信的广播
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //一旦监听到开机广播，就要发送短信给指定号码
        Log.i(TAG, "在这里判断sim是否已切换");
        //1.获取本地存储的sim卡
        String phone = SpUtils.getString(context, ConstentValue.SIM_NUMBER, "");
        //2.获取当前插入的sim卡的序列号
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String serialNumber = tm.getSimSerialNumber();
        //3.两个序列号进行对比
        if (!serialNumber.equals(phone)) {
            //4.序列号不一致，则发短信
            SmsManager sm = SmsManager.getDefault();
            //倒数第二参数是发送完成的意图   最后一个参数是接收到短信的意图
            sm.sendTextMessage(phone, null, "sim Change!", null, null);
        }
    }
}
