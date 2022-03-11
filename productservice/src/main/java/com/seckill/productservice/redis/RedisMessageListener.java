package com.seckill.productservice.redis;

import com.alibaba.fastjson.JSONObject;
import com.seckill.common.entity.product.FinancialProductEntity;
import com.seckill.common.entity.product.LoanProductEntity;
import com.seckill.common.exception.DatabaseOperationException;
import com.seckill.productservice.dao.FinancialProductDao;
import com.seckill.productservice.dao.LoanProductDao;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * 监听库存过期
 * @Author: 七画一只妖
 * @Date: 2022/3/10 20:36
 */
@Component
public class RedisMessageListener implements MessageListener {
    @Resource
    private RedisTemplate<String, Object> redis;

    @Resource
    private FinancialProductDao financialProductDao;

    @Resource
    private LoanProductDao loanProductDao;

    /**
     * 产品结束抢购时，将剩余库存写入数据库，并删除redis钟对应键值对
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println("----------- " + new String(pattern));
        // 使用了 fastjson 来打印对象
        System.out.println(JSONObject.toJSONString(message, true));
        System.out.println("-------------------------------");
        // 获取过期的键
//        System.out.println(redis.getValueSerializer().deserialize(message.getBody()));

        String key = new String(message.getBody(), StandardCharsets.UTF_8);
        System.out.println(key);

        ValueOperations<String, Object> ops = redis.opsForValue();

        //取出库存
        Integer count = (Integer) ops.get("pre" + key);
        System.out.println(count);

        //删除永久键
        redis.delete("pre" + key);
        System.out.println("删除了" + key + "的持久键");

        //将剩余库存推送给数据库
        FinancialProductEntity financialProductEntity = financialProductDao.selectById(key);
        LoanProductEntity loanProductEntity = loanProductDao.selectById(key);
        if (financialProductEntity != null){
            financialProductEntity.setStock(count);
            int update = financialProductDao.updateById(financialProductEntity);
//            int update = financialProductDao.insert(financialProductEntity);
            if(update == 0){
                System.out.println("更新产品信息失败!");
            }else {
                System.out.println("更新成功，产品ID为:" + key);
            }
        }else if(loanProductEntity != null){
            loanProductEntity.setStock(count);
            int update = loanProductDao.updateById(loanProductEntity);
//            int update = loanProductDao.insert(loanProductEntity);
            if(update == 0){
                System.out.println("更新产品信息失败!");
            }else {
                System.out.println("更新成功，产品ID为:" + key);
            }
        }else {
            System.out.println("更新出错，请检查，ID:" + key);
        }
    }
}