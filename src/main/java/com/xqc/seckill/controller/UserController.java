package com.xqc.seckill.controller;


import com.xqc.seckill.pojo.User;
import com.xqc.seckill.vo.RespBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {
    /**
     * 返回用户信息, 同时我们也演示如何携带参数
     * @param user * @return
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user) {
        return RespBean.success(user);
    }
}