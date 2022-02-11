package com.seckill.orderservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author zxy
 * @Classname OrderEntity
 * @Date 2022/2/11 14:09
 */
@Data
@TableName("order")
public class OrderEntity {

    private Long orderId;
    private Long UserId;
    private Long productId;
    private Long ctime;
}
