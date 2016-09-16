package com.ty.phoneguardian.Service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;

import com.ty.phoneguardian.bean.ConstentValue;
import com.ty.phoneguardian.Utils.SpUtils;

/**
 * Created by Lavender on 2016/8/11.
 */
public class LocationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //只用开启器一次服务，所以写在 onCreate() 中
    @Override
    public void onCreate() {
        super.onCreate();
        //获取经纬度坐标（LocationManager）
        //1.获取位置管理者对象
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        //2.这里以最优的方式获取经纬度坐标    Criteria(标准)
        Criteria criteria = new Criteria();
        //允许花费
        criteria.setCostAllowed(true);
        //指定获取经纬度的精确度
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //设置为true的话就可以得到最好的方式
        String bestProvider = lm.getBestProvider(criteria, true);
        //3.通过位置管理者对象获取经纬度坐标(定位方式，minTime获取经纬度坐标的最小间隔时间，minDistance移动最小距离)
        MyLocationListener listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.requestLocationUpdates(bestProvider, 0, 0, listener);
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            //得到经纬
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            String phone = SpUtils.getString(getApplicationContext(), ConstentValue.SIM_NUMBER, "");
            //4.发送短信
            SmsManager manager = SmsManager.getDefault();
            manager.sendTextMessage(phone, null, "longitude = " + longitude + ", latitude = " + latitude, null, null);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            //gps状态发生切换的事件监听
        }

        @Override
        public void onProviderEnabled(String s) {
            //gps开启时的时间监听
        }

        @Override
        public void onProviderDisabled(String s) {
            //gps关闭时的时间监听
        }
    }
}
