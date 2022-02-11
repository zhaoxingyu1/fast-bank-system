package com.seckill.userservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zxy
 * @Classname UserApplication
 * @Date 2022/2/10 19:55
 */
@MapperScan("com.seckill.userservice.dao")
@SpringBootApplication
public class UserApplication {


    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}