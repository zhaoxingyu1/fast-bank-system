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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    /**
     * 用户预约产品
     * @param request 请求
     * @param type 类型
     * @param productId 产品id
     * @return 用户预约的产品
     * @throws Exception 异常
     */
    @GetMapping("/userAppointment/{type}/{productId}")
    public Object userAppointProduct(HttpServletRequest request,
                                     @PathVariable("type") String type,
                                     @PathVariable("productId") String productId) throws Exception {
        // 从Token中获取用户id
        String jwtToken = request.getHeader(HeaderConsts.JWT_TOKEN);
        JwtToken token = TokenUtil.decodeToken(jwtToken);
        String userId = token.getUserId();
//        String userId = "test_user_id";

        if (request.getHeader(FeignConsts.HEADER_NAME) != null){
            return userProductService.userAppointProduct(userId, type, productId);
        } else {
            if(userProductService.userAppointProduct(userId, type, productId)){
                return DataFactory.success(SimpleData.class,"ok");
            }else {
                return DataFactory.fail(CodeEnum.INTERNAL_ERROR,"出现了未知错误");
            }
        }
    }

    /**
     * 用户购买（秒杀）产品（这个貌似用不到，用户购买商品缓存-1是在订单模块完成，我先不删）
     * @param request 请求
     * @param type 类型
     * @param productId 产品id
     * @return 用户购买（秒杀）产品
     * @throws Exception 异常
     */
    @GetMapping("/userBuyProduct/{type}/{productId}")
    public Object userBuyProduct(HttpServletRequest request,
                                 @PathVariable("type") String type,
                                 @PathVariable("productId") String productId) throws Exception {
        // 从Token中获取用户id
//        String jwtToken = request.getHeader(HeaderConsts.JWT_TOKEN);
//        JwtToken token = TokenUtil.decodeToken(jwtToken);
//        String userId = token.getUserId();
        String userId = "test_user_id";
        if (request.getHeader(FeignConsts.HEADER_NAME) != null){
            return userProductService.userBuyProduct(userId, type, productId);
        } else {
            if(userProductService.userBuyProduct(userId, type, productId)){
                return DataFactory.success(SimpleData.class,"ok");
            }else {
                return DataFactory.fail(CodeEnum.INTERNAL_ERROR,"出现了未知错误");
            }
        }
    }


    /**
     * 用户查看已预约的产品（按照时间排序）
     * @param request   请求
     * @return  用户查看已预约的产品
     */
    @GetMapping("/userGetAppointmentProduct")
    public Object userGetAppointment(HttpServletRequest request){
        // 从Token中获取用户id
//        String jwtToken = request.getHeader(HeaderConsts.JWT_TOKEN);
//        JwtToken token = TokenUtil.decodeToken(jwtToken);
//        String userId = token.getUserId();
        String userId = "test_user_id";
        if (request.getHeader(FeignConsts.HEADER_NAME) != null){
            return userProductService.userGetAppointment(userId);
        } else {
            return DataFactory.success(ListData.class, "ok").parseData(userProductService.userGetAppointment(userId));
        }
    }


    /**
     * 管理员产看某个产品所有预约的人
     * @param request 请求
     * @param productId 产品id
     * @return 管理员产看某个产品所有预约的人
     */
    @GetMapping("/adminGetAppointment/{productId}")
    public Object adminGetUserByProductId(HttpServletRequest request,
                                          @PathVariable("productId") String productId){
        if (request.getHeader(FeignConsts.HEADER_NAME) != null){
            return userProductService.adminGetUserByProductId(productId);
        } else {
            return DataFactory.success(ListData.class, "ok").parseData(userProductService.adminGetUserByProductId(productId));
        }
    }


    /**
     * 管理员取消某个人的对某个产品的预约
     * @param request 请求
     * @param userId 用户ID
     * @param productId 产品ID
     * @return 布尔 是否成功
     * @throws Exception 异常
     */
    @GetMapping("/adminCancelAppointment/{userId}/{productId}")
    public Object adminDeleteAppointByUserId(HttpServletRequest request,
                                             @PathVariable("userId") String userId,
                                             @PathVariable("productId") String productId) throws Exception {
        if (request.getHeader(FeignConsts.HEADER_NAME) != null){
            return userProductService.adminDeleteAppointByUserId(productId, userId);
        } else {
            if(userProductService.adminDeleteAppointByUserId(productId, userId)){
                return DataFactory.success(SimpleData.class,"ok");
            }else {
                return DataFactory.fail(CodeEnum.INTERNAL_ERROR,"出现了未知错误");
            }
        }
    }
}
