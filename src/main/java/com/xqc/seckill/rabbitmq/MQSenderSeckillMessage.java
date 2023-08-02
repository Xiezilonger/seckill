package com.xqc.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class MQSenderSeckillMessage {

    //装配
    @Resource
    private RabbitTemplate rabbitTemplate;

    //发送秒杀消息
    public void sendSeckillMessage(String message) {
        log.info("发送消息--->" + message);
        rabbitTemplate.convertAndSend("seckillExchange",
                "seckill.message", message);

    }
}
