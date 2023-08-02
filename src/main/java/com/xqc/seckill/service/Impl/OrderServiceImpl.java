package com.xqc.seckill.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xqc.seckill.mapper.OrderMapper;
import com.xqc.seckill.pojo.Order;
import com.xqc.seckill.pojo.SeckillGoods;
import com.xqc.seckill.pojo.SeckillOrder;
import com.xqc.seckill.pojo.User;
import com.xqc.seckill.service.OrderService;
import com.xqc.seckill.service.SeckillGoodsService;
import com.xqc.seckill.service.SeckillOrderService;
import com.xqc.seckill.vo.GoodsVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements
        OrderService {
    @Resource
    private SeckillGoodsService seckillGoodsService;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private SeckillOrderService seckillOrderService;
    @Resource
    private RedisTemplate redisTemplate;

    //秒杀商品，减少库存
    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goodsVo) {
        //查询后端的库存量进行减一
        SeckillGoods seckillGoods =
                seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>()
                        .eq("goods_id", goodsVo.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        //在默认的事务隔离级别 REPEATABLE READ 中，
        //UPDATE 语句会在事务中锁定要更新的行，
        //这可以防止其他会话在同一行上执行 UPDATE 或 DELETE 操作

        //只有在更新成功时update才会返回True
        boolean update = seckillGoodsService.update
                (new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count-1")
                        .eq("goods_id", goodsVo.getId()).gt("stock_count", 0));
        if (!update) {//如果更新失败,说明已经没有库存了
            return null;
        }

        //生成普通订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goodsVo.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);

        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setUserId(user.getId());

        seckillOrderService.save(seckillOrder);

        //将生成的秒杀订单存入到redis,这样在查询每个用户是否秒杀该商品时
        //直接到redis查询,起到优化效果
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goodsVo.getId(), seckillOrder);
        return order;
    }
}