package com.ty.phoneguardian.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.ty.phoneguardian.bean.ConstentValue;
import com.ty.phoneguardian.Engine.AddressDao;
import com.ty.phoneguardian.R;
import com.ty.phoneguardian.Utils.SpUtils;

/**
 * Created by Lavender on 2016/8/12.
 */
public class AddressService extends Service {
    private static final String TAG = "AddressService";
    private TelephonyManager mTm;
    private MyPhoneStateListener mPst;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private View mViewToast;
    private WindowManager mWm;
    private TextView tv_toast_view;
    private String mAddress;
    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tv_toast_view.setText(mAddress);
        }
    };
    private int[] mDrawablePic;
    private int startX;
    private int startY;
    private WindowManager mWM;
    private int screenHight;
    private int screenWidth;
    private IntentFilter mFilter;
    private InnerOutCallReceiver mInnerOutCallReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //第一次开启服务，管理吐司，电话状态的监听
    @Override
    public void onCreate() {
        super.onCreate();
        //1.电话管理者对象(服务开启式，就监听，关闭时就不坚挺)
        mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mPst = new MyPhoneStateListener();
        //2.监听电话状态
        mTm.listen(mPst, PhoneStateListener.LISTEN_CALL_STATE);
        //获取窗体对象
        mWm = (WindowManager) getSystemService(WINDOW_SERVICE);

        //监听播出电话的广播过滤条件
        mFilter = new IntentFilter();
        //ACTION_NEW_OUTGOING_CALL 播出电话的Action
        mFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        //创建广播接收者
        mInnerOutCallReceiver = new InnerOutCallReceiver();
        //注册广播监听者
        registerReceiver(mInnerOutCallReceiver, mFilter);
    }

    //接收到广播后，需要显示自定义的吐司，显示播出归属地
    private class InnerOutCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取号码
            String phone = getResultData();
            //显示号码
            showToast(phone);
        }
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        //3.手动重写，状态发生改变时会触发
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    //空闲状态(关闭吐司)
                    Log.i(TAG, "空闲状态");
                    //挂断电话，移除吐司
                    if (mWm != null && mViewToast != null) {
                        mWm.removeView(mViewToast);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机
                    Log.i(TAG, "摘机状态");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //响铃(显示吐司)
                    Log.i(TAG, "响铃状态");
                    showToast(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    public void showToast(String incomingNumber) {
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE   //不能获得焦点
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE      //默认能被触摸
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;      //屏幕亮的时候显示
        params.format = PixelFormat.TRANSLUCENT;    //样式是透明
        //在响铃的时候显示吐司，和电话类型一致
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        //指定吐司的所在位置（将吐司指定在左上角）
        params.gravity = Gravity.LEFT + Gravity.TOP;
        //吐司显示效果，将吐司挂载到窗体上
        mViewToast = View.inflate(this, R.layout.toast_view, null);
        tv_toast_view = (TextView) mViewToast.findViewById(R.id.tv_toast_view);

        //得到屏幕的宽度
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        screenHight = mWM.getDefaultDisplay().getHeight();
        screenWidth = mWM.getDefaultDisplay().getWidth();

        //触摸事件（按下(一次),移动(多次),抬起(一次)）
        mViewToast.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //按下
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //移动
                        //移动距离x与y的距离
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        //偏移量
                        int disX = moveX - startX;
                        int disY = moveY - startY;

                        //获取各个方向的位置
                        params.x = params.x + disX;  //x坐标
                        params.y = params.y + disY;    //y坐标

                        //容错处理（ivDrag不能超出屏幕）
                        //下边缘(屏幕的高度 - 22 = 底边缘显示的最大值)
                        //要用移动后的坐标来判断
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > screenWidth - mViewToast.getWidth()) {
                            params.x = screenWidth - mViewToast.getWidth();
                        }
                        if (params.y > screenHight - mViewToast.getHeight() - 22) {
                            params.y = screenHight - mViewToast.getHeight() - 22;
                        }
                        //将tv_toast_view设置到移动后的位置
                        mWm.updateViewLayout(mViewToast, params);

                        //每次移动后，都要给起始坐标重新赋值
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;

                    case MotionEvent.ACTION_UP:
                        //抬起的时候，记录坐标位置
                        SpUtils.putInt(getApplicationContext(), ConstentValue.LOCATION_X, params.x);
                        SpUtils.putInt(getApplicationContext(), ConstentValue.LOCATION_Y, params.y);
                        break;
                }
                //若只响应这个事件，则返回true
                //若既要响应触摸事件，又要响应点击事件，就要返回false
                return true;
            }
        });

        //读取sp中存储的x、y的值
        //params.x 为吐司左上角的x的位置
        params.x = SpUtils.getInt(getApplicationContext(), ConstentValue.LOCATION_X, 0);
        params.y = SpUtils.getInt(getApplicationContext(), ConstentValue.LOCATION_Y, 0);

        //从sp中获取文字的索引，匹配图片
        mDrawablePic = new int[]{R.drawable.call_locate_white, R.drawable.call_locate_orange,
                R.drawable.call_locate_blue, R.drawable.call_locate_gray, R.drawable.call_locate_green};
        int index = SpUtils.getInt(this, ConstentValue.TOAST_STYLE, 0);
        tv_toast_view.setBackgroundResource(mDrawablePic[index]);

        mWm.addView(mViewToast, params);
        //查询归属地
        query(incomingNumber);
    }

    private void query(final String incomingNumber) {
        new Thread() {
            @Override
            public void run() {
                mAddress = AddressDao.getAddress(incomingNumber);
                mHanlder.sendEmptyMessage(1);
            }
        }.start();
    }

    //销毁吐司
    @Override
    public void onDestroy() {
        //取消对话框状态监听(开启服务是监听电话的对象)
        if (mTm != null && mPst != null) {
            mTm.listen(mPst, PhoneStateListener.LISTEN_NONE);
        }
        //销毁广播接收者
        if (mInnerOutCallReceiver != null) {
            unregisterReceiver(mInnerOutCallReceiver);
        }
        super.onDestroy();
    }
}
