package com.seckill.gateway;

import org.apache.http.entity.ContentLengthStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
//import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author zxy
 * @Classname GatewayApplication
 * @Date 2022/2/11 13:24
 */
@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
    SpringApplication.run(GatewayApplication.class, args);
}
}
