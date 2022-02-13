package com.seckill.productservice.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zxy
 * @Classname financialProductEntity
 * @Date 2022/2/11 15:03
 */
@Data
@TableName("financial_product")
public class FinancialProductEntity {
    @TableId
    private Long financialProductId;
    private String financialProductName;
    private BigDecimal price;
    private Integer stock;
    private Long ctime;
}
