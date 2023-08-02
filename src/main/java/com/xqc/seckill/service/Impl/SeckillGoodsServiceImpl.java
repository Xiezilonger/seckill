package com.xqc.seckill.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xqc.seckill.mapper.SeckillGoodsMapper;
import com.xqc.seckill.pojo.SeckillGoods;
import com.xqc.seckill.service.SeckillGoodsService;
import org.springframework.stereotype.Service;

@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements SeckillGoodsService {
}
