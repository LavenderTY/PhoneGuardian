package com.ty.phoneguardian.ToolActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.db.table.TableUtils;
import com.ty.phoneguardian.Engine.AddressDao;
import com.ty.phoneguardian.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class QueryAddressActivity extends AppCompatActivity {
    @InjectView(R.id.et_get_number)
    EditText etGetNumber;
    @InjectView(R.id.but_query)
    Button butQuery;
    @InjectView(R.id.tv_show_address)
    TextView tvShowAddress;
    private String mAddress;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tvShowAddress.setText(mAddress);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_address);
        ButterKnife.inject(this);
        butQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1.得到EditText的值
                String phone_number = etGetNumber.getText().toString();
                if (!TextUtils.isEmpty(phone_number)) {
                    //2.查询数据库，耗时操作，在子线程中完成
                    query(phone_number);
                } else {
                    //5.抖动
                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                    //Interpolator 插补器，数学函数
                    //自定义插补器
                    /*shake.setInterpolator(new Interpolator() {
                        //y = ax + b
                        @Override
                        public float getInterpolation(float v) {
                            //v --> 相当于x
                            return 0;   //0 --> 相当于y
                        }
                    });*/
                    etGetNumber.startAnimation(shake);
                    //6.震动效果(Vibrator 震动)
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    //2000 震动毫秒数
                    vibrator.vibrate(2000);
                    //规律震动(震动规律(不震动时间，震动时间，不震动时间，震动时间...)，重复震动次数)
                    vibrator.vibrate(new long[]{2000, 5000, 2000, 5000}, -1);
                }
            }
        });
        //4.实时查询(监听输入框变化)
        etGetNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //文本发生改变之后调用
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //文本发生改变时调用
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //文本发生改变之前调用
                query(etGetNumber.getText().toString());
            }
        });
    }

    /**
     * 耗时操作，查询指定号码的归属地
     *
     * @param phone 需要查询的电话号码
     */
    private void query(final String phone) {
        new Thread() {
            @Override
            public void run() {
                mAddress = AddressDao.getAddress(phone);
                //3.查询完毕后，发消息给主线程
                mHandler.sendEmptyMessage(1);
            }
        }.start();
    }
}
