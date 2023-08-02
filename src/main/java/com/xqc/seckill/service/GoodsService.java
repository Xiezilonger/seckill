package com.xqc.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xqc.seckill.pojo.Goods;
import com.xqc.seckill.vo.GoodsVo;

import java.util.List;

public interface GoodsService extends IService<Goods> {
    //秒杀商品列表
    List<GoodsVo> findGoodsVo();
    //获取商品详情
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
