package com.seckill.orderservice.config;

import com.seckill.common.consts.RabbitConsts;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : 陈征
 * @date : 2022-02-14 19:13
 */

@Configuration
public class RabbitConfig {
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue rabbitmqQueue() {
        return new Queue(RabbitConsts.LOAN_ORDER_QUEUE, true, false, false);
    }

    @Bean
    public DirectExchange rabbitmqDirectExchange() {
        return new DirectExchange(RabbitConsts.LOAN_ORDER_EXCHANGE, true, false);
    }

    @Bean
    public Binding bindDirect() {
        return BindingBuilder
                .bind(rabbitmqQueue())
                .to(rabbitmqDirectExchange())
                .with(RabbitConsts.LOAN_ORDER_ROUTING);
    }
}
