package com.xqc.seckill.vo;

//接受用户登录时发送的信息(mobile,password)

import lombok.Data;

@Data
public class LoginVo {
    private String mobile;
    private String password;
}
