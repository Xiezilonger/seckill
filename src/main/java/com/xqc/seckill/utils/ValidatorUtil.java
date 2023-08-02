package com.xqc.seckill.utils;


//完成一些校验工作,例如手机号码是否合格

import org.springframework.util.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {

    //校验手机号码的正则表达式
    private static final Pattern mobile_pattern = Pattern.compile("[1][3-9][0-9]{9}$");

    //编写方法,如果满足规则，返回,否则返回F
    public static boolean isMobile(String mobile) {
        if (!StringUtils.hasText(mobile)) {
            return false;
        }

        //进行正则表达式校验
        Matcher matcher = mobile_pattern.matcher(mobile);
        return matcher.matches();
    }
}
