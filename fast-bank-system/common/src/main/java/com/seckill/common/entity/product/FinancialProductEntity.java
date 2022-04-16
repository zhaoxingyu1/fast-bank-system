package com.seckill.common.entity.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zxy
 * @Classname financialProductEntity
 * @Date 2022/2/11 15:03
 */
@Data
@TableName("financial_product")
@EqualsAndHashCode(callSuper = true)
public class FinancialProductEntity extends BaseProduct {
    @TableId(type = IdType.ASSIGN_UUID)
    private String financialProductId;
    private String financialProductName;
//    private String describe;
    // 理财产品：利率
    private Double rate;
}
