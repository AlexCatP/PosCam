package com.psy.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ppssyyy on 2016-07-31.
 */
public class EncodeAndDecode {

    public static String URLencode(String data) {
        try {
            data = URLEncoder.encode(data, "utf-8");
            data = data.replaceAll("\u0025", "\u005f");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String URLdecode(String data) {
        try {
            data = data.replaceAll("\u005f", "\u0025");
            data = URLDecoder.decode(data.replaceAll("%(?![0-9a-fA-F]{2})", "%25") ,"UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /*
	* 16位MD5加密
	*/
    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.reset();

            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        //16位加密，从第9位到25位
        return md5StrBuff.substring(8, 24).toString().toUpperCase();
    }


}
