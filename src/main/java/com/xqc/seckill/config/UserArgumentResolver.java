package com.xqc.seckill.config;

import com.xqc.seckill.pojo.User;
import com.xqc.seckill.service.UserService;
import com.xqc.seckill.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    private UserService userService;

    //如果这个方法返回 true 才会执行下面的 resolveArgument 方法
//返回 false 不执行下面的方法
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
//获取参数是不是 user 类型
        Class<?> aClass = parameter.getParameterType();
//如果为 t, 就执行 resolveArgument
        return aClass == User.class;
    }

    /**
     * 这个方法，类似拦截器，将传入的参数，取出 cookie 值，然后获取对应的 User 对象
     * 并把这个 User 对象作为参数继续传递. * @param parameter
     *
     * @param mavContainer
     * @param webRequest
     * @param binderFactory
     * @return
     * @throws Exception 韩顺平 Java 工程师
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer
            mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
            throws Exception {
        HttpServletRequest request =
                webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response =
                webRequest.getNativeResponse(HttpServletResponse.class);
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        if (!StringUtils.hasText(ticket)) {
            return null;
        }
//根据 cookie-ticket 到 Redis 获取 User
        return userService.getUserByCookie(ticket, request, response);
    }
}