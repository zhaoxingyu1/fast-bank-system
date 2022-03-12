package com.seckill.productservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.consts.PageConst;
import com.seckill.common.exception.DatabaseOperationException;
import com.seckill.common.exception.NotFoundException;
import com.seckill.productservice.dao.LoanProductDao;
import com.seckill.common.entity.product.LoanProductEntity;
import com.seckill.productservice.service.ILoanProductService;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class LoanProductService implements ILoanProductService {
    @Resource
    private LoanProductDao loanProductDao;

    @Resource
    private RedisTemplate<String, Object> redis;

    @Override
    public void addLoanProduct(LoanProductEntity loanProductEntity) throws Exception{
        QueryWrapper<LoanProductEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("loan_product_name",loanProductEntity.getLoanProductName());
        List<LoanProductEntity> i = loanProductDao.selectList(queryWrapper);
        if(i.size() == 0){
            int insert = loanProductDao.insert(loanProductEntity);
            if (insert == 0){
                throw new DatabaseOperationException("添加产品失败");
            }else{
                // redis插入  产品ID：库存
                ValueOperations<String, Object> opsForValue = redis.opsForValue();
                long conTime = loanProductEntity.getEndTime() - loanProductEntity.getStartTime();
                Integer count = loanProductEntity.getStock();
                opsForValue.set(loanProductEntity.getLoanProductId(),count);
                redis.expire(loanProductEntity.getLoanProductId(),conTime, TimeUnit.MILLISECONDS);
                //todo 计划新增产品时，计算当前时间与抢购时间的间隔，使用队列延时加入至队列
            }
        }else {
            throw new DatabaseOperationException("产品已存在，无需重复添加");
        }
    }

    @Override
    public void deleteLoanProduct(String loanProductId) throws Exception{
        LoanProductEntity re = loanProductDao.selectById(loanProductId);
        if(re != null){
            int delete = loanProductDao.deleteById(loanProductId);
            if(delete == 0){
                throw new DatabaseOperationException("删除产品失败");
            }else{
                //redis 删除
                redis.delete(String.valueOf(loanProductId));
            }
        }else{
            throw new NotFoundException("找不到指定产品");
        }
    }

    @Override
    public void updateLoanProduct(LoanProductEntity loanProductEntity) throws Exception{
        LoanProductEntity re = loanProductDao.selectById(loanProductEntity.getLoanProductId());
        if(re != null){
            int delete = loanProductDao.updateById(loanProductEntity);
            if(delete == 0){
                throw new DatabaseOperationException("更新产品失败");
            }else {
                ValueOperations<String, Object> opsForValue = redis.opsForValue();
                Integer stock = loanProductEntity.getStock();
                opsForValue.set(loanProductEntity.getLoanProductId(),stock);
            }
        }else{
            throw new NotFoundException("找不到指定产品");
        }

    }

    @Override
    public LoanProductEntity findLoanProductById(String loanProductId) {
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

    @Override
    public List<LoanProductEntity> getProductById(int page) {
        Page<LoanProductEntity> objectPage = new Page<>(page, PageConst.PageSize);
        loanProductDao.selectPage(objectPage, null);
        return objectPage.getRecords();
    }
}
