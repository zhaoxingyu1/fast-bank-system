package com.seckill.productservice.controller;

import com.seckill.common.consts.FeignConsts;
import com.seckill.common.consts.HeaderConsts;
import com.seckill.common.enums.CodeEnum;
import com.seckill.common.jwt.JwtToken;
import com.seckill.common.jwt.TokenUtil;
import com.seckill.common.response.DataFactory;
import com.seckill.common.response.ListData;
import com.seckill.common.response.SimpleData;
import com.seckill.productservice.service.IUserProductService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: 七画一只妖
 * @Date: 2022/3/12 13:31
 */
@RestController
@RequestMapping("/userProduct")
public class UserProductController {
    @Resource
    private IUserProductService userProductService;


    // todo 用户预约产品
    public Object userAppointProduct(HttpServletRequest request) throws Exception {
        // 从Token中获取用户id
        String jwtToken = request.getHeader(HeaderConsts.JWT_TOKEN);
        JwtToken token = TokenUtil.decodeToken(jwtToken);
        String userId = token.getUserId();

        if (request.getHeader(FeignConsts.HEADER_NAME) != null){
            return userProductService.userAppointProduct(userId, null, null);
        } else {
            if(userProductService.userAppointProduct(userId, null, null)){
                return DataFactory.success(SimpleData.class,"ok");
            }else {
                return DataFactory.fail(CodeEnum.INTERNAL_ERROR,"出现了未知错误");
            }
        }
    }

    // todo 用户购买（秒杀）产品
    public Object userBuyProduct(HttpServletRequest request) throws Exception {
        // 从Token中获取用户id
        String jwtToken = request.getHeader(HeaderConsts.JWT_TOKEN);
        JwtToken token = TokenUtil.decodeToken(jwtToken);
        String userId = token.getUserId();
        if (request.getHeader(FeignConsts.HEADER_NAME) != null){
            return userProductService.userBuyProduct(userId, null, null);
        } else {
            if(userProductService.userBuyProduct(userId, null, null)){
                return DataFactory.success(SimpleData.class,"ok");
            }else {
                return DataFactory.fail(CodeEnum.INTERNAL_ERROR,"出现了未知错误");
            }
        }
    }

    // todo 用户查看已预约的产品（按照时间排序）
    public Object userGetAppointment(HttpServletRequest request){
        // 从Token中获取用户id
        String jwtToken = request.getHeader(HeaderConsts.JWT_TOKEN);
        JwtToken token = TokenUtil.decodeToken(jwtToken);
        String userId = token.getUserId();
        if (request.getHeader(FeignConsts.HEADER_NAME) != null){
            return userProductService.userGetAppointment(userId);
        } else {
            return DataFactory.success(ListData.class, "ok").parseData(userProductService.userGetAppointment(null));
        }
    }

    // todo 管理员产看某个产品所有预约的人
    public Object adminGetUserByProductId(HttpServletRequest request){
        if (request.getHeader(FeignConsts.HEADER_NAME) != null){
            return userProductService.adminGetUserByProductId(null);
        } else {
            return DataFactory.success(ListData.class, "ok").parseData(userProductService.adminGetUserByProductId(null));
        }
    }

    // todo 管理员取消某个人的对某个产品的预约
    public Object adminDeleteAppointByUserId(HttpServletRequest request) throws Exception {
        if (request.getHeader(FeignConsts.HEADER_NAME) != null){
            return userProductService.adminDeleteAppointByUserId(null, null);
        } else {
            if(userProductService.adminDeleteAppointByUserId(null, null)){
                return DataFactory.success(SimpleData.class,"ok");
            }else {
                return DataFactory.fail(CodeEnum.INTERNAL_ERROR,"出现了未知错误");
            }
        }
    }
}
