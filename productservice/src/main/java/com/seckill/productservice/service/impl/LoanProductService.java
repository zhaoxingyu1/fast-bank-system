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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class LoanProductService implements ILoanProductService {
    @Resource
    private LoanProductDao loanProductDao;

    @Resource
    private RedisTemplate<String, Object> redis;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public void addLoanProduct(LoanProductEntity loanProductEntity) throws Exception{
        QueryWrapper<LoanProductEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("loan_product_name",loanProductEntity.getLoanProductName());
        List<LoanProductEntity> i = loanProductDao.selectList(queryWrapper);
        if(i.size() == 0){
            loanProductDao.insert(loanProductEntity);
            //计算startTime和endTime的间隔
            long interval = loanProductEntity.getEndTime() - loanProductEntity.getStartTime();
            // 获取库存
            Integer stock = loanProductEntity.getStock();

            // 查询ID
            QueryWrapper<LoanProductEntity> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("loan_product_name",loanProductEntity.getLoanProductName());
            LoanProductEntity l = loanProductDao.selectOne(queryWrapper1);

            // 构造消息，将id、count、interval放入map中
            HashMap<Object, Object> productMap = new HashMap<>();
            productMap.put("product_id", l.getLoanProductId());
            productMap.put("count", stock);
            productMap.put("con_time", interval);

            // 将消息发送到延时队列delayProductQueue
            rabbitTemplate.convertAndSend("delayProductQueue", productMap, message -> {
                // 获取当前时间戳，计算延时时间
                long nowTime = System.currentTimeMillis();
                long delayTime = loanProductEntity.getStartTime() - nowTime;
                // 将delayTime转换为毫秒，并转换为字符串
                String delayTimeStr = String.valueOf(delayTime);
                // 设置延时时间
                message.getMessageProperties().setExpiration(delayTimeStr);
                return message;
            });
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

    @Override
    public Object getProductsBatch(List<String> ids) {
        //方法要求根据 id 的 List 查出对应的所有产品，以 List 形式返回
        List<LoanProductEntity> list = new ArrayList<>();
        for (String id : ids) {
            LoanProductEntity loanProductEntity = loanProductDao.selectById(id);
            list.add(loanProductEntity);
        }
        return list;
    }
}
