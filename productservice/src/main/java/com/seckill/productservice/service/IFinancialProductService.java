package com.seckill.productservice.service;

import com.seckill.common.entity.FinancialProductEntity;

import java.util.List;

/**
 * @Author: 七画一只妖
 * @Date: 2022/2/13 16:33
 */
public interface IFinancialProductService{
    //新增理财产品
    boolean addFinancialProduct(FinancialProductEntity financialProductEntity);

    //删除理财产品（根据ID）
    boolean deleteFinancialProduct(long financialProductId);

    //更新理财产品
    boolean updateFinancialProduct(FinancialProductEntity financialProductEntity);

    //查找理财产品（根据ID）
    FinancialProductEntity findFinancialProductById(long financialProductId);

    //查找所有理财产品
    List<FinancialProductEntity> findAll();

    //根据产品名称查询（模糊查询）
    List<FinancialProductEntity> findProductByName(String financialProductName);
}
