package com.seckill.common.feign;

import com.seckill.common.enums.OrderStateEnum;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author : 陈征
 * @date : 2022-02-15 18:51
 */


@FeignClient("orderservice")
public interface OrderClient {
    @GetMapping("/order/getById")
    Object getById(String id);

    @PostMapping("/order/updateState")
    Object updateState(@PathVariable String id, @PathVariable String state);
}
