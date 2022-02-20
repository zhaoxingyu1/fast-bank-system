package com.seckill.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("userservice")
@RequestMapping("/user")
public interface UserClient {
}
