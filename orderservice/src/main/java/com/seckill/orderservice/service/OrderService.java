package com.seckill.orderservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.consts.PageConst;
import com.seckill.common.consts.RabbitConsts;
import com.seckill.common.entity.order.OrderEntity;
import com.seckill.common.exception.ForbiddenException;
import com.seckill.common.exception.NotFoundException;
import com.seckill.orderservice.dao.OrderDao;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public OrderEntity getById(String id) throws Exception {
        if (id == null) {
            throw new NotFoundException("id 为空");
        }
        OrderEntity orderEntity = orderDao.selectById(id);
//        todo 调用产品的getbyid

        if (orderEntity == null) {
            throw new NotFoundException("未找到. id: " + id);
        }
        return orderEntity;
    }

    public Page<OrderEntity> getByUserId(String id, int page) throws Exception {
        if (id == null) {
            throw new NotFoundException("id 为空");
        }
        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<OrderEntity>()
                .eq("user_id", id)
                .orderByDesc("ctime");
        Page<OrderEntity> res = orderDao.selectPage(new Page<>(page, PageConst.PageSize), wrapper);

        List<String> ids = res.getRecords().stream().map(OrderEntity::getProductId).collect(Collectors.toList());
        // todo 调用产品的 getProductsBatch 方法要求根据 id 的 List 查出对应的所有产品
        return res;
    }

    public void create(OrderEntity order) throws Exception {
//        todo 查询产品是否开始抢购
        ValueOperations<String, Object> ops = redis.opsForValue();
        if (ops.get(order.getUserId()) != null) {
            throw new ForbiddenException("不能重复下单");
        }
        Long decrement = ops.decrement(order.getProductId());
        assert decrement != null;
        if (decrement < 0) {
            ops.increment(order.getProductId());
            throw new ForbiddenException("商品已卖完或者超出抢购期限");
        }
    }
}
