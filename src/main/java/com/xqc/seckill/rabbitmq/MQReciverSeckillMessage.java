package com.xqc.seckill.rabbitmq;


import cn.hutool.json.JSONUtil;
import com.xqc.seckill.pojo.SeckillMessage;
import com.xqc.seckill.pojo.User;
import com.xqc.seckill.service.GoodsService;
import com.xqc.seckill.service.OrderService;
import com.xqc.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

//消息的消费者,这里调用最重要的seckill方法
@Service
@Slf4j
public class MQReciverSeckillMessage {

    //装配需要的组件
    @Resource
    private GoodsService goodsService;
    @Resource
    private OrderService orderService;
    @Resource
    private RedisTemplate redisTemplate;

    //下单操作
    @RabbitListener(queues = "seckillQueue")
    public void queue(String message) {
        log.info("接收到的消息：" + message);
        SeckillMessage seckillMessage =
                JSONUtil.toBean(message, SeckillMessage.class);
        Long goodId = seckillMessage.getGoodsId();
        User user = seckillMessage.getUser();
        //获取抢购的商品信息
        GoodsVo goodsVo =
                goodsService.findGoodsVoByGoodsId(goodId);
        //下单操作
        orderService.seckill(user, goodsVo);
    }
}
