package com.seckill.productservice.service;

import com.seckill.common.entity.product.LoanProductEntity;

import java.util.List;

/**
 * @Author: 七画一只妖
 * @Date: 2022/2/13 16:33
 */
public interface ILoanProductService{
    //新增贷款产品
    boolean addLoanProduct(LoanProductEntity loanProductEntity);

    //删除贷款产品
    boolean deleteLoanProduct(long loanProductId);

    //更新贷款产品
    boolean updateLoanProduct(LoanProductEntity loanProductEntity);

    //查找贷款产品（根据ID）
    LoanProductEntity findLoanProductById(long loanProductId);

    //查找贷款产品（所有）
    List<LoanProductEntity> findAll();

    //根据产品名称查询（模糊查询）
    List<LoanProductEntity> findProductByName(String loanProductName);
}
