package com.xqc.seckill.service.Impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xqc.seckill.mapper.GoodsMapper;
import com.xqc.seckill.mapper.UserMapper;
import com.xqc.seckill.pojo.Goods;
import com.xqc.seckill.pojo.User;
import com.xqc.seckill.service.GoodsService;
import com.xqc.seckill.service.UserService;
import com.xqc.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class GoodsServiceImpl
        extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {
    @Resource
    private GoodsMapper goodsMapper;

    @Override
    public List<GoodsVo> findGoodsVo() {
        return goodsMapper.findGoodsVo();
    }

    //根据商品id返回秒杀商品的详情
    @Override
    public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
        return goodsMapper.findGoodsVoByGoodsId(goodsId);
    }

    //获取商品列表

}