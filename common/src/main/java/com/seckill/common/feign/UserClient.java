package com.seckill.common.feign;

import com.seckill.common.entity.user.RiskControlEntity;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.response.BaseData;
import com.seckill.common.response.DataFactory;
import com.seckill.common.response.SimpleData;
import com.seckill.common.utils.RiskControl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@FeignClient("userservice")
public interface UserClient {

    @GetMapping("user/selectUserById")
    UserEntity selectUserById(@RequestParam(required = false)String userId);


    @PostMapping("user/applicationRecord/insert")
    Boolean insertApplicationRecord(@RequestParam String userId,@RequestParam String productId,@RequestBody RiskControl riskControl);

    @PostMapping("/user/admin/getRiskControl")
    RiskControlEntity getRiskControl();

    @PostMapping("/user/payment")
    Boolean Payment(@RequestParam String userInfoId,@RequestParam BigDecimal money);
}
