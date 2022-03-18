package com.seckill.productservice.queue;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 七画一只妖
 * @Date: 2022/3/12 13:12
 */
@Component
@RabbitListener(queues = "product-dlx-queue")
public class ProductQueueListener {
    @Resource
    private RedisTemplate<String, Object> redis;

    @RabbitHandler
    public void receive(Map<String, Object> map){
        System.out.println("收到了-------------------:" + new Date());
        String productId = (String) map.get("product_id");
        Integer count = (Integer) map.get("count");
        Long conTime = Long.valueOf(String.valueOf(map.get("con_time")));

        // 简单测试一下
//        System.out.println("产品ID是：" + productId);
//        System.out.println("产品库存是：" + count);
//        System.out.println("缓存过期时间是" + conTime);

        // 推送至缓存
        ValueOperations<String, Object> opsForValue = redis.opsForValue();
        opsForValue.set("pre" + productId,count);
        opsForValue.set(productId,count,conTime, TimeUnit.MILLISECONDS);
    }
}
