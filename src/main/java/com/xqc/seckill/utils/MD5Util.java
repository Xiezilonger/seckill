package com.xqc.seckill.utils;


import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

//根据密码设计方案提供相应的方法
public class MD5Util {

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    //准备一个salt
    private static final String SALT = "abcdefg";

    //加密加盐, 完成的是 md5（pass+salt1）
    public static String inputPassToMidPass(String inputPass) {
    //可以自己设计
        String str = "" + SALT.charAt(0) + inputPass + SALT.charAt(6);
        return md5(str);
    }

    //这个盐随机生成, 完成的是 md5（ md5（pass+salt1）+salt2）
    public static String midPassToDBPass(String midPass, String salt) {
        String str = salt.charAt(1) + midPass + salt.charAt(5);
        return md5(str);

    }

    /**
     * 进行两次加密加盐 最后存到数据库的 md5（ md5（pass+salt1）+salt2）
     * salt1 是前端进行的
     * salt2 是后端进行的随机生成
     */
    public static String inputPassToDBPass(String inputPass, String salt) {
        String midPass = inputPassToMidPass(inputPass);
        String dbPass = midPassToDBPass(midPass, salt);
        return dbPass;
    }
}
