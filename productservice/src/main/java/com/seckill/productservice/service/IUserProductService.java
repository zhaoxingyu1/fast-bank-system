package com.seckill.productservice.service;

import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.entity.user.UserProductEntity;
import com.seckill.common.exception.NotFoundException;
import com.seckill.productservice.response.AdminGetAppointment;

import java.util.List;

/**
 * @Author: 七画一只妖
 * @Date: 2022/3/12 12:30
 */
public interface IUserProductService {
    // 用户预约产品
    Boolean userAppointProduct(String userId,String type,String productId) throws Exception;

    // 用户取消预约产品
    Boolean userCancelAppointment(String userId,String type,String productId) throws Exception;

    // 用户购买（秒杀）产品
    Boolean userBuyProduct(String userId,String type,String productId) throws Exception;

    // 用户查看已预约的产品（按照时间排序）
    List<Object> userGetAppointment(String userId) throws Exception;

    // 管理员产看某个产品所有预约的人
    List<AdminGetAppointment> adminGetUserByProductId(String productId);

    // 管理员取消某个人的对某个产品的预约
    Boolean adminDeleteAppointByUserId(String productId, String userId) throws Exception;

    // 判断用户是否预约了某个产品
    Boolean userIsAppoint(String userId,String productId);
}
