package com.seckill.productservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.productservice.dao.LoanProductDao;
import com.seckill.productservice.entity.LoanProductEntity;
import com.seckill.productservice.service.ILoanProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanProductService implements ILoanProductService {
    private final LoanProductDao loanProductDao;

    @Autowired
    public LoanProductService(LoanProductDao loanProductDao) {
        this.loanProductDao = loanProductDao;
    }

    @Override
    public boolean addLoanProduct(LoanProductEntity loanProductEntity) {
        int i = loanProductDao.insert(loanProductEntity);
        if(i == 0){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public boolean deleteLoanProduct(long loanProductId) {
        int i = loanProductDao.deleteById(loanProductId);
        if(i == 0){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public boolean updateLoanProduct(LoanProductEntity loanProductEntity) {
        int i = loanProductDao.updateById(loanProductEntity);
        if(i == 0){
            return false;
        }else{
            return true;
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
}
