package com.seckill.common.feign;

import com.seckill.common.entity.user.UserEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@FeignClient("userservice")
public interface UserClient {

    @GetMapping("user/selectUserById")
    Object selectUserById(@RequestParam(required = false)String userId);

}
