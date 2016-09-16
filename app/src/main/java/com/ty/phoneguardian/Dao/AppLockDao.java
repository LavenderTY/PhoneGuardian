package com.ty.phoneguardian.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.ty.phoneguardian.Db.AppLockOpenHelper;
import com.ty.phoneguardian.Db.BlackListOpenHelper;
import com.ty.phoneguardian.bean.BlackListInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lavender on 2016/8/13.
 */
public class AppLockDao {
    private AppLockOpenHelper openHelper = null;
    private Context context;

    //AppLockDao 单例模式
    //1.私有化构造方法
    private AppLockDao(Context ctx) {
        this.context = ctx;
        //创建数据库以及其表结构
        openHelper = new AppLockOpenHelper(ctx);
    }

    //2.提供一个当前类的对象
    private static AppLockDao appLockDao = null;

    //3.提供一个方法，如果当前类的对象为空，创建一个新的
    public static AppLockDao getInstence(Context context) {
        if (appLockDao == null) {
            appLockDao = new AppLockDao(context);
        }
        return appLockDao;
    }

    /**
     * 将数据添加到数据库中
     *
     * @param packageName 要添加的应用的包名
     */
    public void insert(String packageName) {
        //1.开启数据库
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packageName", packageName);
        db.insert("applock", null, values);
        //数据库发生改变通知
        context.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
        db.close();
    }

    /**
     * 从数据库中删除一个应用
     *
     * @param packageName 要删除的应用的包名
     */
    public void delete(String packageName) {
        //1.开启数据库
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.delete("applock", "packageName = ?", new String[]{packageName});
        context.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
        db.close();
    }

    /**
     * 查询数据库中的所有结果
     *
     * @return 返回的就是查询到的集合
     */
    public List<String> findAll() {
        //1.开启数据库
        SQLiteDatabase db = openHelper.getWritableDatabase();
        Cursor cursor = db.query("applock", new String[]{"packagename"}, null, null, null, null, null);
        List<String> list = new ArrayList<String>();
        while (cursor.moveToNext()) {
            String packageName = cursor.getString(cursor.getColumnIndex("packagename"));
            list.add(packageName);
        }
        cursor.close();
        db.close();
        return list;
    }
}
