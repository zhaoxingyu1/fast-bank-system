package com.seckill.productservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seckill.productservice.dao.FinancialProductDao;
import com.seckill.common.entity.product.FinancialProductEntity;
import com.seckill.productservice.exception.AddProductionException;
import com.seckill.productservice.exception.AlreadyExistsProductException;
import com.seckill.productservice.service.IFinancialProductService;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zxy
 * @Classname FinancialProductService
 * @Date 2022/2/11 15:05
 */
@Service
@Transactional
public class FinancialProductService implements IFinancialProductService {
    @Resource
    private FinancialProductDao financialProductDao;

    @Resource
    private RedisTemplate<String, Object> redis;


    @Override
    public void addFinancialProduct(FinancialProductEntity financialProductEntity) throws Exception{
        // redis插入  产品ID：库存
        ValueOperations<String, Object> opsForValue = redis.opsForValue();

        FinancialProductEntity i = financialProductDao.selectById(financialProductEntity.getFinancialProductId());
        if(i == null){
            int insert = financialProductDao.insert(financialProductEntity);
            if (insert == 0){
                throw new AddProductionException("添加产品失败");
            }else{
                Integer count = financialProductEntity.getStock();
                opsForValue.set(financialProductEntity.getFinancialProductId(),count);
            }
        }else{
            throw new AlreadyExistsProductException("产品已存在，无需重复添加");
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
