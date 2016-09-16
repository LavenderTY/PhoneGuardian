package com.ty.phoneguardian.Receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.ty.phoneguardian.bean.ConstentValue;
import com.ty.phoneguardian.Service.LocationService;
import com.ty.phoneguardian.Utils.SpUtils;
import com.ty.phoneguardian.Utils.ToastUtils;

/**
 * Created by Lavender on 2016/8/11.
 */
public class SMSReceiver extends BroadcastReceiver {
    private ComponentName mDeviceAdminSample;
    private DevicePolicyManager mDPM;

    @Override
    public void onReceive(Context context, Intent intent) {
        //(上下文环境，广播接收者对应的字节码文件)
        mDeviceAdminSample = new ComponentName(context, DeviceAdmin.class);
        mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        //1.判断是否开启了防盗模式
        boolean isOpen = SpUtils.getBoolean(context, ConstentValue.OPEN_SECURITY, false);
        if (isOpen) {
            //2.获取短信
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            //3.循环遍历短信
            for (Object object : objects) {
                //4.获取短信对象
                SmsMessage sm = SmsMessage.createFromPdu((byte[]) object);
                //5.获取短信对象的基本信心
                String sddress = sm.getDisplayOriginatingAddress();
                String msg = sm.getDisplayMessageBody();
                //6.判断是否包含播放音乐的关键字
                if (msg.contains("#*alarm*#")) {
                    //7.播放音乐
                    MediaPlayer player = new MediaPlayer();
                    player.setLooping(true);
                    player.start();
                }
                if (msg.contains("#*location*#")) {
                    //8.开启服务获取位置
                    context.startService(new Intent(context, LocationService.class));
                }
                if (msg.contains("#*lockscreen*#")) {
                    //9.远程锁屏
                    //是否开启的判断，组件对象可以作为是否激活的判断标志
                    if (mDPM.isAdminActive(mDeviceAdminSample)) {
                        //锁屏
                        mDPM.lockNow();
                        //锁屏同时设置密码
                        mDPM.resetPassword("", 0);
                    } else {
                        ToastUtils.show(context, "请先激活");
                    }
                }
                if (msg.contains("#*wipedata*#")) {
                    //10.数据销毁
                    //是否开启的判断，组件对象可以作为是否激活的判断标志
                    if (mDPM.isAdminActive(mDeviceAdminSample)) {
                        //清除手机数据
                        mDPM.wipeData(0);
                        //清除手机SD卡的数据
                        //mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                    } else {
                        ToastUtils.show(context, "请先激活");
                    }
                }
            }
        }
    }
}
