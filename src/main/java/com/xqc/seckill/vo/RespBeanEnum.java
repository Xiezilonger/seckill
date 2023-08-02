package com.xqc.seckill.vo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {

    //通用信息
    SUCCESS(200, "success"),
    ERROR(500, "服务端异常"),

    //登录
    LOGIN_ERROR(500210, "用户id或密码错误"),
    MOBILE_ERROR(500211, "手机号码格式不正确"),
    MOBILE_NOT_EXIST(500213, "手机号码不存在"),
    //其他在开发过程中灵活添加
    //秒杀模块
    ENTRY_STOCK(500500, "库存不足"),
    REPEATE_ERROR(500501, "该商品每人限购一件");


    private final Integer code;
    private final String message;

}
