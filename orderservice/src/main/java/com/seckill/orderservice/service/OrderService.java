package com.seckill.orderservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.consts.HeaderConsts;
import com.seckill.common.consts.PageConst;
import com.seckill.common.consts.RedisConsts;
import com.seckill.common.entity.order.OrderEntity;
import com.seckill.common.entity.product.BaseProduct;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.enums.OrderStateEnum;
import com.seckill.common.exception.ForbiddenException;
import com.seckill.common.exception.NotFoundException;
import com.seckill.common.feign.ProductClient;
import com.seckill.common.feign.UserClient;
import com.seckill.common.jwt.TokenUtil;
import com.seckill.common.utils.RiskControl;
import com.seckill.common.utils.RiskControlUtils;
import com.seckill.orderservice.dao.OrderDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author : 陈征
 * @date : 2022-02-14 21:10
 */

@Service
@Transactional
@Slf4j
public class OrderService {
    @Resource
    private RedisTemplate<String, Object> redis;

    @Resource
    private RabbitTemplate rabbit;

    @Resource
    private OrderDao orderDao;

    @Resource
    private ProductClient productClient;

    @Resource
    private UserClient userClient;

    public OrderEntity getById(HttpServletRequest request, String id) throws Exception {
        if (id == null) {
            throw new NotFoundException("id 为空");
        }
        OrderEntity orderEntity = orderDao.selectById(id);
        if (orderEntity == null) {
            throw new NotFoundException("未找到此订单");
        }
        if (!orderEntity.getUserId().equals(TokenUtil.decodeToken(request.getHeader(HeaderConsts.JWT_TOKEN)).getUserId())) {
            throw new ForbiddenException("你就是歌姬吧");
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
        return orderDao.selectPage(new Page<>(page, PageConst.PageSize), wrapper);
    }

    public Page<OrderEntity> getAll(int page) {
        return orderDao.selectPage(
                new Page<>(page, PageConst.PageSize),
                new QueryWrapper<>()
        );
    }

    public String seckill(OrderEntity order) throws Exception {
        if (productClient.getProductType(order.getProductId()).equals("loan")) {
            throw new ForbiddenException("傻逼");
        }
        // 判断用户是否已预约
        Boolean reserved = productClient.isReserved(order.getUserId(), order.getProductId());
        if (!reserved) {
            throw new ForbiddenException("还没有预约哦");
        }

        ValueOperations<String, Object> ops = redis.opsForValue();

        OrderEntity entity = orderDao.selectOne(
                new QueryWrapper<OrderEntity>()
                        .eq("user_id", order.getUserId())
                        .eq("product_id", order.getProductId())
        );
        if (entity != null) {
            throw new ForbiddenException("不能对同一产品重复下单哦");
        }
//         分布式锁
        Boolean absent = ops.setIfAbsent(
                "lock_" + order.getProductId(),
                "KANO",
                RedisConsts.ORDER_WAITING_TIME,
                TimeUnit.MILLISECONDS
        );

        Long decrement = ops.decrement(order.getProductId());
        assert decrement != null;
        if (decrement < 0) {
            ops.increment(order.getProductId());
            throw new ForbiddenException("商品已卖完或者未在抢购期限内");
        }
        assert absent != null;
        if (!absent) {
            throw new NotFoundException("晚了一步");
        }

        order.setState(OrderStateEnum.PENDING.name());
        orderDao.insert(order);

        log.info("created financial order: " + order.getOrderId());
        ops.set(
                RedisConsts.ORDER_KEY_PREFIX_EXPIRED + order.getOrderId(),
                "寄",
                RedisConsts.ORDER_WAITING_TIME,
                TimeUnit.MILLISECONDS
        );
//        解锁
        redis.unlink(order.getProductId());
        return order.getOrderId();
    }

    public String create(OrderEntity order) throws Exception {
        if (productClient.getProductType(order.getProductId()).equals("financial")) {
            throw new ForbiddenException("傻逼");
        }
        // 风险控制
        UserEntity user = userClient.selectUserById(order.getUserId());
        RiskControl riskControl = RiskControlUtils.isQualified(user);
        if (riskControl.getThroughState() == 0) {
            throw new ForbiddenException(riskControl.getCause());
        }

        order.setState(OrderStateEnum.PENDING.name());
        orderDao.insert(order);
        log.info("created loan order: " + order.getOrderId());
        return order.getOrderId();
    }

    public void updateState(String id, OrderStateEnum orderState) {
        log.info("update state: " + id);
        redis.unlink(RedisConsts.ORDER_KEY_PREFIX_EXPIRED + id);
        orderDao.update(
                null,
                new UpdateWrapper<OrderEntity>()
                        .set("state", orderState.name())
                        .eq("order_id", id)
        );
    }
}
