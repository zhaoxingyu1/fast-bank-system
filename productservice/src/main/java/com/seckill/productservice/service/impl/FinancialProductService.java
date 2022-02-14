package com.seckill.productservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seckill.productservice.dao.FinancialProductDao;
import com.seckill.common.entity.product.FinancialProductEntity;
import com.seckill.productservice.service.IFinancialProductService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zxy
 * @Classname FinancialProductService
 * @Date 2022/2/11 15:05
 */
@Service
public class FinancialProductService implements IFinancialProductService {
    private final FinancialProductDao financialProductDao;

    @Autowired
    public FinancialProductService(FinancialProductDao financialProductDao) {
        this.financialProductDao = financialProductDao;
    }

    @Override
    public boolean addFinancialProduct(FinancialProductEntity financialProductEntity) {
        int i = financialProductDao.insert(financialProductEntity);
        if(i == 0){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public boolean deleteFinancialProduct(long financialProductId) {
        int i = financialProductDao.deleteById(financialProductId);
        if(i == 0){
            return false;
        }else{
            return true;
        }
    }

    //更新产品信息
    @Override
    public boolean updateFinancialProduct(FinancialProductEntity financialProductEntity) {
        int i = financialProductDao.updateById(financialProductEntity);
        if(i == 0){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public FinancialProductEntity findFinancialProductById(long financialProductId) {
        return financialProductDao.selectById(financialProductId);
    }

    @Override
    public List<FinancialProductEntity> findAll() {
        return financialProductDao.selectList(null);
    }

    @Override
    public List<FinancialProductEntity> findProductByName(String financialProductName) {
        QueryWrapper<FinancialProductEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(financialProductName),"financial_product_name",financialProductName);
        return financialProductDao.selectList(queryWrapper);
    }
}
