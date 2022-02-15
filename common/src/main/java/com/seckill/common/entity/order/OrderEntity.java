package com.seckill.common.entity.order;

import com.baomidou.mybatisplus.annotation.*;
import com.seckill.common.entity.product.BaseProduct;
import lombok.Data;

/**
 * @author : 陈征
 * @date : 2022-02-14 20:29
 */

@Data
@TableName("order")
public class OrderEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    String orderId;

    String userId;

    String productId;

    @TableField(fill = FieldFill.INSERT)
    Long ctime;

    @TableField(exist = false)
    BaseProduct product;
}
