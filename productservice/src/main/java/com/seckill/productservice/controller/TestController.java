package com.seckill.productservice.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 测试redis键过期事件（测试用）
 * @Author: 七画一只妖
 * @Date: 2022/3/10 20:38
 */
@RestController
@RequestMapping("/product")
public class TestController {
    @Resource
    private RedisTemplate<String, Object> redis;

    @GetMapping("/redis")
    public void re() {
        ValueOperations<String, Object> ops = redis.opsForValue();
        ops.set("123", "456");
        // 设置需要过期的键，过期时间，时间单位
        redis.expire("123", 5000L, TimeUnit.MILLISECONDS);
    }
}
