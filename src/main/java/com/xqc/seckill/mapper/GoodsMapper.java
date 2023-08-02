package com.xqc.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xqc.seckill.pojo.Goods;
import com.xqc.seckill.pojo.User;
import com.xqc.seckill.vo.GoodsVo;

import java.util.List;

public interface GoodsMapper extends BaseMapper<Goods> {

    //获取商品列表-秒杀
    List<GoodsVo> findGoodsVo();

    //根据id获取指定商品信息
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
