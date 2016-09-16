package com.ty.phoneguardian.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Lavender on 2016/8/8.
 */
public class StreamUtil {
    /**
     * 流转换成字符串
     *
     * @param is 流对象
     * @return 流转换成字符串，返回null代表异常
     */
    public static String streamToString(InputStream is) {
        //1.在读取的过程中，将读取的内容存储在缓存中，然后一次性的转换成字符串返回
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //2.读流操作，读到没有为止（循环）
        byte[] b = new byte[1024];
        //3.记录读取内容的临时变量
        int len = 0;
        try {
            while ((len = is.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            //4.返回读取的数据
            return bos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
