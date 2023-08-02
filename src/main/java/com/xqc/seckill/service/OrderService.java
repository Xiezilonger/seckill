package com.xqc.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xqc.seckill.pojo.Order;
import com.xqc.seckill.pojo.User;
import com.xqc.seckill.vo.GoodsVo;

public interface OrderService extends IService<Order> {

    //方法-秒杀
    Order seckill(User user, GoodsVo goodsVo);

}
