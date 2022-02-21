package com.seckill.common.entity.product;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: 七画一只妖
 * @Date: 2022/2/14 21:48
 */
@Data
public class BaseProduct {
    private BigDecimal price;
    private Integer stock;
    private Long ctime;
    private Long startTime;
    private Long endTime;
}
