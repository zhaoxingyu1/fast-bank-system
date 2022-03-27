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
import com.seckill.productservice.service.IFinancialProductService;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    public void addFinancialProduct(FinancialProductEntity financialProductEntity) throws Exception{
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

            // 构造消息
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("product_id", f.getFinancialProductId());
            productMap.put("count", count);
            productMap.put("con_time", conTime);
            // 发送消息至延时队列
            rabbitTemplate.convertAndSend("delayProductQueue", productMap, message -> {
                // 获取当前时间戳，计算延时时间
//                long nowTime = System.currentTimeMillis();
//                long delayTime = financialProductEntity.getStartTime() - nowTime;
                // 将delayTime转换为毫秒，并转换为字符串
                String delayTimeStr = String.valueOf(delayTime);
                // 设置延时时间
                message.getMessageProperties().setExpiration(delayTimeStr);
                return message;
            });
//            System.out.println("成功发送了消息：" + new Date());
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
    public List<FinancialProductEntity> getProductById(int page) {
        //设置分页
        Page<FinancialProductEntity> page1 = new Page<>(page,PageConst.PageSize);
        //查询
        IPage<FinancialProductEntity> iPage = financialProductDao.selectPage(page1, null);
        return iPage.getRecords();
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
}
