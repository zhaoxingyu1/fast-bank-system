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
@RequestMapping("/product/userProduct")
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
     * 用户取消预约产品
     * 将user_product表中的status改为0
     */
    @GetMapping("/userCancelAppointment/{type}/{productId}")
    public Object userCancelAppointment(HttpServletRequest request,
                                        @PathVariable("type") String type,
                                        @PathVariable("productId") String productId) throws Exception {
        // 从Token中获取用户id
        String jwtToken = request.getHeader(HeaderConsts.JWT_TOKEN);
        JwtToken token = TokenUtil.decodeToken(jwtToken);
        String userId = token.getUserId();
//        String userId = "test_user_id";

        if (request.getHeader(FeignConsts.HEADER_NAME) != null){
            return userProductService.userCancelAppointment(userId, type, productId);
        } else {
            if(userProductService.userCancelAppointment(userId, type, productId)){
                return DataFactory.success(SimpleData.class,"ok");
            }else {
                return DataFactory.fail(CodeEnum.INTERNAL_ERROR,"出现了未知错误");
            }
        }
    }


    /**
     * 贷款产品库存减1
     */
    @GetMapping("/userProductStock/{productId}")
    public Object loanProductStockReduce(HttpServletRequest request,
                                         @PathVariable("productId")String productId) throws Exception {
        if (request.getHeader(FeignConsts.HEADER_NAME) != null){
            return userProductService.reduceProductStock(productId);
        }else{
            if(userProductService.reduceProductStock(productId)){
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
    public Object userGetAppointment(HttpServletRequest request) throws Exception {
        // 从Token中获取用户id
        String jwtToken = request.getHeader(HeaderConsts.JWT_TOKEN);
        JwtToken token = TokenUtil.decodeToken(jwtToken);
        String userId = token.getUserId();
//        String userId = "test_user_id";
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


    //传一个用户id和一个产品id，返回一个Boolean，判断用户是否预约这个产品
    @GetMapping("/isReserved/{userId}/{productId}")
    public Object isReserved(@PathVariable("userId") String userId,
                             @PathVariable("productId") String productId,
                             HttpServletRequest request) throws Exception{
        if (request.getHeader(FeignConsts.HEADER_NAME) != null){
            return userProductService.userIsAppoint(userId, productId);
        }else {
            return DataFactory.success(SimpleData.class, "ok").parseData(userProductService.userIsAppoint(userId, productId));
        }
    }



}
