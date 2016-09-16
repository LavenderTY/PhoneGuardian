package com.ty.phoneguardian.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Lavender on 2016/8/9.
 * 能够获取焦点的自定义的TextView
 */
public class FocusTextView extends TextView {
    //使用在通过java代码来创建控件
    public FocusTextView(Context context) {
        super(context);
    }

    //由系统调用（带属性+上下文环境的构造方法）
    public FocusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //由系统调用（带属性+上下文环境+布局文件中定义样式文件的构造方法）
    public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //重写获取焦点的方法，有系统调用，调用时系统默认获取焦点
    @Override
    public boolean isFocused() {
        return true; //不管怎样都要获取焦点
    }
}
