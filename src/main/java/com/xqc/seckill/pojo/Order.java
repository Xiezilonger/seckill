package com.xqc.seckill.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("t_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long goodsId;

    private Long deliveryAddrId;

    private String goodsName;

    private Integer goodsCount;

    private BigDecimal goodsPrice;
    /**
     * 订单渠道 1pc，2Android，3ios
     */
    private Integer orderChannel;
    /**
     * 订单状态：0 新建未支付 1 已支付 2 已发货 3 已收货 4 已退款 5 已完成
     */
    private Integer status;

    private Date createDate;

    private Date payDate;
}
