package com.seckill.productservice.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Author: 七画一只妖
 * @Date: 2022/2/21 18:52
 */
@Configuration
@Slf4j
public class MetaObjectTimeHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("属性自动填充：创建世界ctime");
        fillStrategy(metaObject, "ctime", System.currentTimeMillis());
    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }
}
