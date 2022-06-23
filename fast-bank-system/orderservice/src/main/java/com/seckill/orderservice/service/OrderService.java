package com.seckill.orderservice.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.consts.HeaderConsts;
import com.seckill.common.consts.PageConst;
import com.seckill.common.consts.RedisConsts;
import com.seckill.common.entity.order.OrderEntity;
import com.seckill.common.entity.order.OrderUserEntity;
import com.seckill.common.entity.product.BaseProduct;
import com.seckill.common.entity.user.RiskControlEntity;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.enums.OrderStateEnum;
import com.seckill.common.enums.ProductTypeEnum;
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
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public Page<OrderEntity> getByUserId(String id, String productType, int page) throws Exception {
        if (id == null) {
            throw new NotFoundException("id 为空");
        }
        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<OrderEntity>()
                .eq("user_id", id)
                .orderByDesc("ctime");
        if (productType != null) {
            wrapper = wrapper.eq("product_type", ProductTypeEnum.valueOf(productType));
        }
        return orderDao.selectPage(new Page<>(page, PageConst.PageSize), wrapper);
    }

    public Page<OrderEntity> getAll(int page, String productType) {
        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<OrderEntity>().orderByDesc("ctime");
        if (productType != null) {
            wrapper.eq("product_type", productType);
        }
        return orderDao.selectPage(
                new Page<>(page, PageConst.PageSize),
                wrapper
        );
    }

    public Page<OrderEntity> getByProduct(int page, String id) throws Exception {
        if (id == null) {
            throw new NotFoundException("id 为空");
        }
        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<OrderEntity>()
                .orderByDesc("ctime")
                .eq("product_id", id);

        Page<OrderEntity> page1 = orderDao.selectPage(
                new Page<>(page, PageConst.PageSize),
                wrapper
        );
//        转成带用户名和是否失信的对象
        List<OrderEntity> orders = page1.getRecords();
        for (int i = 0; i < orders.size(); i++) {
            // 风险控制信息
            UserEntity user = userClient.selectUserById(orders.get(i).getUserId());
            if (user == null) {
                continue;
            }
            RiskControlEntity riskControlEntity = userClient.getRiskControl();
            RiskControl riskControl = RiskControlUtils.isQualified(user, riskControlEntity);

            orders.set(i, new OrderUserEntity(
                    orders.get(i),
                    riskControl.getThroughState() == 1,
                    user.getUsername()
            ));
        }
        return page1;
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

//         分布式锁
        String lockKey = "lock_" + order.getProductId() + order.getUserId();
        Boolean absent = ops.setIfAbsent(
                lockKey,
                "KANO",
                RedisConsts.ORDER_WAITING_TIME,
                TimeUnit.MILLISECONDS
        );
        assert absent != null;
        if (!absent) {
            throw new NotFoundException("晚了一步");
        }

        // 订单去重
        OrderEntity entity = orderDao.selectOne(
                new QueryWrapper<OrderEntity>()
                        .eq("user_id", order.getUserId())
                        .eq("product_id", order.getProductId())
        );
        if (entity != null) {
//        解锁
            redis.unlink(lockKey);
            throw new ForbiddenException("不能对同一产品重复下单哦");
        }

        // 减少 redis 库存
        Long decrement = ops.decrement(order.getProductId());
        if (decrement < 0) {
//        解锁
            redis.unlink(lockKey);
            ops.increment(order.getProductId());
            throw new ForbiddenException("商品已卖完或者未在抢购期限内");
        }
//        减少真实库存
        ops.decrement("pre" + order.getProductId());

        order.setState(OrderStateEnum.PENDING.name());
        order.setProductType(ProductTypeEnum.financial.name());
        orderDao.insert(order);
        log.info("created financial order: " + order.getOrderId());
//        往 redis 插入一条 15 分钟后过期的键，等待支付
        ops.set(
                RedisConsts.ORDER_KEY_PREFIX_EXPIRED + order.getOrderId(),
                "寄",
                RedisConsts.ORDER_WAITING_TIME,
                TimeUnit.MILLISECONDS
        );
//        过期键对应的产品id
        ops.set(
                RedisConsts.ORDER_KEY_PREFIX_EXPIRED + order.getOrderId() + "wuhu",
                order.getProductId()
        );
//        解锁
        redis.unlink(lockKey);
        return order.getOrderId();
    }

    public String create(OrderEntity order) throws Exception {
        if (productClient.getProductType(order.getProductId()).equals("financial")) {
            throw new ForbiddenException("傻逼");
        }
        // 风险控制
        UserEntity user = userClient.selectUserById(order.getUserId());
        RiskControlEntity riskControlEntity = userClient.getRiskControl();
        RiskControl riskControl = RiskControlUtils.isQualified(user, riskControlEntity);
        // 记录用户操作
        userClient.insertApplicationRecord(order.getUserId(), order.getProductId(), riskControl);
        if (riskControl.getThroughState() == 0) {
            throw new ForbiddenException(riskControl.getCause());
        }

        order.setState(OrderStateEnum.PENDING.name());
        order.setProductType(ProductTypeEnum.loan.name());
        orderDao.insert(order);
        log.info("created loan order: " + order.getOrderId());
        productClient.loanProductStockReduce(order.getProductId());
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

    public List<BigDecimal> oneYear() {
        List<BigDecimal> res = new ArrayList<>();
        Date now = new Date();
        now.setDate(1);
        now.setHours(0);
        now.setMinutes(0);
        now.setSeconds(0);
        long thisMonthStart = now.getTime();
        for (int i = 11; i > 0; i--) {
            // 按 1 个月 30 天来算
            long start = thisMonthStart - (30L * 24 * 60 * 60 * 1000) * i;
            long end = thisMonthStart - (30L * 24 * 60 * 60 * 1000) * (i - 1);
            QueryWrapper<OrderEntity> wrapper = new QueryWrapper<>();
            wrapper.ge("ctime", start).le("ctime", end);
            List<String> ids = orderDao.selectList(wrapper).stream().map(OrderEntity::getProductId).collect(Collectors.toList());
            Optional<BigDecimal> opt = productClient.getProductsBatch(ids).stream()
                    .map(BaseProduct::getPrice)
                    .reduce((a, b) -> a = a.add(b));
            res.add(opt.orElseGet(() -> new BigDecimal(0)));
        }
        res.add(thisMonthSell());
        return res;
    }

    public long thisMonthOrder() {
        Date now = new Date();
        now.setDate(1);
        now.setHours(0);
        now.setMinutes(0);
        now.setSeconds(0);
        long time = now.getTime();
        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<>();
        wrapper.ge("ctime", time);
        return orderDao.selectCount(wrapper);
    }

    public BigDecimal thisMonthSell() {
        Date now = new Date();
        now.setDate(1);
        now.setHours(0);
        now.setMinutes(0);
        now.setSeconds(0);
        long time = now.getTime();
        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<>();
        wrapper.ge("ctime", time);
        List<String> ids = orderDao.selectList(wrapper).stream().map(OrderEntity::getProductId).collect(Collectors.toList());
        Optional<BigDecimal> reduce = productClient.getProductsBatch(ids).stream().filter(Objects::nonNull)
                .map(BaseProduct::getPrice).reduce((a, b) -> a = a.add(b));
        return reduce.orElseGet(() -> new BigDecimal(0));
    }
}
