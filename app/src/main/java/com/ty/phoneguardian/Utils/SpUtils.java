package com.ty.phoneguardian.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Lavender on 2016/8/9.
 */
public class SpUtils {
    private static SharedPreferences sp;

    /**
     * 写入boolean类型的变量至SharedPreferences
     *
     * @param ctx   上下文环境
     * @param key   存储节点的名称
     * @param value 存储节点的值 boolean
     */
    public static void putBoolean(Context ctx, String key, boolean value) {
        //（存储节点文件的名称，读写方式）
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).commit();
    }

    /**
     * 从SharedPreferences获取boolean类型的变量
     *
     * @param ctx     上下文环境
     * @param key     存储节点的名称
     * @param devalue 没有此节点时的默认值
     * @return 返回默认值或者有此节点时的值
     */
    public static boolean getBoolean(Context ctx, String key, boolean devalue) {
        //（存储节点文件的名称，读写方式）
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, devalue);
    }

    /**
     * 写入String类型的变量至SharedPreferences
     *
     * @param ctx   上下文环境
     * @param key   存储节点的名称
     * @param value 存储节点的值 String
     */
    public static void putString(Context ctx, String key, String value) {
        //（存储节点文件的名称，读写方式）
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, value).commit();
    }

    /**
     * 从SharedPreferences获取String类型的变量
     *
     * @param ctx     上下文环境
     * @param key     存储节点的名称
     * @param devalue 没有此节点时的默认值
     * @return 返回默认值或者有此节点时的值
     */
    public static String getString(Context ctx, String key, String devalue) {
        //（存储节点文件的名称，读写方式）
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getString(key, devalue);
    }

    /**
     * 写入Int类型的变量至SharedPreferences
     *
     * @param ctx   上下文环境
     * @param key   存储节点的名称
     * @param value 存储节点的值 Int
     */
    public static void putInt(Context ctx, String key, int value) {
        //（存储节点文件的名称，读写方式）
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key, value).commit();
    }

    /**
     * 从SharedPreferences获取Int类型的变量
     *
     * @param ctx     上下文环境
     * @param key     存储节点的名称
     * @param devalue 没有此节点时的默认值
     * @return 返回默认值或者有此节点时的值
     */
    public static int getInt(Context ctx, String key, int devalue) {
        //（存储节点文件的名称，读写方式）
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getInt(key, devalue);
    }

    /**
     * 从SharedPreferences中移除节点
     *
     * @param ctx 上下文环境
     * @param key 存储节点的名称
     */
    public static void remove(Context ctx, String key) {
        //（存储节点文件的名称，读写方式）
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().remove(key);
    }
}
