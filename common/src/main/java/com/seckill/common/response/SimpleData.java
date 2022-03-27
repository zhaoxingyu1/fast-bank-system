package com.seckill.common.response;

import lombok.Getter;

/**
 * @author : 陈征
 * @date : 2022-01-10 19:19
 */

@Getter
public class SimpleData extends BaseData {
    Object data;

    public SimpleData(String msg) {
        this.msg = msg;
    }

    @Override
    protected void parse(Object data) {
        this.data = data;
    }
}
