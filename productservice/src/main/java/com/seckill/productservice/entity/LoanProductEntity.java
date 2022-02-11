package com.seckill.productservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zxy
 * @Classname loanProductEntity
 * @Date 2022/2/11 14:59
 */
@Data
@TableName("loan_product")
public class LoanProductEntity {
    private Long loanProductId;
    private String loanProductName;
    private BigDecimal price;
    private Integer stock;
    private Long ctime;
}
