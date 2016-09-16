package com.ty.phoneguardian.Db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lavender on 2016/8/13.
 */
public class BlackListOpenHelper extends SQLiteOpenHelper {
    public BlackListOpenHelper(Context context) {
        super(context, "blacklist.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建数据库的表
        sqLiteDatabase.execSQL("create table blackList(_id integer primary key autoincrement,phone varchar(20),mode varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
