package com.ty.phoneguardian.Activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ty.phoneguardian.bean.ConstentValue;
import com.ty.phoneguardian.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactActivity extends AppCompatActivity {
    private final String TAG = "ContactActivity";
    private ListView lv_contact;
    private List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    private MyAdapter mAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //8.填充数据适配器
            mAdapter = new MyAdapter();
            lv_contact.setAdapter(mAdapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contasct);
        initUI();
        initData();
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public HashMap<String, String> getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = null;
            ViewHolder mHolder = null;
            if (view == null) {
                mHolder = new ViewHolder();
                v = View.inflate(ContactActivity.this, R.layout.item_contact, null);
                mHolder.tv_contact_name = (TextView) v.findViewById(R.id.tv_contact_name);
                mHolder.tv_contact_phone = (TextView) v.findViewById(R.id.tv_contact_phone);
                v.setTag(mHolder);
            } else {
                v = view;
                mHolder = (ViewHolder) v.getTag();
            }
            mHolder.tv_contact_name.setText(getItem(i).get("name"));
            mHolder.tv_contact_phone.setText(getItem(i).get("phone"));
            return v;
        }
    }

    class ViewHolder {
        TextView tv_contact_name;
        TextView tv_contact_phone;
    }

    /**
     * 获取系统联系人数据方法
     */
    private void initData() {
        //读取联系人可能是耗时操作
        new Thread() {
            @Override
            public void run() {
                //1.获取内容解析器
                ContentResolver resolver = getContentResolver();
                //2.查询系统联系人数据库表
                Cursor cursor = resolver.query(Uri.parse(ConstentValue.RAW_CONTACTS), new String[]{"contact_id"}, null, null, null);
                //集合做Wie成员变量使用的话必须要先清理集合
                list.clear();
                //3.循环游标直到没有数据
                while (cursor.moveToNext()) {
                    String contact_id = cursor.getString(cursor.getColumnIndex("contact_id"));
                    Log.i(TAG, contact_id);
                    //4.根据用户唯一性id去查询数据
                    Cursor cData = resolver.query(Uri.parse(ConstentValue.DATA), new String[]{"data1", "mimetype"}, "raw_contact_id = ?", new String[]{contact_id}, null);
                    HashMap<String, String> map = new HashMap<String, String>();
                    while (cData.moveToNext()) {
                        String data1 = cData.getString(cData.getColumnIndex("data1"));
                        String mimetype = cData.getString(cData.getColumnIndex("mimetype"));
                        Log.i(TAG, "data1 = " + data1);
                        Log.i(TAG, "mimetype = " + mimetype);
                        if ("vnd.android.cursor.item/name".equals(mimetype)) {
                            //不为空的话就添加带map中
                            if (!TextUtils.isEmpty(data1)) {
                                map.put("name", data1);
                            }
                        } else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                            if (!TextUtils.isEmpty(data1)) {
                                map.put("phone", data1);
                            }
                        }
                    }
                    //要把cData遍历完成后，才能把map添加进集合，否则会出现重复数据
                    list.add(map);
                }
                //5.发送消息，告诉主线程可以使用集合了，就可以设置数据适配器
                mHandler.sendEmptyMessage(1);
            }
        }.start();
    }

    private void initUI() {
        lv_contact = (ListView) findViewById(R.id.lv_contact);
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //1.获取点中条目索引指向的集合中的对象
                if (mAdapter != null) {
                    //2.获取当前条目指向集合对应的电话号码
                    String phone = mAdapter.getItem(i).get("phone");
                    //3.
                    //4.结束时，需要返回数据
                    Intent intent = new Intent();
                    intent.putExtra("phone", phone);
                    setResult(0, intent);
                    finish();
                }
            }
        });
    }
}
