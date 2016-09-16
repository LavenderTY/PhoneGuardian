package com.ty.phoneguardian.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Lavender on 2016/8/10.
 */
public class Md5Utils {
    /**
     * 给指定字符串按照MD5算法去加密
     *
     * @param psd 需要加密的密码
     * @return 返回加密后的密码
     */
    public static String encoder(String psd) {
        //加盐处理
        psd = psd + "phoneGuardian";
        try {
            //1.指定加密算法类型
            MessageDigest digest = MessageDigest.getInstance("MD5");
            //2.将需要加密的字符串转换成byte类型的数组，然后进行随机哈希
            byte[] bytes = digest.digest(psd.getBytes());
            //3.循环遍历byte类型,让其生成32位的字符串，固定写法
            StringBuffer buffer = new StringBuffer();
            for (byte b : bytes) {
                int i = b & 0xff;  //固定写法
                //将int类型的i装换成16进制
                String hexStr = Integer.toHexString(i);
                if (hexStr.length() < 2) {
                    hexStr = "0" + hexStr;
                }
                //4.拼接字符串
                buffer.append(hexStr);
            }
            return String.valueOf(buffer);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
