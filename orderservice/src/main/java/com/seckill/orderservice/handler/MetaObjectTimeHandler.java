package com.seckill.orderservice.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author : 陈征
 * @date : 2022-02-14 16:38
 */

@Component
public class MetaObjectTimeHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        fillStrategy(metaObject, "ctime", new Date().getTime());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        fillStrategy(metaObject, "mtime", new Date().getTime());
    }
}
