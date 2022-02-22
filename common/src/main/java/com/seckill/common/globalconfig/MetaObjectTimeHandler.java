package com.seckill.common.globalconfig;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * @author : 陈征
 * @date : 2022-02-14 16:38
 */

@Component
public class MetaObjectTimeHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        fillStrategy(metaObject, "ctime", System.currentTimeMillis());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        fillStrategy(metaObject, "mtime", System.currentTimeMillis());
    }
}
