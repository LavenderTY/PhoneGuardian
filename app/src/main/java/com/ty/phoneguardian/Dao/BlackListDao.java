package com.ty.phoneguardian.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ty.phoneguardian.bean.BlackListInfo;
import com.ty.phoneguardian.Db.BlackListOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lavender on 2016/8/13.
 */
public class BlackListDao {
    private BlackListOpenHelper openHelper = null;
    private List<BlackListInfo> list = new ArrayList<BlackListInfo>();

    //BlackListDao 单例模式
    //1.私有化构造方法
    public BlackListDao(Context ctx) {
        //创建数据库以及其表结构
        openHelper = new BlackListOpenHelper(ctx);
    }

    //2.提供一个当前类的对象
    private static BlackListDao blackListDao = null;

    //3.提供一个方法，如果当前类的对象为空，创建一个新的
    public static BlackListDao getInstence(Context context) {
        if (blackListDao == null) {
            blackListDao = new BlackListDao(context);
        }
        return blackListDao;
    }

    /**
     * 增加一个条目
     *
     * @param phone 拦截的电话号码
     * @param mode  拦截类型（1:短信   2:电话   3:拦截所有(短信+电话)）
     */
    public void insert(String phone, String mode) {
        //1.开启数据库
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone", phone);
        values.put("mode", mode);
        db.insert("blackList", null, values);
        db.close();
    }

    /**
     * 从数据库中删除一个电话号码
     *
     * @param phone 删除的电话号码
     */
    public void delete(String phone) {
        //1.开启数据库
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.delete("blackList", "phone = ?", new String[]{phone});
        db.close();
    }

    /**
     * 根据电话号码更新拦截模式
     *
     * @param phone 更新拦截模式的电话号码
     * @param mode  要更新为的类型（1:短信   2:电话   3:拦截所有(短信+电话)）
     */
    public void update(String phone, String mode) {
        //1.开启数据库
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", mode);
        db.update("blackList", values, "phone = ?", new String[]{phone});
        db.close();
    }

    /**
     * 查询数据库中的所有结果
     *
     * @return 返回的就是查询到的集合
     */
    public List<BlackListInfo> findAll() {
        //1.开启数据库
        SQLiteDatabase db = openHelper.getWritableDatabase();
        Cursor cursor = db.query("blackList", new String[]{"phone", "mode"}, null, null, null, null, "_id desc");
        while (cursor.moveToNext()) {
            BlackListInfo info = new BlackListInfo();
            info.phone = cursor.getString(cursor.getColumnIndex("phone"));
            info.mode = cursor.getString(cursor.getColumnIndex("mode"));
            list.add(info);
        }
        db.close();
        return list;
    }

    /**
     * 每次逆序查询数据中的指定数据条数
     *
     * @param index 开始查询的索引值
     * @return 返回的就是查询到的集合
     */
    public List<BlackListInfo> findItem(int index) {
        //1.开启数据库
        SQLiteDatabase db = openHelper.getWritableDatabase();
        //?  查询的索引值     20每次查询的条数
        String sql = "select phone,mode from blackList order by _id desc limit ?,20";
        Cursor cursor = db.rawQuery(sql, new String[]{index + ""});
        while (cursor.moveToNext()) {
            BlackListInfo info = new BlackListInfo();
            info.phone = cursor.getString(cursor.getColumnIndex("phone"));
            info.mode = cursor.getString(cursor.getColumnIndex("mode"));
            list.add(info);
        }
        db.close();
        return list;
    }

    /**
     * 查询数据总条数
     *
     * @return 返回0 表示没有数据
     */
    public int getCount() {
        int count = 0;
        //1.开启数据库
        SQLiteDatabase db = openHelper.getWritableDatabase();
        String sql = "select count(*) from blackList";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        db.close();
        return count;
    }

    /**
     * 查询数据的类型
     *
     * @param phone 拦截的电话号码
     * @return 电话的拦截模式   0:没有  1:短信  2:电话  3:所有
     */
    public int getMode(String phone) {
        int mode = 0;
        //1.开启数据库
        SQLiteDatabase db = openHelper.getWritableDatabase();
        Cursor cursor = db.query("blackList", new String[]{"mode"}, "phone = ?", new String[]{phone}, null, null, null);
        if (cursor.moveToNext()) {
            mode = cursor.getInt(cursor.getColumnIndex("mode"));
        }
        db.close();
        return mode;
    }
}
