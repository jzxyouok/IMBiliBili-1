package com.lh.imbilibili.utils;

import com.lh.imbilibili.data.Constant;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by liuhui on 2016/7/8.
 */
public class BiliBilliSignUtils {

    public static String getSign(String params) {
        try {
            params += Constant.SECRETKEY;
            MessageDigest md = MessageDigest.getInstance("md5");
            StringBuffer sb = new StringBuffer();
            byte[] temp = md.digest(params.getBytes());
            for (int i = 0; i < temp.length; i++) {
                String s = Integer.toHexString(temp[i] & 0xff);
                if (s.length() < 2)
                    sb.append(0);
                sb.append(s);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
