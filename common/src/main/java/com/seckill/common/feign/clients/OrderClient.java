package com.seckill.common.feign.clients;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author : 陈征
 * @date : 2022-02-15 18:51
 */


@FeignClient("orderservice")
public interface OrderClient {
}
