package com.seckill.userservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.consts.FeignConsts;
import com.seckill.common.entity.user.RiskControlEntity;
import com.seckill.common.entity.user.RoleEntity;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.entity.user.UserInfoEntity;
import com.seckill.common.enums.CodeEnum;
import com.seckill.common.response.ComplexData;
import com.seckill.common.response.DataFactory;
import com.seckill.common.response.SimpleData;

import com.seckill.userservice.service.RiskControlService;
import com.seckill.userservice.service.RoleService;
import com.seckill.userservice.service.UserInfoService;
import com.seckill.userservice.service.UserService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zxy
 * @Classname AdminController
 * @Date 2022/3/5 9:32
 */
@RestController
@RequestMapping("/user")
public class AdminController {

    @Resource
    private UserInfoService userInfoService;
    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;
    @Resource
    private RiskControlService riskControlService;
    @Resource
    private RedisTemplate<String, Object> redis;

    /**
     * 分页查询全部用户
     * @param request
     * @return
     */
    @GetMapping("/admin/selectAllUserOrByLikeName")
    public Object selectAllUser(HttpServletRequest request,@RequestParam(required = false)String name, Integer current) {
        Page<UserEntity> userEntityPage=null;
        if(name==null){
            userEntityPage = userService.selectAllUser(current);
        }else{
            userEntityPage = userService.selectUserListByName(name, current);
        }


        return DataFactory.success(SimpleData.class, "ok").parseData(userEntityPage);

    }




    @GetMapping("/admin/selectUserInfoByCreditStatus")
    public Object selectUserInfoByCreditStatus(Integer creditStatus,Integer current){

        Page<UserInfoEntity> userInfoEntityPage = userInfoService.selectUserInfoByCreditStatus(creditStatus, current);

        return DataFactory.success(SimpleData.class,"ok").parseData(userInfoEntityPage);

    }

    @PostMapping("/admin/updateUserCreditStatus")
    public Object updateUserCreditStatus(String userInfoId,Integer creditStatus){

        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setUserInfoId(userInfoId);
        userInfoEntity.setCreditStatus(creditStatus);

        Boolean bool = userInfoService.updateUserInfo(userInfoEntity);

        if(!bool){
            return DataFactory.fail(CodeEnum.INTERNAL_ERROR,"修改失败");
        }
        return DataFactory.success(SimpleData.class,"ok");

    }


    @PostMapping("/admin/selectAllByInfo")
    public Object selectAllByInfo(@RequestParam(required = false)String info,Integer current){

        Page<UserInfoEntity> userInfoEntityPage = userInfoService.selectAllByInfo(info, current);

        return DataFactory.success(SimpleData.class,"ok").parseData(userInfoEntityPage);
    }

    @PostMapping("/admin/insertOrUpdate")
    public Object insertOrUpdate(RiskControlEntity riskControl){

        Boolean bool = riskControlService.insertOrUpdate(riskControl);

        if (bool){
            return DataFactory.success(SimpleData.class,"ok");
        }else{
            return DataFactory.fail(CodeEnum.INTERNAL_ERROR,"修改或插入规则失败");
        }

    }

    @PostMapping("/admin/getRiskControl")
    public Object getRiskControl(HttpServletRequest request){


        RiskControlEntity riskControl = riskControlService.getRiskControl();

        if(request.getHeader(FeignConsts.HEADER_NAME)==null){
            return DataFactory.success(SimpleData.class,"ok").parseData(riskControl);
        }else{
            return riskControl;
        }

    }

    @PostMapping("/admin/openScriptControl")
    public Object openScriptControl(){

        redis.opsForValue().set("switch","true");
        return DataFactory.success(SimpleData.class,"ok");
    }

    @PostMapping("/admin/closeScriptControl")
    public Object closeScriptControl(){

        redis.opsForValue().set("switch","false");
        return DataFactory.success(SimpleData.class,"ok");
    }

    @PostMapping("/admin/userCount")
    public Object userCountByTime(Integer day){

        Integer count = userService.selectUserCountByTime(day);

        return DataFactory.success(SimpleData.class,"ok").parseData(count);
    }

    @PostMapping("/admin/getUserFlow")
    public Object getUserFlow(){

        Integer userFlow = (Integer)redis.opsForValue().get("userFlow");
        return DataFactory.success(SimpleData.class,"ok").parseData(userFlow);
    }

}

