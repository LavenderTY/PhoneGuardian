package com.ty.phoneguardian.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ty.phoneguardian.bean.ConstentValue;
import com.ty.phoneguardian.R;
import com.ty.phoneguardian.Utils.SpUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ToastLocationActivity extends Activity {

    @InjectView(R.id.iv_drag)
    ImageView ivDrag;
    @InjectView(R.id.but_top)
    Button butTop;
    @InjectView(R.id.but_bottom)
    Button butBottom;
    private int startX;
    private int startY;
    private WindowManager mWM;
    private int screenHight;
    private int screenWidth;
    private long[] mHits = new long[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_location);
        ButterKnife.inject(this);
        initListener();
    }

    /**
     * 事件处理函数
     */
    private void initListener() {
        //得到屏幕的宽度
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        screenHight = mWM.getDefaultDisplay().getHeight();
        screenWidth = mWM.getDefaultDisplay().getWidth();

        //回显操作
        //获取当前的坐标
        int locationX = SpUtils.getInt(this, ConstentValue.LOCATION_X, 0);
        int locationY = SpUtils.getInt(this, ConstentValue.LOCATION_Y, 0);

        //左上角的坐标作用在ivDrag上
        //ivDrag在什么布局中，位置设置就按照其提供方法
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = locationX;
        params.topMargin = locationY;
        if (locationY > (screenHight - 22) / 2) {
            butTop.setVisibility(View.VISIBLE);
            butBottom.setVisibility(View.INVISIBLE);
        } else {
            butTop.setVisibility(View.INVISIBLE);
            butBottom.setVisibility(View.VISIBLE);
        }
        //将上面的设置设置给ivDrag
        ivDrag.setLayoutParams(params);
        /*
        1.原数组（要被拷贝的数组）
        2.原数组的拷贝起始位置索引
        3.目标数组（原数组的数据 -- 拷贝  -->  目标数组）
        4.目标数组接收值得起始位置索引
        5.拷贝长度
         */
        //处理点击事件,是在抬起的时候调用的
        ivDrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[mHits.length - 1] - mHits[0] < 500) {
                    int left = screenWidth / 2 - ivDrag.getWidth()/2;
                    int top = (screenHight - 22) / 2 - ivDrag.getHeight()/2;
                    int right = screenWidth / 2 + ivDrag.getWidth()/2;
                    int bottom = (screenHight - 22) / 2 + ivDrag.getHeight()/2;

                    ivDrag.layout(left, top, right, bottom);
                    //记录位置
                    SpUtils.putInt(getApplicationContext(), ConstentValue.LOCATION_X, ivDrag.getLeft());
                    SpUtils.putInt(getApplicationContext(), ConstentValue.LOCATION_Y, ivDrag.getTop());
                }
            }
        });

        //触摸事件（按下(一次),移动(多次),抬起(一次)）
        ivDrag.setOnTouchListener(new View.OnTouchListener() {
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
                        int left = ivDrag.getLeft() + disX;  //左侧坐标
                        int top = ivDrag.getTop() + disY;    //上面的坐标
                        int right = ivDrag.getRight() + disX;  //右侧坐标
                        int bottom = ivDrag.getBottom() + disY;   //下面的坐标

                        //容错处理（ivDrag不能超出屏幕）
                        //下边缘(屏幕的高度 - 22 = 底边缘显示的最大值)
                        //要用移动后的坐标来判断
                        if (left < 0 || top < 0 || right > screenWidth || bottom > screenHight - 22) {
                            return true;
                        }
                        //上面与下面按钮的显示判断
                        if (top > (screenHight - 22) / 2) {
                            butTop.setVisibility(View.VISIBLE);
                            butBottom.setVisibility(View.INVISIBLE);
                        } else {
                            butTop.setVisibility(View.INVISIBLE);
                            butBottom.setVisibility(View.VISIBLE);
                        }

                        //将ivDrag设置到移动后的位置
                        ivDrag.layout(left, top, right, bottom);

                        //每次移动后，都要给起始坐标重新赋值
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;

                    case MotionEvent.ACTION_UP:
                        //抬起的时候，记录坐标位置
                        SpUtils.putInt(getApplicationContext(), ConstentValue.LOCATION_X, ivDrag.getLeft());
                        SpUtils.putInt(getApplicationContext(), ConstentValue.LOCATION_Y, ivDrag.getTop());
                        break;
                }
                //若只响应这个事件，则返回true
                //若既要响应触摸事件，又要响应点击事件，就要返回false
                return false;
            }
        });
    }
}

