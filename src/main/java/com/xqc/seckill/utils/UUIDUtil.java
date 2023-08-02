package com.xqc.seckill.utils;

import org.junit.jupiter.api.Test;

import java.util.UUID;

//生成UUID的工具类
public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

//    @Test
//    void t1(){
//        System.out.println(UUIDUtil.uuid());
//    }
}
