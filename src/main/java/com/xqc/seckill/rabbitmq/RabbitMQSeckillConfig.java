package com.xqc.seckill.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQSeckillConfig {

    //定义队列名
    private static final String QUEUE = "seckillQueue";
    private static final String EXCHANGE = "seckillExchange";

    /**
     * 老师解读
     * 1. 配置队列
     * 2. 队列名为 queue
     * 3. true 表示: 持久化
     * durable： 队列是否持久化。 队列默认是存放到内存中的，rabbitmq 重启则丢失，
     * 若想重启之后还存在则队列要持久化，
     * 保存到 Erlang 自带的 Mnesia 数据库中，当 rabbitmq 重启之后会读取该数据库
     */
    @Bean
    public Queue queue_seckill() {
        return new Queue(QUEUE, true);
    }

    //创建交换机-Topic
    @Bean
    public TopicExchange topicExchange_seckill(){
        return new TopicExchange(EXCHANGE);
    }

    //将队列保存到交换机,并指定路由
    @Bean
    public Binding binding_seckill() {
        return BindingBuilder.bind(queue_seckill())
                .to(topicExchange_seckill()).with("seckill.#");
    }
}
