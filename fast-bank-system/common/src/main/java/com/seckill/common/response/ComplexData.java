package com.seckill.common.response;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : 陈征
 * @date : 2022-01-10 19:07
 */

@Getter
public class ComplexData extends BaseData {
    private final Map<String, Object> data;

    public ComplexData(String msg) {
        this.msg = msg;
        this.data = new HashMap<>();
    }

    @Override
    public void parse(Object entity) {
        String[] keyClass = entity.getClass().toString().split("\\.");
        if (!keyClass[keyClass.length - 1].endsWith("Entity")) {
            return;
        }
        String keyName = keyClass[keyClass.length - 1].split("Entity")[0];
        this.data.put(keyName.substring(0, 1).toLowerCase() + keyName.substring(1), entity);
    }

    public ComplexData parseData(String key, Object val) {
        data.put(key, val);
        return this;
    }
}
