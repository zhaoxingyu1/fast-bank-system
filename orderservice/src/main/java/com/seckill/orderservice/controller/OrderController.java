package com.seckill.orderservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.consts.HeaderConsts;
import com.seckill.common.entity.order.OrderEntity;
import com.seckill.common.consts.FeignConsts;
import com.seckill.common.enums.OrderStateEnum;
import com.seckill.common.jwt.JwtToken;
import com.seckill.common.jwt.TokenUtil;
import com.seckill.common.response.*;
import com.seckill.orderservice.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author : 陈征
 * @date : 2022-02-14 19:13
 */

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {
    @Resource
    private OrderService orderService;

    @GetMapping("/getById")
    public Object getById(HttpServletRequest request, String id) throws Exception {
        final OrderEntity order = orderService.getById(request, id);

        if (request.getHeader(FeignConsts.HEADER_NAME) != null) {
            return order;
        }
        return DataFactory.success(SimpleData.class, "ok").parseData(order);
    }

    @GetMapping("/getByUser")
    public BaseData getByUser(HttpServletRequest request, Integer page, String userId, String productType) throws Exception {
        if (page == null) {
            page = 1;
        }

        JwtToken token = TokenUtil.decodeToken(request.getHeader(HeaderConsts.JWT_TOKEN));
        String id = token.getUserId();
        if (token.getRole().equals("admin")) {
            id = userId;
        }

        Page<OrderEntity> entities = orderService.getByUserId(id, productType, page);
        return DataFactory.success(SimpleData.class, "ok").parseData(entities);
    }

    @GetMapping("/getAll")
    public BaseData getAll(Integer page, String productType) {
        if (page == null) {
            page = 1;
        }
        Page<OrderEntity> all = orderService.getAll(page, productType);
        return DataFactory.success(SimpleData.class, "ok").parseData(all);
    }

    @GetMapping("/getByProduct")
    public BaseData getByProduct(String id, Integer page) throws Exception {
        if (page == null) {
            page = 1;
        }
        Page<OrderEntity> byProduct = orderService.getByProduct(page, id);
        return DataFactory.success(SimpleData.class, "ok").parseData(byProduct);
    }

    @PostMapping("/seckill")
    public BaseData seckill(HttpServletRequest request, OrderEntity order) throws Exception {
        order.setUserId(TokenUtil.decodeToken(request.getHeader(HeaderConsts.JWT_TOKEN)).getUserId());

        String id = orderService.seckill(order);
        return DataFactory.success(SimpleData.class, "ok").parseData(id);
    }

    @PostMapping("/create")
    public BaseData create(HttpServletRequest request, OrderEntity order) throws Exception {
        order.setUserId(TokenUtil.decodeToken(request.getHeader(HeaderConsts.JWT_TOKEN)).getUserId());

        String id = orderService.create(order);
        return DataFactory.success(SimpleData.class, "ok").parseData(id);
    }

    @PostMapping("/updateState")
    public Object updateState(HttpServletRequest request, @RequestParam String id,@RequestParam String state) {
        orderService.updateState(id, OrderStateEnum.valueOf(state));
        if (request.getHeader(FeignConsts.HEADER_NAME) != null) {
            log.info("------------ feign 调用!");
            return null;
        }
        return DataFactory.success(SimpleData.class, "ok");
    }

    @GetMapping("/oneYear")
    public BaseData oneYear() {
        return DataFactory.success(ListData.class, "ok").parseData(orderService.oneYear());
    }

    @GetMapping("/thisMonthOrder")
    public BaseData thisMonthOrder() {
        return DataFactory.success(SimpleData.class, "ok").parseData(orderService.thisMonthOrder());
    }

    @GetMapping("/thisMonthSell")
    public BaseData thisMonthSell() {
        return DataFactory.success(SimpleData.class, "ok").parseData(orderService.thisMonthSell());
    }
}
