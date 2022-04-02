package com.seckill.productservice.response;

import com.seckill.common.entity.user.UserProductEntity;
import lombok.Data;

/**
 * @Author: 七画一只妖
 * @Date: 2022-04-02 19:09
 */
@Data
public class UserGetAppointmentProduct<T> {
    // user_product 和 产品类型
    private UserProductEntity userProductEntity;

    // 对应的产品实体
    private T productEntity;
}
