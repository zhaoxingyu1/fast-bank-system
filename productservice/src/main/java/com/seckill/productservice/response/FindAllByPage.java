package com.seckill.productservice.response;

import lombok.Data;

/**
 * @Author: 七画一只妖
 * @Date: 2022-04-03 10:43
 */
@Data
public class FindAllByPage<T> {
    // 产品对象
    private T productEntity;

    // 剩余库存
    private Integer stock;
}
