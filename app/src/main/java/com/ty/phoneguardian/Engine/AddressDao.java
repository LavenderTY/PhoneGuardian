package com.ty.phoneguardian.Engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Lavender on 2016/8/11.
 */
public class AddressDao {
    public static final String TAG = "AddressDao";
    //1.指定访问数据库的路径
    public static String path = "data/data/com.ty.phoneguardian/files/address.db";
    public static String mAddress = "未知号码";

    /**
     * 传递一个电话号码，开启数据库连接，进行访问，返回一个归属地
     *
     * @param phone 查询的电话号码
     */
    public static String getAddress(String phone) {
        mAddress = "未知号码";
        //2.开启数据库连(只读)
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        //正则表达式匹配电话号码
        String match = "^1[3-8]\\d{9}";
        if (phone.matches(match)) {
            phone = phone.substring(0, 7);
            //3.数据库查询
            Cursor cursor = db.query("data1", new String[]{"outkey"}, "id = ?", new String[]{phone}, null, null, null);
            //4.查询到即可
            if (cursor.moveToNext()) {
                String outkey = cursor.getString(cursor.getColumnIndex("outkey"));
                Log.i(TAG, outkey);
                //5.通过农data1查询的结果，作为外键data2
                Cursor indexCursor = db.query("data2", new String[]{"location"}, "id = ?", new String[]{outkey}, null, null, null);
                if (indexCursor.moveToNext()) {
                    mAddress = indexCursor.getString(indexCursor.getColumnIndex("location"));
                    Log.i(TAG, mAddress);
                }
            } else {
                mAddress = "未知号码";
            }
        } else {
            int length = phone.length();
            switch (length) {
                case 3:
                    mAddress = "报警电话";
                    break;
                case 4:
                    mAddress = "模拟器";
                    break;
                case 5:
                    mAddress = "服务电话";
                    break;
                case 7:
                    mAddress = "本地电话";
                    break;
                case 8:
                    mAddress = "本地电话";
                    break;
                case 11:
                    //(3+8) 区号+座机号码(外地)
                    String area = phone.substring(1, 3);
                    Cursor cursor = db.query("data2", new String[]{"area"}, "area = ?", new String[]{area}, null, null, null);
                    if (cursor.moveToNext()) {
                        mAddress = cursor.getString(cursor.getColumnIndex("area"));
                        System.out.println(mAddress);
                    } else {
                        mAddress = "未知号码";
                    }
                    break;
                case 12:
                    //(4+8) 区号+座机号码(外地)
                    String area1 = phone.substring(1, 4);
                    Cursor cursor1 = db.query("data2", new String[]{"area"}, "area = ?", new String[]{area1}, null, null, null);
                    if (cursor1.moveToNext()) {
                        mAddress = cursor1.getString(cursor1.getColumnIndex("area"));
                    } else {
                        mAddress = "未知号码";
                    }
                    break;
            }
        }
        return mAddress;
    }
}
