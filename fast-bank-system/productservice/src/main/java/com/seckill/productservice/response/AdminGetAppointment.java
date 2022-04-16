package com.seckill.productservice.response;

import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.entity.user.UserProductEntity;
import lombok.Data;

/**
 * @Author: 七画一只妖
 * @Date: 2022-04-02 19:32
 */
@Data
public class AdminGetAppointment {
    // user_product 和 用户信息
    private UserProductEntity userProductEntity;

    // 对应的用户实体
    private UserEntity userEntity;
}
