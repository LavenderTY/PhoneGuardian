package com.ty.phoneguardian.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ty.phoneguardian.Engine.CommonNumberDao;
import com.ty.phoneguardian.R;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CommonNumberQueryActivity extends AppCompatActivity {
    private String TAG = "CommonNumberQueryActivity";
    @InjectView(R.id.elv_common_number)
    ExpandableListView elvCommonNumber;
    private CommonNumberDao mDao;
    private List<CommonNumberDao.Group> mList;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_number_query);
        ButterKnife.inject(this);
        initData();
    }

    /**
     * 给可扩展的ListView设置数据，并且填充
     */
    private void initData() {
        mDao = new CommonNumberDao();
        mList = mDao.getNumberGroup();
        Log.i(TAG, String.valueOf(mList));
        mAdapter = new MyAdapter();
        elvCommonNumber.setAdapter(mAdapter);

        //改可扩展的LiseView注册点击事件
        elvCommonNumber.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                startCall(mAdapter.getChild(i, i1).number);
                //返回true 表示事件由setOnChildClickListener管理，但是这里是有孩子节点管理
                return false;
            }
        });
    }

    private void startCall(String number) {
        //开启打电话的界面
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    class MyAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            //获取总组数
            return mList.size();
        }

        @Override
        public int getChildrenCount(int i) {
            //获取孩子节点的总数
            //i  组的id
            //获取某一组中国孩子节点的总数
            return mList.get(i).listChild.size();
        }

        @Override
        public CommonNumberDao.Group getGroup(int i) {
            //获取组所对应的对象
            return mList.get(i);
        }

        @Override
        public CommonNumberDao.Child getChild(int i, int i1) {
            //获取孩子节点
            //i  组的id     il 孩子节点的id
            return mList.get(i).listChild.get(i1);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            //获取孩子节点的id
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            //是否将group的id作为数据适配器的id
            return false;
        }

        //dip = dp
        //dpi = ppi  像素密度(每一个英寸上分布的像素点的个数)
        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            //获取组的view
            TextView textView = new TextView(getApplicationContext());
            textView.setText("          " + getGroup(i).name);
            textView.setTextColor(Color.RED);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
            return textView;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            View v = View.inflate(getApplicationContext(), R.layout.elv_chilld_item, null);
            TextView tv_elv_name = (TextView) v.findViewById(R.id.tv_elv_name);
            TextView tv_elv_number = (TextView) v.findViewById(R.id.tv_elv_number);
            tv_elv_name.setText(getChild(i, i1).name);
            tv_elv_number.setText(getChild(i, i1).number);
            return v;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            //孩子节点能否被选中
            //false --> 不能被选中
            //能不能被选中，是孩子节点是否响应事件
            return true;
        }
    }
}
