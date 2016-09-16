package com.ty.phoneguardian.Engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Lavender on 2016/8/15.
 */
public class SmsBackUp {
    private static int index = 0;

    //3.传递一个实现了此接口的类的对象（至备份短信的工具类中）,已经实现了接口中未实现的方法
    //备份短信的方法   callBack 是实现类的对象，不是接口
    public static void backUp(Context context, String path, CallBack callBack) {
        //1.获取备份短信写入的文件
        File file = new File(path);
        //2.获取内容解析器，获取短信数据的数据
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/"),
                new String[]{"address", "date", "type", "body"}, null, null, null);
        FileOutputStream fos = null;
        try {
            //3文件相应的输出流
            fos = new FileOutputStream(file);
            //4.序列化数据库中读取的数据，放置在xml中
            XmlSerializer xs = Xml.newSerializer();
            //5.xml相应的设置
            xs.setOutput(fos, "utf-8");
            xs.startDocument("utf-8", true);
            xs.startTag(null, "sms");
            //6.获取所有短信
            //A.传递进来的是对话框的话，指定对话框的总数
            //A.传递进来的是进度条的话，指定进度条的总数
            //4.获取参数，在合适的地方调用方法
            if (callBack != null) {
                callBack.setMax(cursor.getCount());
            }
            while (cursor.moveToNext()) {
                xs.startTag(null, "message");

                xs.startTag(null, "address");
                xs.text(cursor.getString(cursor.getColumnIndex("address")));
                xs.endTag(null, "address");

                xs.startTag(null, "date");
                xs.text(cursor.getString(cursor.getColumnIndex("date")));
                xs.endTag(null, "date");

                xs.startTag(null, "type");
                xs.text(cursor.getString(cursor.getColumnIndex("type")));
                xs.endTag(null, "type");

                xs.startTag(null, "body");
                xs.text(cursor.getString(cursor.getColumnIndex("body")));
                xs.endTag(null, "body");
                xs.endTag(null, "message");
                //7.每次循环都要叠加短信条数
                //进度条的更新可以在子线程中进行
                //A.传递进来的是对话框的话，指定对话框的当前百分比
                //A.传递进来的是进度条的话，指定进度条的当前百分比
                index++;
                if (callBack != null) {
                    callBack.setProgress(index);
                }
                Thread.sleep(500);
            }
            xs.endTag(null, "sms");
            xs.endDocument();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }
    }

    //回调
    //1.定义一个接口
    public interface CallBack {
        //2.定义接口中未实现的业务逻辑(短信总数，当前进度)
        //设置短信总数的方法（接口中的为实现的方法，根据具体情况调用）
        void setMax(int max);

        //百分比更新
        void setProgress(int index);
    }
}
