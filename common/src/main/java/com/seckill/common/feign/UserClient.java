package com.seckill.common.feign;

import com.seckill.common.entity.user.UserEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("userservice")
public interface UserClient {

    @GetMapping("/selectUserByUsernameClient")
    UserEntity selectUserByUsernameClient(String name);

}
