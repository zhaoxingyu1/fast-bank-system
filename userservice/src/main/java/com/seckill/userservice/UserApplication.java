package com.seckill.userservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author zxy
 * @Classname UserApplication
 * @Date 2022/2/10 19:55
 */
@MapperScan("com.seckill.userservice.dao")
//@EnableFeignClients(basePackages = {"com.seckill.common.feign"} )
@SpringBootApplication(scanBasePackages = {"com.seckill.userservice"} )
public class UserApplication {
//,"com.seckill.common.globalconfig"

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
