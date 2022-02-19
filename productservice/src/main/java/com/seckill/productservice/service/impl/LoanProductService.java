package com.seckill.productservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seckill.productservice.dao.LoanProductDao;
import com.seckill.common.entity.product.LoanProductEntity;
import com.seckill.productservice.exception.*;
import com.seckill.productservice.service.ILoanProductService;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LoanProductService implements ILoanProductService {
    @Resource
    private LoanProductDao loanProductDao;

    @Resource
    private RedisTemplate<String, Object> redis;

    @Override
    public void addLoanProduct(LoanProductEntity loanProductEntity) throws Exception{
        // redis插入  产品ID：库存
        ValueOperations<String, Object> opsForValue = redis.opsForValue();

        QueryWrapper<LoanProductEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("loan_product_name",loanProductEntity.getLoanProductName());
        List<LoanProductEntity> i = loanProductDao.selectList(queryWrapper);
        if(i.size() == 0){
            int insert = loanProductDao.insert(loanProductEntity);
            if (insert == 0){
                throw new AddProductionException("添加产品失败");
            }else{
                Integer count = loanProductEntity.getStock();
                opsForValue.set(loanProductEntity.getLoanProductId(),count);
            }
        }else {
            throw new AlreadyExistsProductException("产品已存在，无需重复添加");
        }
    }

    @Override
    public void deleteLoanProduct(long loanProductId) throws Exception{
        LoanProductEntity re = loanProductDao.selectById(loanProductId);
        if(re != null){
            int delete = loanProductDao.deleteById(loanProductId);
            if(delete == 0){
                throw new DeleteProductionException("删除产品失败");
            }
        }else{
            throw new NotFindProductException("找不到指定产品");
        }
    }

    @Override
    public void updateLoanProduct(LoanProductEntity loanProductEntity) throws Exception{
        LoanProductEntity re = loanProductDao.selectById(loanProductEntity.getLoanProductId());
        if(re != null){
            int delete = loanProductDao.updateById(loanProductEntity);
            if(delete == 0){
                throw new UpdateProductionException("更新产品失败");
            }
        }else{
            throw new NotFindProductException("找不到指定产品");
        }

    }

    @Override
    public LoanProductEntity findLoanProductById(long loanProductId) {
        return loanProductDao.selectById(loanProductId);
    }

    @Override
    public List<LoanProductEntity> findAll() {
        return loanProductDao.selectList(null);
    }

    @Override
    public List<LoanProductEntity> findProductByName(String loanProductName) {
        QueryWrapper<LoanProductEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(loanProductName),"loan_product_name",loanProductName);
        return loanProductDao.selectList(queryWrapper);
    }
}
