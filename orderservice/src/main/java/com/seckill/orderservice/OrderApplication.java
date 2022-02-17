package com.seckill.orderservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author zxy
 * @Classname OrderApplication
 * @Date 2022/2/11 13:08
 */
@MapperScan("com.seckill.orderservice.dao")
@EnableFeignClients(basePackages = "com.seckill.common.feign")
@SpringBootApplication(scanBasePackages = {"com.seckill.orderservice", "com.seckill.common.globalconfig"})
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
