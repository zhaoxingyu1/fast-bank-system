package com.seckill.common.response;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 陈征
 * @date : 2022-01-10 19:21
 */

@Getter
public class ListData extends BaseData {
    private final List<Object> data;

    public ListData(String msg) {
        this.msg = msg;
        this.data = new ArrayList<>();
    }

    @Override
    public void parse(Object data) {
        if (data instanceof List) {
            this.data.addAll((List) data);
        } else {
            this.data.add(data);
        }
    }
}
