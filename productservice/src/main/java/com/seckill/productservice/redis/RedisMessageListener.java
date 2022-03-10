package com.seckill.productservice.redis;

import com.alibaba.fastjson.JSONObject;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: 七画一只妖
 * @Date: 2022/3/10 20:36
 */
@Component
public class RedisMessageListener implements MessageListener {
    @Resource
    private RedisTemplate<String, Object> redis;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println("----------- " + new String(pattern));
        // 使用了 fastjson 来打印对象
        System.out.println(JSONObject.toJSONString(message, true));
        System.out.println("-------------------------------");
        // 获取过期的键
        System.out.println(redis.getValueSerializer().deserialize(message.getBody()));
    }
}