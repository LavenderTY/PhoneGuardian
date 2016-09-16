package com.ty.phoneguardian.Engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lavender on 2016/8/11.
 */
public class VirusDao {
    public static final String TAG = "VirusDao";
    //1.指定访问数据库的路径
    public static String path = "data/data/com.ty.phoneguardian/files/antivirus.db";

    //2.开启数据库，查询数据库中对应的md5码
    public static List<String> getVirusList() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("datable", new String[]{"md5"}, null, null, null, null, null);
        List<String> virusList = new ArrayList<String>();
        while (cursor.moveToNext()) {
            virusList.add(cursor.getString(cursor.getColumnIndex("md5")));
        }
        cursor.close();
        db.close();
        return virusList;
    }
}
