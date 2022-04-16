package com.seckill.common.entity.product;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author: 七画一只妖
 * @Date: 2022-03-24 21:07
 */
@Data
@TableName("product_type")
public class ProductTypeEntity {
    @TableId
    private String productId;

    private String type;
}
