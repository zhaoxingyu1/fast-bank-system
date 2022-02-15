package com.seckill.orderservice.controller;

import com.seckill.common.entity.order.OrderEntity;
import com.seckill.common.feign.FeignConsts;
import com.seckill.common.response.DataFactory;
import com.seckill.common.response.SimpleData;
import com.seckill.orderservice.exception.OrderNotFoundException;
import com.seckill.orderservice.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
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
    public Object getOrderById(HttpServletRequest request, Long id) throws OrderNotFoundException {
        final OrderEntity order = orderService.getById(id);
        if (request.getHeader(FeignConsts.HEADER_NAME) != null) {
            return order;
        }
        return DataFactory.success(SimpleData.class, "ok");
    }
}
