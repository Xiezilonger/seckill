package com.xqc.seckill.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xqc.seckill.mapper.UserMapper;
import com.xqc.seckill.pojo.User;
import com.xqc.seckill.service.UserService;
import com.xqc.seckill.utils.CookieUtil;
import com.xqc.seckill.utils.MD5Util;
import com.xqc.seckill.utils.UUIDUtil;
import com.xqc.seckill.utils.ValidatorUtil;
import com.xqc.seckill.vo.LoginVo;
import com.xqc.seckill.vo.RespBean;
import com.xqc.seckill.vo.RespBeanEnum;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserMapper userMapper;

    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        //接收到手机号和密码
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //校验手机号码和密码是否为空
        if (!StringUtils.hasText(mobile) || !StringUtils.hasText(password)) {
            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }
        //校验手机号吗格式是否正确
        if (!ValidatorUtil.isMobile(mobile)) {
            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }
        //校验手机号号码在数据库中是否存在
        User user = userMapper.selectById(mobile);
        if (user == null) {
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        //校验密码
        if (!MD5Util.inputPassToDBPass(password, user.getSlat()).equals(user.getPassword())) {
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        //用户登陆成功

        //给每个用户生成ticket-唯一
        String ticket = UUIDUtil.uuid();
        //将登陆成功的用户保存到session中
//        request.getSession().setAttribute(ticket, user);

        //为了实现分布式session,把登陆的用户存放到redis
        redisTemplate.opsForValue().set("user:" + ticket, user);
        //将ticket保存到cookie
        CookieUtil.setCookie(request, response, "userTicket", ticket);

        return RespBean.success(ticket);

    }


    //根据Cookie获取用户
    @Override
    public User getUserByCookie(String userTicket,
                                HttpServletRequest request, HttpServletResponse response) {

        if(!StringUtils.hasText(userTicket)){
            return null;
        }
        User user =(User) redisTemplate.opsForValue().get("user:" + userTicket);
        //如果用户不为 null,重新设置 cookie
        if(user != null){
            CookieUtil.setCookie(request,response,"userTicket",userTicket);
        }
        return user;
    }
}
