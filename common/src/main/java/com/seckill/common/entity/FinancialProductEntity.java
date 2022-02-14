package com.seckill.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author zxy
 * @Classname financialProductEntity
 * @Date 2022/2/11 15:03
 */
@Data
@TableName("financial_product")
@EqualsAndHashCode(callSuper = true)
public class FinancialProductEntity extends BaseProduct {
    @TableId
    private Long financialProductId;
    private String financialProductName;
}
