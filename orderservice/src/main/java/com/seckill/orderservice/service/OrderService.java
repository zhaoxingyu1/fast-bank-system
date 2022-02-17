package com.seckill.orderservice.service;

import com.seckill.common.entity.order.OrderEntity;
import com.seckill.orderservice.dao.OrderDao;
import com.seckill.orderservice.exception.DuplicateOrderException;
import com.seckill.orderservice.exception.ExhaustedStockException;
import com.seckill.orderservice.exception.OrderNotFoundException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author : 陈征
 * @date : 2022-02-14 21:10
 */

@Service
@Transactional
public class OrderService {
    @Resource
    private RedisTemplate<String, Object> redis;

    @Resource
    private RabbitTemplate rabbit;

    @Resource
    private OrderDao orderDao;

    public OrderEntity getById(Long id) throws OrderNotFoundException {
        if (id == null) {
            throw new OrderNotFoundException("id 为空");
        }
        final OrderEntity orderEntity = orderDao.selectById(id);
        if (orderEntity == null) {
            throw new OrderNotFoundException("未找到. id: " + id);
        }
        return orderEntity;
    }

    public void create(OrderEntity order) throws Exception {
        ValueOperations<String, Object> ops = redis.opsForValue();
        if (ops.get(order.getUserId()) != null) {
            throw new DuplicateOrderException("不能重复下单");
        }
        Long decrement = ops.decrement(order.getProductId());
        if (decrement < 0) {
            ops.increment(order.getProductId());
            throw new ExhaustedStockException("商品已卖完或者超出抢购期限");
        }
    }
}
