package com.seckill.productservice.service;

import com.seckill.common.entity.product.FinancialProductEntity;
import com.seckill.productservice.response.FindAllByPage;

import java.util.List;

/**
 * @Author: 七画一只妖
 * @Date: 2022/2/13 16:33
 */
public interface IFinancialProductService{
    //新增理财产品
    String addFinancialProduct(FinancialProductEntity financialProductEntity) throws Exception;

    //删除理财产品（根据ID）
    void deleteFinancialProduct(String financialProductId) throws Exception;

    //更新理财产品
    void updateFinancialProduct(FinancialProductEntity financialProductEntity) throws Exception;

    //查找理财产品（根据ID）
    FinancialProductEntity findFinancialProductById(String financialProductId);

    //查找所有理财产品
    List<FinancialProductEntity> findAll();

    //根据产品名称查询（模糊查询）
    List<FinancialProductEntity> findProductByName(String financialProductName);

    //分页查询产品
    List<FindAllByPage<FinancialProductEntity>> getProductById(int page);

    Object getProductsBatch(List<String> ids);
}
