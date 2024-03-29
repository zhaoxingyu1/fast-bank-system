package com.seckill.productservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.consts.PageConst;
import com.seckill.common.entity.product.ProductTypeEntity;
import com.seckill.common.exception.DatabaseOperationException;
import com.seckill.common.exception.NotFoundException;
import com.seckill.productservice.dao.LoanProductDao;
import com.seckill.common.entity.product.LoanProductEntity;
import com.seckill.productservice.dao.ProductTypeDao;
import com.seckill.productservice.response.FindAllByPage;
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

    @Resource
    private ProductTypeDao productTypeDao;

    @Override
    public String addLoanProduct(LoanProductEntity loanProductEntity) throws Exception{
        // 获取当前时间戳，计算延时时间，判断时间是否合法
        long nowTime = System.currentTimeMillis();
        long delayTime = loanProductEntity.getStartTime() - nowTime;
        if (delayTime < 0) {
            throw new DatabaseOperationException("开抢时间不合法");
        }
        QueryWrapper<LoanProductEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("loan_product_name",loanProductEntity.getLoanProductName());
        List<LoanProductEntity> i = loanProductDao.selectList(queryWrapper);
        if(i.size() == 0){
            loanProductDao.insert(loanProductEntity);
            //计算startTime和endTime的间隔
            long interval = loanProductEntity.getEndTime() - loanProductEntity.getStartTime();
            if(interval <= 0){
                throw new DatabaseOperationException("时间间隔不合法，相隔时间戳必须大于0");
            }
            // 获取库存
            Integer stock = loanProductEntity.getStock();

            // 查询ID
            QueryWrapper<LoanProductEntity> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("loan_product_name",loanProductEntity.getLoanProductName());
            LoanProductEntity l = loanProductDao.selectOne(queryWrapper1);

            // 还需要同时更新product_type表
            ProductTypeEntity productTypeEntity = new ProductTypeEntity();
            productTypeEntity.setProductId(l.getLoanProductId());
            productTypeEntity.setType("loan");
            productTypeDao.insert(productTypeEntity);

            // 1.发送消息至延时队列（产品开抢前五分钟的提醒）
            HashMap<Object, Object> productMap1 = new HashMap<>();
            productMap1.put("product_id", l.getLoanProductId());
            productMap1.put("count", stock);
            productMap1.put("con_time", interval);
            productMap1.put("type", 2);
            rabbitTemplate.convertAndSend("delayed_exchange","routinkey", productMap1, message -> {
                message.getMessageProperties().setDelay((int)getTime(delayTime));
                return message;
            });

            // 2.将消息发送到延时队列delayProductQueue（开抢的消息）
            HashMap<Object, Object> productMap2 = new HashMap<>();
            productMap2.put("product_id", l.getLoanProductId());
            productMap2.put("count", stock);
            productMap2.put("con_time", interval);
            productMap2.put("type", 1);
            rabbitTemplate.convertAndSend("delayed_exchange","routinkey", productMap2, message -> {
                // 将delayTime转换为毫秒，并转换为字符串
                // 设置延时时间
                message.getMessageProperties().setDelay((int)delayTime);
                return message;
            });
            return l.getLoanProductId();
        }else {
            throw new DatabaseOperationException("产品已存在，无需重复添加");
        }
    }

    @Override
    public void deleteLoanProduct(String loanProductId) throws Exception{
        LoanProductEntity re = loanProductDao.selectById(loanProductId);
        productTypeDao.deleteById(loanProductId);
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
    public List<FindAllByPage<LoanProductEntity>> getProductById(int page) {
        Page<LoanProductEntity> page1 = new Page<>(page, PageConst.PageSize);
        IPage<LoanProductEntity> iPage = loanProductDao.selectPage(page1, null);
        ValueOperations<String, Object> opsForValue = redis.opsForValue();

        List<LoanProductEntity> records = iPage.getRecords();
        List<FindAllByPage<LoanProductEntity>> response = new ArrayList<>();
        for (LoanProductEntity loanProductEntity : records) {
            Integer count = (Integer) opsForValue.get(loanProductEntity.getLoanProductId());
            if (count == null) {
                count = loanProductDao.selectById(loanProductEntity.getLoanProductId()).getStock();
            }
            // 将数据放入到response中
            FindAllByPage<LoanProductEntity> findAllByPage = new FindAllByPage<>();
            findAllByPage.setProductEntity(loanProductEntity);
            findAllByPage.setStock(count);
            response.add(findAllByPage);
        }
        return response;
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

    /**
     * 计算这个时间前5分钟的时间
     */
    private long getTime(long time){
        // 五分钟的毫秒数
        long fiveMinute = 5 * 60 * 1000;

        // 计算延时时间（邮件提醒消息需要提早五分钟发送给用户）
        long delayTime = time - fiveMinute;
        // 如果延时时间小于五分钟，则延时五分钟
        if (delayTime < 0){
            return 0;
        }
        return delayTime;
    }
}
