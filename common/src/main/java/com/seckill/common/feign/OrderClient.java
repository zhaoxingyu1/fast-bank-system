package com.seckill.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author : 陈征
 * @date : 2022-02-15 18:51
 */


@FeignClient("orderservice")
public interface OrderClient {
    @GetMapping("/order/getById")
    Object getById(Long id);
}
