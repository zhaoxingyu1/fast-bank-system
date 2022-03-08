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
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @PostMapping("/login")
    public Object userLogin(@RequestParam("username") String username, @RequestParam("password") String password) {

        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);

        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(usernamePasswordToken);
        }catch (UnknownAccountException e){
            //用户名错误
        }catch (IncorrectCredentialsException e){
            // 密码错误

        }
        // 登录成功 将信息存进session
        UserEntity user = userService.selectUserByUsername(username);
        // 缓存用户信息
        Session session = subject.getSession(true);
        session.setAttribute("user", user);
        // 缓存角色

        String role = user.getUserRole().getRole();
        Set<String> roleList = new HashSet<>();
        roleList.add(role);

        session.setAttribute("role", roleList);

//        // 缓存权限
//        Set<String> permissionList = userService.findPermission(username);
//        session.setAttribute("permission", permissionList);


        return "登录成功";
    }


    // 注销
    @GetMapping("/logout")
//    @RequiresPermissions("admin")
    public String userLogout(){

        System.out.println(123);
        Subject subject = SecurityUtils.getSubject();
        boolean bool = subject.hasRole("admin");

        if(!bool){
            System.out.println("hhh");
        }

        System.err.println(subject.getSession().getAttribute("role"));
        subject.logout();
        return "登出成功";
    }

    /**
     * 注册
     * @param request
     * @param user
     * @param userInfo
     * @param role
     * @return
     * @throws Exception
     */
    @PostMapping("/createUser")
    public Object createUser(HttpServletRequest request, UserEntity user, UserInfoEntity userInfo, RoleEntity role) throws Exception {

        String header = request.getHeader("占位");

        Boolean bool = userService.insertUser(user, userInfo, role);

        if (header.equals("feign")) {
            //feign调用

        } else {
            //前端调用

        }
        return null;
    }

    @GetMapping("/deleteUserById")
    public Object deleteUserById(HttpServletRequest request, String userId) throws Exception {

        String header = request.getHeader("占位");

        Boolean bool = userService.deleteUserById(userId);

        if (header.equals("feign")) {
            //feign调用

        } else {
            //前端调用

        }
        return null;
    }

    @PostMapping("/updateUserById")
    public Object updateUserById(HttpServletRequest request, UserEntity user) {

        String header = request.getHeader("占位");

        Boolean bool = userService.updateUserById(user);

        if (header.equals("feign")) {
            //feign调用

        } else {
            //前端调用

        }
        return null;
    }

    /**
     * 根据id查询用户
     *
     * @param request
     * @param userId
     * @return
     */
    @GetMapping("/selectUserById")
    public Object selectUserById(HttpServletRequest request, String userId) {

        String header = request.getHeader("占位");

        UserEntity userEntity = userService.selectUserById(userId);

        if (header.equals("feign")) {
            //feign调用

        } else {
            //前端调用

        }
        return null;
    }

    /**
     * 分页查询全部用户
     *
     * @param request
     * @return
     */
    @GetMapping("/selectAllUser")
    public Object selectAllUser(HttpServletRequest request, Integer current, Integer size) {
        String header = request.getHeader("占位");

        Page<UserEntity> userEntityPage = userService.selectAllUser(current, size);

        //总条数
        long total = userEntityPage.getTotal();
        //总页数
        long pages = userEntityPage.getPages();
        // 分页查询的用户
        List<UserEntity> userEntityList = userEntityPage.getRecords();


        if (header.equals("feign")) {
            //feign调用

        } else {
            //前端调用

        }
        return null;
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


        if (header.equals("feign")) {
            //feign调用

        } else {
            //前端调用

        }
        return null;
    }

    /**
     * 分割 role表
     * =======================================================================================
     */

    @PostMapping("/updateUserRole")
    public Object updateUserRole(HttpServletRequest request, String userId, String role) {

        String header = request.getHeader("占位");
        UserEntity userEntity = userService.selectUserById(userId);
        RoleEntity roleEntity = new RoleEntity();

        String roleId = userEntity.getRoleId();
        roleEntity.setRoleId(roleId);
        roleEntity.setRole(role);

        Boolean bool = roleService.updateUserRoleById(roleEntity);
        if (header.equals("feign")) {
            //feign调用

        } else {
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
     *
     * @param request
     * @param userId
     * @param userInfo
     * @return
     */
    @PostMapping("/updateUserInfo")
    public Object updateUserInfo(HttpServletRequest request, String userId, UserInfoEntity userInfo) {
        String header = request.getHeader("占位");
        UserEntity userEntity = userService.selectUserById(userId);

        userInfo.setUserInfoId(userEntity.getUserInfoId());
        Boolean bool = userInfoService.updateUserInfo(userInfo);


        if (header.equals("feign")) {
            //feign调用

        } else {
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
