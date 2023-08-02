package com.xqc.seckill.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xqc.seckill.pojo.Order;
import com.xqc.seckill.pojo.SeckillMessage;
import com.xqc.seckill.pojo.SeckillOrder;
import com.xqc.seckill.pojo.User;

import com.xqc.seckill.service.GoodsService;
import com.xqc.seckill.service.OrderService;
import com.xqc.seckill.service.SeckillOrderService;
import com.xqc.seckill.vo.GoodsVo;
import com.xqc.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {
    @Resource
    private GoodsService goodsService;
    @Resource
    private SeckillOrderService seckillOrderService;
    @Resource
    private OrderService orderService;
    @Resource
    private RedisTemplate redisTemplate;
//    @Resource
//    private MQSenderSeckillMessage mqSenderSeckillMessage;

    //定义map,记录秒杀商品是否还有库存
    private HashMap<Long, Boolean> entryStockMap = new HashMap<>();

//    @RequestMapping(value = "/doSeckill")
//    public String doSeckill(Model model, User user, Long goodsId) {
//        System.out.println("-----秒杀 V1.0--------");
//        //===================秒杀 v1.0 start =========================
//        if (user == null) {
//            return "login";
//        }
//        model.addAttribute("user", user);
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//        //判断库存
//        if (goodsVo.getStockCount() < 1) {
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return "secKillFail";
//        }
//        //解决重复抢购(-----原始的去数据库查询版-----)
////        SeckillOrder seckillOrder =
////                seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId())
////                        .eq("goods_id", goodsId));
////        if (seckillOrder != null) {
////            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
////            return "secKillFail";
////        }
//
//        //V2.0版本：解决重复抢购,直接到redis中获取对应的秒杀订单，如果有，就说明已经抢购了
//        SeckillOrder o = (SeckillOrder) redisTemplate.opsForValue()
//                .get("order:" + user.getId() + ":" + goodsVo.getId());
//
//        //如果订单不为空说明已经抢购过了,直接返回错误页面
//        if (o != null) {
//            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
//            return "secKillFail";
//        }
//
//
//        //抢购
//        Order order = orderService.seckill(user, goodsVo);
//        if (order == null) {
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return "secKillFail";
//        }
//        model.addAttribute("order", order);
//        model.addAttribute("goods", goodsVo);
//        return "orderDetail";
//        //===================秒杀 v1.0 end... =========================
//    }

    //说明:处理用户的秒杀请求
    //V3.0,Redis库存预减
    //V4.0,使用内存标记，避免多次操作 redis, false 表示空库存了
    //V5.0,加入消息队列,实现秒杀的异步请求
    @RequestMapping(value = "/doSeckill")
    public String doSeckill(Model model, User user, Long goodsId) {
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if (goodsVo.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }
        //解决重复抢购(-----原始的去数据库查询版-----)
//        SeckillOrder seckillOrder =
//                seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId())
//                        .eq("goods_id", goodsId));
//        if (seckillOrder != null) {
//            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
//            return "secKillFail";
//        }

        //解决重复抢购,直接到redis中获取对应的秒杀订单，如果有，就说明已经抢购了
        SeckillOrder o = (SeckillOrder) redisTemplate.opsForValue()
                .get("order:" + user.getId() + ":" + goodsVo.getId());

        //如果订单不为空说明已经抢购过了,直接返回错误页面
        if (o != null) {
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }

        if (!entryStockMap.get(goodsId)) {
            //如果当前这个秒杀商品已经是空库存，则直接返回.
            model.addAttribute("errmsg",
                    RespBeanEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";

        }

        //库存预减,如果在Redis中预检库存发现秒杀商品库存为0就直接返回
        //从而减少去执行orderService.seckill()方法,防止线程堆积,优化高并发
        Long decrement = redisTemplate.opsForValue()
                .decrement("seckillGoods:" + goodsId);
        if (decrement < 0) {
            //这里使用内存标记，避免多次操作 redis, false 表示空库存了.
            entryStockMap.put(goodsId, false);
            //恢复成 0
            redisTemplate.opsForValue().increment("seckillGoods:" + goodsId);
            model.addAttribute("errmsg",
                    RespBeanEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }
        //抢购
        Order order = orderService.seckill(user, goodsVo);
        if (order == null) {
            model.addAttribute("errmsg",
                    RespBeanEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }
        model.addAttribute("order", order);
        model.addAttribute("goods", goodsVo);
        return "orderDetail";

//        //抢购,像消息队列发送秒杀异步请求
//        //这里我们发送秒杀消息后, 立即返回结果[临时结果]-"比如排队中..."
//        //errmsg 为排队中.... , 暂时返回 "secKillFaill" 页面
//        SeckillMessage seckillMessage =
//                new SeckillMessage(user, goodsId);
//        mqSenderSeckillMessage.sendSeckillMessage
//                (JSONUtil.toJsonStr(seckillMessage));
//        model.addAttribute("errmsg", "排队中.....");
//        return "secKillFail";

    }

    //该方法是在类的所有属性都初始化完后，自动执行的
    //这里我们就可以把所有秒杀商品的库存量加载到redis来提升性能
    @Override
    public void afterPropertiesSet() throws Exception {

        //查询所有的秒杀商品
        List<GoodsVo> list = goodsService.findGoodsVo();
        //先判断是否为空
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        //遍历list,然后将秒杀商品的库存量放入到redis中
        //秒杀商品库存量对应key : seckillGoods:商品id
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());

            //当有库存为 false，当无库存为 true。防止库存没有了，
            //还会到 Redis 进行判断操作
            entryStockMap.put(goodsVo.getId(), true);

        });

    }
}