package com.xqc.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xqc.seckill.pojo.User;
import com.xqc.seckill.vo.LoginVo;
import com.xqc.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService extends IService<User> {
    //完成用户的登录校验
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    //根据Cookie-ticket获取用户
    User getUserByCookie(String userTicket,HttpServletRequest request,HttpServletResponse response);
}
