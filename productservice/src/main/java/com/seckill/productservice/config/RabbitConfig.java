package com.seckill.productservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : 陈征
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
}
