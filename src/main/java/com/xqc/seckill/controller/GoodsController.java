package com.xqc.seckill.controller;

import com.xqc.seckill.pojo.User;
import com.xqc.seckill.service.GoodsService;
import com.xqc.seckill.service.UserService;
import com.xqc.seckill.vo.GoodsVo;
import org.hibernate.validator.constraints.EAN;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private UserService userService;

    @Resource
    private GoodsService goodsService;

    //装配
    @Resource
    private RedisTemplate redisTemplate;

    //手动进行渲染需要的模板解析器
    @Resource
    private ThymeleafViewResolver thymeleafViewResolver;

    //进入到商品列表页
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model, User user,
                         HttpServletRequest request, HttpServletResponse response) {

        //如果用户没有登录
        if (user == null) {
            return "login";
        }

        //先到redis获取页面-如果有就直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if (StringUtils.hasText(html)) {
            return html;
        }
        //将user放入到model,携带给下一个模板使用
        model.addAttribute("user", user);
        //将商品列表信息,放入到model,携带给下一个模板使用
        model.addAttribute("goodsList", goodsService.findGoodsVo());

        //如果为从 redis 中取出的页面为 null，手动渲染，存入 redis 中
        WebContext webContext =
                new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        if (StringUtils.hasText(html)) {
            //每 60s 更新一次 redis 页面缓存, 即 60s 后, 该页面缓存失效, Redis 会清除该页面缓存
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
        }
        return html;
    }

//    //进入到商品详情页(-----直接查DB没有用redis-----)
//    @RequestMapping("/toDetail/{goodsId}")
//    public String toDetail(Model model, User user, @PathVariable Long goodsId) {
//
//        model.addAttribute("user", user);
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//        //============处理秒杀倒计时和状态 start ==============
//        Date startDate = goodsVo.getStartDate();
//        Date endDate = goodsVo.getEndDate();
//        Date nowDate = new Date();
//        //秒杀状态(0,1,2对应未开始、进行中、已结束)
//        int secKillStatus = 0;
//        //秒杀倒计时
//        int remainSeconds = 0;
//        if (nowDate.before(startDate)) {
//            //秒杀还没有开始
//            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
//        } else if (nowDate.after(endDate)) {
//            //秒杀结束
//            secKillStatus = 2;
//            remainSeconds = -1;
//        } else {
//            //秒杀进行中
//            secKillStatus = 1;
//            remainSeconds = 0;
//        }
//        model.addAttribute("secKillStatus", secKillStatus);
//        model.addAttribute("remainSeconds", remainSeconds);
//        //============处理秒杀倒计时和状态 end ==============
//        model.addAttribute("goods", goodsVo);
//
//        //当返回秒杀商品详情时，同时返回秒杀倒计时剩余时间
//        //为了配合前端展示秒杀商品的状态
//
//        return "goodsDetail";
//    }

    //跳转商品详情页面
    @RequestMapping(value = "/toDetail/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail(Model model, User user, @PathVariable Long goodsId, HttpServletRequest request,
                           HttpServletResponse response) {
//使用页面缓存
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
        if (StringUtils.hasText(html)) {
            return html;
        }
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//============处理秒杀倒计时和状态 start ==============
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
//秒杀状态
        int secKillStatus = 0;
//秒杀倒计时
        int remainSeconds = 0;
        if (nowDate.before(startDate)) {
//秒杀还没有开始
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
        } else if (nowDate.after(endDate)) {
//秒杀结束
            secKillStatus = 2;
            remainSeconds = -1;
        } else {
//秒杀进行中
            secKillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
//============处理秒杀倒计时和状态 end ==============
        model.addAttribute("goods", goodsVo);
//return "goodsDetail";
//如果为 null，手动渲染，存入 redis 中
        WebContext webContext = new WebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);
        if (StringUtils.hasText(html)) {
//设置每 60s 更新一次缓存, 即 60s 后, 该页面缓存失效, Redis 会清除该页面缓存
            valueOperations.set("goodsDetail:" + goodsId, html, 60, TimeUnit.SECONDS);
        }
        return html;
    }
}
