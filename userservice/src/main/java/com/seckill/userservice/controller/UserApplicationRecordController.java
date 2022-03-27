package com.seckill.userservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.consts.HeaderConsts;
import com.seckill.common.entity.user.UserApplicationRecordEntity;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.enums.CodeEnum;
import com.seckill.common.jwt.JwtToken;
import com.seckill.common.jwt.TokenUtil;
import com.seckill.common.response.DataFactory;
import com.seckill.common.response.SimpleData;
import com.seckill.userservice.service.UserApplicationRecordService;
import com.seckill.userservice.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author zxy
 * @Classname UserApplicationRecordController
 * @Date 2022/3/25 20:16
 */
@RestController
@RequestMapping("/user")
public class UserApplicationRecordController {

    @Resource
    private UserApplicationRecordService userApplicationRecordService;
    @Resource
    private UserService userService;

    /**
     * 预约贷款产品的时候添加申请记录
     * @param request
     * @param productName
     * @return
     */
    @PostMapping("/applicationRecord/insert")
    public Object insert(HttpServletRequest request,String productName){

        String jwtToken = request.getHeader(HeaderConsts.JWT_TOKEN);
        JwtToken token = TokenUtil.decodeToken(jwtToken);

        UserEntity user = userService.selectUserById(token.getUserId());

        return userApplicationRecordService.insert(user, productName);

    }

    @PostMapping("/applicationRecord/deleteById")
    public Object deleteById(String id){

        Boolean bool = userApplicationRecordService.deleteById(id);

        if(bool){
            return DataFactory.success(SimpleData.class,"ok");
        }else {
            return DataFactory.fail(CodeEnum.INTERNAL_ERROR,"出现未知错误,请联系管理员");
        }

    }

    @PostMapping("/applicationRecord/selectAllOrByLikeNamePage")
    public Object updateById(@RequestParam(required = false) String name, Integer current){

        Page<UserApplicationRecordEntity> entityPage = userApplicationRecordService.selectAllOrByLikeNamePage(name, current);

        return DataFactory.success(SimpleData.class,"ok").parseData(entityPage);

    }

    @PostMapping("/applicationRecord/selectByTime")
    public Object selectByTime(Integer day, Integer current){

        Long time = Long.valueOf((day * 60*60 *24*1000));

        Page<UserApplicationRecordEntity> entityPage = userApplicationRecordService.selectByTime(time, current);

        return DataFactory.success(SimpleData.class,"ok").parseData(entityPage);
    }


}