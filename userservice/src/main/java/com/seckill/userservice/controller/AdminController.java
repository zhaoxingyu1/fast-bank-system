package com.seckill.userservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.entity.user.RoleEntity;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.enums.CodeEnum;
import com.seckill.common.response.ComplexData;
import com.seckill.common.response.DataFactory;
import com.seckill.common.response.SimpleData;
import com.seckill.userservice.jwt.JwtTokenManager;
import com.seckill.userservice.service.RoleService;
import com.seckill.userservice.service.UserInfoService;
import com.seckill.userservice.service.UserService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zxy
 * @Classname AdminController
 * @Date 2022/3/5 9:32
 */
@RestController
@RequestMapping("admin")
public class AdminController {

    @Resource
    private UserInfoService userInfoService;
    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;
    @Resource(name = "jwtTokenManager")
    private JwtTokenManager jwtTokenManager;


    /**
     * 分页查询全部用户
     * =========================================待修改=========================================================
     *
     * @param request
     * @return
     */
    @GetMapping("/selectAllUser")
    @RequiresRoles(value = "admin")
    public Object selectAllUser(HttpServletRequest request, Integer current, Integer size) {

        Page<UserEntity> userEntityPage = userService.selectAllUser(current, size);

        //总条数
        long total = userEntityPage.getTotal();
        //总页数
        long pages = userEntityPage.getPages();
        // 分页查询的用户
        List<UserEntity> userEntityList = userEntityPage.getRecords();

        return DataFactory.success(ComplexData.class, "ok").parseData("total",total).parseData("pages",pages).parseData(userEntityList);

    }

    @GetMapping("/selectUserListByName")
    public Object selectUserListByName(HttpServletRequest request, String name, Integer current, Integer size) {
        String header = request.getHeader("占位");

        Page<UserEntity> userEntityPage = userService.selectUserListByName(name, current, size);

        //总条数
        long total = userEntityPage.getTotal();
        //总页数
        long pages = userEntityPage.getPages();
        // 分页查询的用户
        List<UserEntity> userEntityList = userEntityPage.getRecords();

        return DataFactory.success(ComplexData.class, "ok").parseData("total",total).parseData("pages",pages).parseData(userEntityList);
    }

    /**
     * 分割 role表
     * ====================================未搞完===================================================
     */

    @PostMapping("/updateUserRole")
    @RequiresRoles(value = "admin")
    public Object updateUserRole(HttpServletRequest request, String userId, String role) {


        UserEntity userEntity = userService.selectUserById(userId);
        RoleEntity roleEntity = new RoleEntity();

        String roleId = userEntity.getRoleId();
        roleEntity.setRoleId(roleId);
        roleEntity.setRole(role);

        Boolean bool = roleService.updateUserRoleById(roleEntity);

        if(!bool){
            return DataFactory.fail(CodeEnum.INTERNAL_ERROR,"修改失败，未知错误");
        }

        return DataFactory.success(SimpleData.class,"ok");
    }

}

