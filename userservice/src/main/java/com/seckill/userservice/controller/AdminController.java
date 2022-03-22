package com.seckill.userservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.entity.user.RoleEntity;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.entity.user.UserInfoEntity;
import com.seckill.common.enums.CodeEnum;
import com.seckill.common.response.ComplexData;
import com.seckill.common.response.DataFactory;
import com.seckill.common.response.SimpleData;

import com.seckill.userservice.service.RoleService;
import com.seckill.userservice.service.UserInfoService;
import com.seckill.userservice.service.UserService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
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

    /**
     * 分页查询全部用户
     * =========================================待修改=========================================================
     *
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


}

