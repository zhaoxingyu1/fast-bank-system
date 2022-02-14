package com.seckill.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.seckill.common.entity.product.BaseProduct;

/**
 * @author : 陈征
 * @date : 2022-02-14 20:29
 */

public class OrderEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    @TableField("order_id")
    Long orderId;

    @TableField("user_id")
    Long userId;

    @TableField("product_id")
    Long productId;

    @TableField(fill = FieldFill.INSERT)
    Long ctime;

    @TableField(exist = false)
    BaseProduct product;
}
