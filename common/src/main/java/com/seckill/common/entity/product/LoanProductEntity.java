package com.seckill.common.entity.product;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
    @TableId
    private Long loanProductId;
    private String loanProductName;
}
