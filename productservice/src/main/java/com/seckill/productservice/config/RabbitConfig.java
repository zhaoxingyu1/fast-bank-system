package com.seckill.productservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @author : 七画一只妖
 * @date : 2022-02-14 19:13
 */

@Configuration
public class RabbitConfig {
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    // 定义延时队列
    @Bean("delayProductQueue")
    public Queue delayQueue(){
        return QueueBuilder.durable("delayProductQueue")
                .withArgument("x-dead-letter-exchange", "product-dlx-exchange")
                .withArgument("x-dead-letter-routing-key", "routing-key-delay")
                .build();
    }

    // 定义死信队列
    @Bean("dlxQueue")
    public Queue dlxQueue(){
        return QueueBuilder.durable("product-dlx-queue").build();
    }

    // 定义死信交换机
    @Bean("dlxExchange")
    public Exchange dlxExchange(){
        return ExchangeBuilder.directExchange("product-dlx-exchange").build();
    }

    //绑定队列和交换机
    @Bean("dlxBinding")
    public Binding dlxBinding(@Qualifier("dlxExchange")Exchange exchange,
                              @Qualifier("dlxQueue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with("routing-key-delay").noargs();
    }





//    //交换机
//    public static final String DELAYED_EXCHANGE ="delayed_exchange";
//    //队列
//    public static final String DELAYED_QUEUE ="delayed_queue";
//    //routeingKey
//    public static final String DELAYED_ROUTINGKEY ="delayed_routingKey";
//
//    //声明延迟交换机
//    @Bean
//    public CustomExchange delayedExchange(){
//        HashMap<String, Object> arguments = new HashMap<>();
//        //自定义交换机的类型
//        arguments.put("x-delayed-type", "direct");
//        /**
//         * 交换机名
//         * 交换机类型
//         * 持久化
//         * 自动删除
//         */
//        return new CustomExchange(DELAYED_EXCHANGE,"x-delayed-message",true,false,arguments);
//    }
//
//    /**
//     * 声明队列
//     * @return
//     */
//    @Bean
//    public Queue delayedQueue(){
//        return new Queue(DELAYED_QUEUE);
//    }
//
//    //延迟交换机和队列绑定
//    @Bean
//    public Binding delayedQueueBindingDelayedExchange(Queue delayedQueue,CustomExchange delayedExchange){
//        return BindingBuilder.bind(delayedQueue).to(delayedExchange).with(DELAYED_ROUTINGKEY).noargs();
//    }
}
