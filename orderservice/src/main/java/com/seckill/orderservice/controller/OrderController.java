package com.seckill.orderservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.entity.order.OrderEntity;
import com.seckill.common.consts.FeignConsts;
import com.seckill.common.response.BaseData;
import com.seckill.common.response.DataFactory;
import com.seckill.common.response.SimpleData;
import com.seckill.orderservice.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author : 陈征
 * @date : 2022-02-14 19:13
 */

@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;

    @GetMapping("/getById")
    public Object getById(HttpServletRequest request, String id) throws Exception {
        final OrderEntity order = orderService.getById(id);
        if (request.getHeader(FeignConsts.HEADER_NAME) != null) {
            return order;
        }
        return DataFactory.success(SimpleData.class, "ok");
    }

    @GetMapping("/getByUser")
    public Object getByUser(HttpServletRequest request, String id, int page) throws Exception {
        Page<OrderEntity> entities = orderService.getByUserId(id, page);
        if (request.getHeader(FeignConsts.HEADER_NAME) != null) {
            return entities;
        }
        return DataFactory.success(SimpleData.class, "ok").parseData(entities);
    }

    @PostMapping("/create")
    public BaseData create(OrderEntity order) throws Exception {
        orderService.create(order);
        return DataFactory.success(SimpleData.class, "ok");
    }
}
