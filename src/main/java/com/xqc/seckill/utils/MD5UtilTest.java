package com.xqc.seckill.utils;

import org.junit.jupiter.api.Test;


public class MD5UtilTest {

    @Test
    void MD5UtilTest(){
//        System.out.println(MD5Util.inputPassToMidPass("12345"));
        System.out.println(MD5Util.inputPassToDBPass("13a4ea7a48838c78a1b537aafc121308","xqcxqcxqc"));
    }
}
