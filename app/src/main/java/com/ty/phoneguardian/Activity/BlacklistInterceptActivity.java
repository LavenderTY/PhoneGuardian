package com.ty.phoneguardian.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ty.phoneguardian.bean.BlackListInfo;
import com.ty.phoneguardian.Dao.BlackListDao;
import com.ty.phoneguardian.R;
import com.ty.phoneguardian.Utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BlacklistInterceptActivity extends AppCompatActivity {
    @InjectView(R.id.blacklist_add)
    Button blacklistAdd;
    @InjectView(R.id.lv_blacklist_number)
    ListView lvBlacklistNumber;
    private BlackListDao mDao;
    private List<BlackListInfo> list;
    private int mode = 1;
    private MyAdapter adapter;
    private boolean mIsLode = false;   //防止重复加载
    private int mCount = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (adapter == null) {
                adapter = new MyAdapter();
                lvBlacklistNumber.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist_intercept);
        ButterKnife.inject(this);
        initData();
        initBut();
    }

    /**
     * 按钮事件
     */
    private void initBut() {
        blacklistAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        //监听其滚动状态
        lvBlacklistNumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                //状态发生改变时调用
                //OnScrollListener.SCROLL_STATE_FLING  飞速滚动
                //OnScrollListener.SCROLL_STATE_IDLE   空闲状态
                //OnScrollListener.SCROLL_STATE_TOUCH_SCROLL  拿手触摸着去滚动状态
                //条件一：滚动到停止状态
                //条件二：最后一个条目可见（最后一个条目的索引值>=数据适配器中集合的总条目数-1）
                if (list != null) {
                    if (i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lvBlacklistNumber.getFirstVisiblePosition() >= list.size() - 1 && !mIsLode) {
                        //加载下一页数据
                        if (mCount > list.size()) {
                            //获取数据库中的所有号码
                            new Thread() {
                                @Override
                                public void run() {
                                    //1.获取操作黑名单数据库的对象
                                    mDao = new BlackListDao(getApplicationContext());
                                    //2.查询数据
                                    List<BlackListInfo> moreData = list = mDao.findItem(list.size());
                                    //添加下一页数据
                                    list.addAll(moreData);
                                    //4.通知数据适配器刷新
                                    handler.sendEmptyMessage(1);
                                }
                            }.start();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View v = View.inflate(getApplicationContext(), R.layout.add_blcaklist_dialog, null);
        dialog.setView(v, 0, 0, 0, 0);
        final EditText et_blacklist_phone = (EditText) v.findViewById(R.id.et_blacklist_phone);
        RadioGroup rg_group = (RadioGroup) v.findViewById(R.id.rg_group);
        Button but_blacklist_ok = (Button) v.findViewById(R.id.but_blacklist_ok);
        Button but_blacklist_cancel = (Button) v.findViewById(R.id.but_blacklist_cancel);

        //监听选中条目的切换过程
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_sms:
                        //拦截短信
                        mode = 1;
                        break;
                    case R.id.rb_phone:
                        //拦截电话
                        mode = 2;
                        break;
                    case R.id.rb_all:
                        //拦截所有
                        mode = 3;
                        break;
                }
            }
        });
        but_blacklist_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1.获取输入框的电话号码
                String phone = et_blacklist_phone.getText().toString();
                if (!TextUtils.isEmpty(phone)) {
                    //2.向数据库中插入号码
                    mDao.insert(phone, mode + "");
                    //3.让数据库和集合保持一致(方法：1.数据库中重新读一遍数据，2.手动向集合中添加一个对象(插入数据构建的对象))
                    BlackListInfo info = new BlackListInfo();
                    info.phone = phone;
                    info.mode = mode + "";
                    //4.将数据插入到集合的最顶端
                    list.add(0, info);
                    //5.通知数据适配器刷新
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }

                    //6.隐藏对话框
                    dialog.dismiss();
                } else {
                    ToastUtils.show(getApplicationContext(), "请输入拦截号码");
                }
            }
        });
        but_blacklist_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View v = null;
            ViewHolder viewHolder = null;
            if (view == null) {
                viewHolder = new ViewHolder();
                v = View.inflate(getApplicationContext(), R.layout.blacklist_item, null);
                viewHolder.tv_blacklist_number = (TextView) v.findViewById(R.id.tv_blacklist_number);
                viewHolder.tv_blacklist_type = (TextView) v.findViewById(R.id.tv_blacklist_type);
                viewHolder.but_blacklist_delete = (Button) v.findViewById(R.id.but_blacklist_delete);
                v.setTag(viewHolder);
            } else {
                v = view;
                viewHolder = (ViewHolder) v.getTag();
            }
            viewHolder.tv_blacklist_number.setText(list.get(i).phone);
            int mode = Integer.parseInt(list.get(i).mode);
            switch (mode) {
                case 1:
                    viewHolder.tv_blacklist_type.setText("拦截短信");
                    break;
                case 2:
                    viewHolder.tv_blacklist_type.setText("拦截电话");
                    break;
                case 3:
                    viewHolder.tv_blacklist_type.setText("拦截所有");
                    break;
            }

            viewHolder.but_blacklist_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //1.数据库中删除
                    mDao.delete(list.get(i).phone);
                    //2.集合中删除
                    list.remove(i);
                    //3.通知适配器
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            return v;
        }
    }

    private static class ViewHolder {
        private TextView tv_blacklist_number;
        private TextView tv_blacklist_type;
        private Button but_blacklist_delete;
    }
    private void initData() {
        list = new ArrayList<BlackListInfo>();
        //获取数据库中的所有号码
        new Thread() {
            @Override
            public void run() {
                //1.获取操作黑名单数据库的对象
                mDao = new BlackListDao(getApplicationContext());
                //2.查询数据
                list = mDao.findItem(0);
                //3.查询数据总数
                mCount = mDao.getCount();
                //4.通知主线程
                handler.sendEmptyMessage(1);
            }
        }.start();
    }
}

