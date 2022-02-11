package com.seckill.productservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zxy
 * @Classname ProductApplication
 * @Date 2022/2/11 18:40
 */
@MapperScan("com.seckill.productservice.dao")
@SpringBootApplication
public class ProductApplication {

    public static void main(String[] args) {

        SpringApplication.run(ProductApplication.class);
    }
}
