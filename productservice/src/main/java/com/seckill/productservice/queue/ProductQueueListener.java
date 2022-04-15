package com.seckill.productservice.queue;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
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
@RabbitListener(queues = "delayed_queue")
public class ProductQueueListener {
    @Resource
    private RedisTemplate<String, Object> redis;

    @Autowired
    private SendUserEmail sendUserEmail;

    @RabbitHandler
    public void receive(Map<String, Object> map){
        System.out.println("收到了-------------------:" + new Date());

        String productId = null;
        Integer count = null;
        Long conTime = null;
        Integer type = null;

        try{
            productId = (String) map.get("product_id");
            count = (Integer) map.get("count");
            conTime = Long.valueOf(String.valueOf(map.get("con_time")));
            type = (Integer) map.get("type");
        } catch (Exception e){
            e.printStackTrace();
        }

        // 打印属性
        System.out.println("productId:" + productId);
        System.out.println("count:" + count);
        System.out.println("conTime:" + conTime);
        System.out.println("type:" + type);

        // 判断这四个属性是否为空，只要有一个为空，就不执行下面的代码
        if(productId == null || count == null || conTime == null || type == null){
            throw new RuntimeException("消息队列在进行推送时，消息中的属性有空值，请检查");
        }

        try{
            if(type == 1){
                //类型1，产品开始抢购的消息
                // 推送至缓存
                ValueOperations<String, Object> opsForValue = redis.opsForValue();
                opsForValue.set("pre" + productId,count);
                opsForValue.set(productId,count,conTime, TimeUnit.MILLISECONDS);
            }else if(type == 2){
                //类型2，产品开枪前五分钟发邮件提醒预约的人
                System.out.println("收到了-------------------:" + new Date());
                System.out.println("成功进入发邮件的逻辑");
                try {
                    sendUserEmail.sendEmailToUser(productId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                throw new RuntimeException("消息类型错误");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
