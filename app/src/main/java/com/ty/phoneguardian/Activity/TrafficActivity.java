package com.ty.phoneguardian.Activity;

import android.net.TrafficStats;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ty.phoneguardian.R;

public class TrafficActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);

        //获取手机下载流量
        //获取流量(R 手机(2G,3G,4G)下载流量)  单位 byte
        long mobileRxBytes = TrafficStats.getMobileRxBytes();
        //获取手机总流量(上传+下载)
        //T total(手机总流量(上传+下载))
        long mobileTxBytes = TrafficStats.getMobileTxBytes();

        //total(下载流量总和(手机+wifi))
        long totalRxBytes = TrafficStats.getTotalRxBytes();

        //总流量((手机+wifi)+(上传+下载))
        long totalTxBytes = TrafficStats.getTotalTxBytes();

        //流量获取模块（发送短信），运营商（联通，移动...），（流量监听）接口
    }
}
