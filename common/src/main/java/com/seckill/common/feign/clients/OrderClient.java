package com.seckill.common.feign.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author : 陈征
 * @date : 2022-02-15 18:51
 */


@FeignClient("orderservice")
@RequestMapping("/order")
public interface OrderClient {
    @GetMapping("/getById")
    public Object getById(Long id);
}
