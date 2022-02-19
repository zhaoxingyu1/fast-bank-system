package com.seckill.productservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seckill.productservice.dao.FinancialProductDao;
import com.seckill.common.entity.product.FinancialProductEntity;
import com.seckill.productservice.exception.*;
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

        QueryWrapper<FinancialProductEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("financial_product_name",financialProductEntity.getFinancialProductName());
        List<FinancialProductEntity> i = financialProductDao.selectList(queryWrapper);
        if(i.size() == 0){
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
    public void deleteFinancialProduct(long financialProductId) throws Exception{
        FinancialProductEntity re = financialProductDao.selectById(financialProductId);
        if (re != null){
            int delete = financialProductDao.deleteById(financialProductId);
            if(delete == 0){
                throw new DeleteProductionException("删除产品失败");
            }
        }else {
            throw new NotFindProductException("找不到指定产品");
        }

    }

    //更新产品信息
    @Override
    public void updateFinancialProduct(FinancialProductEntity financialProductEntity) throws Exception {
        FinancialProductEntity re = financialProductDao.selectById(financialProductEntity.getFinancialProductId());
        if(re != null){
            int update = financialProductDao.updateById(financialProductEntity);
            if(update == 0){
                throw new UpdateProductionException("更新产品信息失败");
            }
        }else{
            throw new NotFindProductException("找不到指定产品");
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
