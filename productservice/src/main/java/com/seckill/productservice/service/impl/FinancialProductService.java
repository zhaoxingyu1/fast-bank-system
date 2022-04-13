package com.seckill.productservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.consts.PageConst;
import com.seckill.common.entity.product.ProductTypeEntity;
import com.seckill.common.exception.DatabaseOperationException;
import com.seckill.common.exception.NotFoundException;
import com.seckill.productservice.dao.FinancialProductDao;
import com.seckill.common.entity.product.FinancialProductEntity;
import com.seckill.productservice.dao.ProductTypeDao;
import com.seckill.productservice.response.FindAllByPage;
import com.seckill.productservice.service.IFinancialProductService;
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

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private ProductTypeDao productTypeDao;


    @Override
    public String addFinancialProduct(FinancialProductEntity financialProductEntity) throws Exception{
        // 获取当前时间戳，计算延时时间，判断时间是否合法
        long nowTime = System.currentTimeMillis();
        long delayTime = financialProductEntity.getStartTime() - nowTime;

        if (delayTime < 0) {
            throw new DatabaseOperationException("开抢时间不合法");
        }
        System.out.println(financialProductEntity);
        QueryWrapper<FinancialProductEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("financial_product_name",financialProductEntity.getFinancialProductName());
        List<FinancialProductEntity> i = financialProductDao.selectList(queryWrapper);
//        FinancialProductEntity i = financialProductDao.selectOne(queryWrapper);
        if(i.size() == 0){
            // 插入数据库
            financialProductDao.insert(financialProductEntity);

            // 计算时间间隔
            Integer count = financialProductEntity.getStock();
            long conTime = financialProductEntity.getEndTime() - financialProductEntity.getStartTime();

            if(conTime <= 0){
                throw new DatabaseOperationException("时间间隔不合法，相隔时间戳必须大于0");
            }
            // 获取ID值要重新查询出来
            QueryWrapper<FinancialProductEntity> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("financial_product_name",financialProductEntity.getFinancialProductName());
            FinancialProductEntity f = financialProductDao.selectOne(queryWrapper2);

            // 还需要同时更新product_type表
            ProductTypeEntity productTypeEntity = new ProductTypeEntity();
            productTypeEntity.setType("financial");
            productTypeEntity.setProductId(f.getFinancialProductId());
            productTypeDao.insert(productTypeEntity);


            // 2.发送消息至延时队列（产品开抢前五分钟的提醒）
            if (getTime(delayTime)>0){
                HashMap<String, Object> productMap2 = new HashMap<>();
                productMap2.put("product_id", f.getFinancialProductId());
                productMap2.put("count", count);
                productMap2.put("con_time", conTime);
                productMap2.put("type", 2);
                rabbitTemplate.convertAndSend("delayProductQueue", productMap2, message -> {
                    // 设置延时时间
                    message.getMessageProperties().setDelay((int)getTime(delayTime));
                    return message;
                });
            }


            // 构造消息
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("product_id", f.getFinancialProductId());
            productMap.put("count", count);
            productMap.put("con_time", conTime);
            productMap.put("type", 1);
            // 1.发送消息至延时队列（产品的抢购开始）
            rabbitTemplate.convertAndSend("delayProductQueue", productMap, message -> {
                // 设置消息队列延时时间
                String delayTimeStr = String.valueOf(delayTime);
                message.getMessageProperties().setExpiration(delayTimeStr);
                return message;
            });


            // 返回产品ID
            return f.getFinancialProductId();
        }else{
            throw new DatabaseOperationException("产品已存在，无需重复添加");
        }
    }

    @Override
    public void deleteFinancialProduct(String financialProductId) throws Exception{
        FinancialProductEntity re = financialProductDao.selectById(financialProductId);
        if (re != null){
            int delete = financialProductDao.deleteById(financialProductId);
            if(delete == 0){
                throw new DatabaseOperationException("删除产品失败");
            } else {
                //redis 删除
                redis.delete(String.valueOf(financialProductId));
            }
        }else {
            throw new NotFoundException("找不到指定产品");
        }

    }

    //更新产品信息
    @Override
    public void updateFinancialProduct(FinancialProductEntity financialProductEntity) throws Exception {
        FinancialProductEntity re = financialProductDao.selectById(financialProductEntity.getFinancialProductId());
        if(re != null){
            int update = financialProductDao.updateById(financialProductEntity);
            if(update == 0){
                throw new DatabaseOperationException("更新产品信息失败");
            }else{
                ValueOperations<String, Object> opsForValue = redis.opsForValue();
                Integer stock = financialProductEntity.getStock();
                opsForValue.set(financialProductEntity.getFinancialProductId(),stock);
            }
        }else{
            throw new NotFoundException("找不到指定产品");
        }
    }

    @Override
    public FinancialProductEntity findFinancialProductById(String financialProductId) {
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

    @Override
    public List<FindAllByPage<FinancialProductEntity>> getProductById(int page) {
        //设置分页
        Page<FinancialProductEntity> page1 = new Page<>(page,PageConst.PageSize);
        //查询
        IPage<FinancialProductEntity> iPage = financialProductDao.selectPage(page1, null);
        ValueOperations<String, Object> opsForValue = redis.opsForValue();

        List<FinancialProductEntity> records = iPage.getRecords();
        List<FindAllByPage<FinancialProductEntity>> response = new ArrayList<>();
        for (FinancialProductEntity financialProductEntity : records) {
            String financialProductId = financialProductEntity.getFinancialProductId();
            Integer count = (Integer) opsForValue.get(financialProductId);
            if (count == null){
                // 没在缓存，则查询数据库
                count = financialProductDao.selectById(financialProductId).getStock();
            }
            // 加入到返回结果中
            FindAllByPage<FinancialProductEntity> objectFindAllByPage = new FindAllByPage<>();
            objectFindAllByPage.setProductEntity(financialProductEntity);
            objectFindAllByPage.setStock(count);
            response.add(objectFindAllByPage);
        }
        return response;
    }

    @Override
    public Object getProductsBatch(List<String> ids) {
        //方法要求根据 id 的 List 查出对应的所有产品，以 List 形式返回
        List<FinancialProductEntity> list = new ArrayList<>();
        for (String id : ids) {
            FinancialProductEntity financialProductEntity = financialProductDao.selectById(id);
            list.add(financialProductEntity);
        }
        return list;
    }

    /**
     * 计算这个时间前5分钟的时间
     * @return
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
