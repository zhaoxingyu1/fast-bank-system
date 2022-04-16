package com.seckill.common.entity.order;

import com.baomidou.mybatisplus.annotation.*;
import com.seckill.common.entity.product.BaseProduct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.security.DenyAll;

/**
 * @author : 陈征
 * @date : 2022-02-14 20:29
 */

@Data
@TableName("c_order")
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    String orderId;

    String userId;

    String productId;

    String state;

    String productType;

    @TableField(fill = FieldFill.INSERT)
    Long ctime;

    public OrderEntity(OrderEntity entity) {
        this.orderId = entity.orderId;
        this.userId = entity.userId;
        this.productId = entity.productId;
        this.state = entity.state;
        this.productType = entity.productType;
        this.ctime = entity.ctime;
    }
}
