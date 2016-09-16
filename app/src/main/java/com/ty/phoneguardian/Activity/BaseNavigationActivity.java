package com.ty.phoneguardian.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by Lavender on 2016/8/11.
 */
public abstract class BaseNavigationActivity extends Activity {
    public GestureDetector gesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //3.创建收拾识别器的对象
        gesture = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            //4.重写手势识别器中，包含按下点和抬起点在移动过程中的方法
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //e1  起始点（按下点）
                //e2  抬起点
                if (e1.getRawX() - e2.getRawX() > 100) {
                    //下一页，由右向左滑动
                    //这里是界面跳转操作
                    sendToNextPage();
                }
                if (e2.getRawX() - e1.getRawX() > 100) {
                    //上一页，由左向右滑动
                    //页面跳转操作
                    sendToPrePage();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    //1.监听当前Actvity上的触摸事件（按下(一次),滑动(多次),抬起(一次)）
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //2.通过手势识别器去识别不同的事件类型，做逻辑
        gesture.onTouchEvent(event);   //将Activity中的手机移动操作，交由手势识别器处理
        return super.onTouchEvent(event);
    }

    //抽象方法，跳转到下一页的方法
    public abstract void sendToNextPage();

    //抽象方法，跳转到上一页的方法
    public abstract void sendToPrePage();

    //统一处理跳转至下一页的方法
    public void nextPage(View v) {
        sendToNextPage();
    }

    //统一处理跳转至上一页的方法
    public void upPage(View v) {
        sendToPrePage();
    }
}
