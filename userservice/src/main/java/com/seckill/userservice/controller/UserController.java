package com.seckill.userservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.entity.user.RoleEntity;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.entity.user.UserInfoEntity;
import com.seckill.userservice.dao.UserDao;
import com.seckill.userservice.service.RoleService;
import com.seckill.userservice.service.UserInfoService;
import com.seckill.userservice.service.UserService;
import org.apache.catalina.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zxy
 * @Classname UserController
 * @Date 2022/2/11 14:41
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserInfoService userInfoService;
    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;


    @PostMapping("/createUser")
    public Object createUser(HttpServletRequest request,UserEntity user, UserInfoEntity userInfo, RoleEntity role){

        String header = request.getHeader("占位");

        Boolean bool = userService.insertUser(user, userInfo, role);

        if(header.equals("feign")){
            //feign调用

        }else{
            //前端调用

        }
        return null;
    }

    @GetMapping("/deleteUserById")
    public Object deleteUserById(HttpServletRequest request,String userId){

        String header = request.getHeader("占位");

        Boolean bool = userService.deleteUserById(userId);

        if(header.equals("feign")){
            //feign调用

        }else{
            //前端调用

        }
        return null;
    }

    @PostMapping("/updateUserById")
    public Object updateUserById(HttpServletRequest request,UserEntity user){

        String header = request.getHeader("占位");

        Boolean bool = userService.updateUserById(user);

        if(header.equals("feign")){
            //feign调用

        }else{
            //前端调用

        }
        return null;
    }

    /**
     * 根据id查询用户
     * @param request
     * @param userId
     * @return
     */
    @GetMapping("/selectUserById")
    public Object selectUserById(HttpServletRequest request,String userId){

        String header = request.getHeader("占位");

        UserEntity userEntity = userService.selectUserById(userId);

        if(header.equals("feign")){
            //feign调用

        }else{
            //前端调用

        }
        return null;
    }

    /**
     * 分页查询全部用户
     * @param request
     * @return
     */
    @GetMapping("/selectAllUser")
    public Object selectAllUser(HttpServletRequest request,Integer current, Integer size){
        String header = request.getHeader("占位");

        Page<UserEntity> userEntityPage = userService.selectAllUser(current, size);

        //总条数
        long total = userEntityPage.getTotal();
        //总页数
        long pages = userEntityPage.getPages();
        // 分页查询的用户
        List<UserEntity> userEntityList = userEntityPage.getRecords();


        if(header.equals("feign")){
            //feign调用

        }else{
            //前端调用

        }
        return null;
    }

    @GetMapping("/selectUserListByName")
    public Object selectUserListByName(HttpServletRequest request,String name,Integer current, Integer size){
        String header = request.getHeader("占位");

        Page<UserEntity> userEntityPage = userService.selectUserListByName(name,current, size);

        //总条数
        long total = userEntityPage.getTotal();
        //总页数
        long pages = userEntityPage.getPages();
        // 分页查询的用户
        List<UserEntity> userEntityList = userEntityPage.getRecords();


        if(header.equals("feign")){
            //feign调用

        }else{
            //前端调用

        }
        return null;
    }

    /**
     *分割 role表
     * =======================================================================================
     */

    @PostMapping("/updateUserRole")
    public Object updateUserRole(HttpServletRequest request,String userId,String role){

        String header = request.getHeader("占位");
        UserEntity userEntity = userService.selectUserById(userId);
        RoleEntity roleEntity = new RoleEntity();

        String roleId = userEntity.getRoleId();
        roleEntity.setRoleId(roleId);
        roleEntity.setRole(role);

        Boolean bool = roleService.updateUserRoleById(roleEntity);
        if(header.equals("feign")){
            //feign调用

        }else{
            //前端调用

        }
        return null;

    }


    /*
        info表
        ===========================================================
     */

    /**
     * 修改用户信息
     * @param request
     * @param userId
     * @param userInfo
     * @return
     */
   @PostMapping("/updateUserInfo")
   public Object updateUserInfo(HttpServletRequest request,String userId,UserInfoEntity userInfo){
       String header = request.getHeader("占位");
       UserEntity userEntity = userService.selectUserById(userId);

       userInfo.setUserInfoId(userEntity.getUserInfoId());
       Boolean bool = userInfoService.updateUserInfo(userInfo);


       if(header.equals("feign")){
           //feign调用

       }else{
           //前端调用

       }
       return null;
   }

    /*
        用户产品表
      ==============================================
     */
//    @PostMapping("/")
//    public Object insertUserProduct(HttpServletRequest request,String userId,)

}
