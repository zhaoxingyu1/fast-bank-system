package com.seckill.common.entity.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zxy
 * @Classname loanProductEntity
 * @Date 2022/2/11 14:59
 */
@Data
@TableName("loan_product")
@EqualsAndHashCode(callSuper = true)
public class LoanProductEntity extends BaseProduct{
    @TableId(type = IdType.ASSIGN_UUID)
    private String loanProductId;
    private String loanProductName;
//    private String describe;
    // 贷款产品：利息
    private Double interest;

}
