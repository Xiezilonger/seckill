package com.xqc.seckill.controller;

import com.xqc.seckill.service.UserService;
import com.xqc.seckill.vo.LoginVo;
import com.xqc.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {

    @Resource
    private UserService userService;

    //编写方法，可以进入到登陆页面
    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    //编写方法，处理用户登录请求
    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){

        log.info("{}",loginVo);
        return userService.doLogin(loginVo,httpServletRequest,httpServletResponse);
    }
}
