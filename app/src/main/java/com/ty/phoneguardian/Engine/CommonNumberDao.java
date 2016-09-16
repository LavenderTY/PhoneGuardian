package com.ty.phoneguardian.Engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lavender on 2016/8/11.
 */
public class CommonNumberDao {
    public static final String TAG = "CommonNumberDao";
    //1.指定访问数据库的路径
    public static String path = "data/data/com.ty.phoneguardian/files/commonnum.db";

    /**
     * 查询classlist表中的数据
     *
     * @return 返回存储了表中数据的集合
     */
    public List<Group> getNumberGroup() {
        //2.开启数据库连(只读)
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        //3.查询数据库
        Cursor cursor = db.query("classlist", new String[]{"name", "idx"}, null, null, null, null, null);
        List<Group> groupList = new ArrayList<Group>();
        while (cursor.moveToNext()) {
            Group group = new Group();
            group.name = cursor.getString(cursor.getColumnIndex("name"));
            group.idx = cursor.getInt(cursor.getColumnIndex("idx"));
            group.listChild = getNumberChild(group.idx);
            groupList.add(group);
        }
        return groupList;
    }

    /**
     * 查询 table1 - table8 表中的数据
     *
     * @param idx 每个table的后缀名
     * @return 返回存储了表中数据的集合
     */
    public List<Child> getNumberChild(int idx) {
        //2.开启数据库连(只读)
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        //3.查询数据库
        Cursor cursor = db.rawQuery("select * from table" + idx + ";", null);
        List<Child> childList = new ArrayList<Child>();
        while (cursor.moveToNext()) {
            Child child = new Child();
            child.id = cursor.getInt(cursor.getColumnIndex("_id"));
            child.number = cursor.getString(cursor.getColumnIndex("number"));
            child.name = cursor.getString(cursor.getColumnIndex("name"));
            childList.add(child);
        }
        return childList;
    }

    public class Group {
        public String name;
        public int idx;
        public List<Child> listChild;
    }

    public class Child {
        public int id;
        public String number;
        public String name;
    }
}
