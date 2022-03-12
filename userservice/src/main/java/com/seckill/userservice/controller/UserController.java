package com.seckill.userservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.consts.HeaderConsts;
import com.seckill.common.entity.user.RoleEntity;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.entity.user.UserInfoEntity;
import com.seckill.common.enums.CodeEnum;
import com.seckill.common.response.ComplexData;
import com.seckill.common.response.DataFactory;
import com.seckill.common.response.SimpleData;
import com.seckill.common.utils.DigestsUtil;
import com.seckill.common.utils.EmptyUtil;
import com.seckill.common.utils.ShiroUtil;
import com.seckill.userservice.dao.UserDao;
import com.seckill.userservice.jwt.JwtTokenManager;
import com.seckill.userservice.service.RoleService;
import com.seckill.userservice.service.UserInfoService;
import com.seckill.userservice.service.UserService;
import io.jsonwebtoken.Claims;
import org.apache.catalina.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
    @Resource(name = "jwtTokenManager")
    private JwtTokenManager jwtTokenManager;


    @PostMapping("/login")
    public Object userLogin(@RequestParam("username") String username, @RequestParam("password") String password) {

        String jwtToken = null;
        try {
            UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);

            Subject subject = SecurityUtils.getSubject();
            subject.login(usernamePasswordToken);
            //登录完成之后需要颁发令牌
            String sessionId = ShiroUtil.getShiroSessionId();

            UserEntity user = userService.selectUserByUsername(username);

            Map<String, Object> claims = new HashMap<>();
            claims.put("shiroUser", JSONObject.toJSONString(user));

            jwtToken = jwtTokenManager.issuedToken("system", subject.getSession().getTimeout(), sessionId, claims);


        } catch (UnknownAccountException e) {
            //用户名错误
            return DataFactory.fail(CodeEnum.INTERNAL_ERROR, "用户名错误");
        } catch (IncorrectCredentialsException e) {
            // 密码错误
            return DataFactory.fail(CodeEnum.INTERNAL_ERROR, "密码错误");
        } catch (Exception e) {
            // 登录异常 请联系管理员
            return DataFactory.fail(CodeEnum.INTERNAL_ERROR, "未知错误，请联系管理员");
        }

        return DataFactory.success(SimpleData.class, "登录成功").parseData(jwtToken);
    }


    // 注销
    @RequiresAuthentication
    @GetMapping("/logout")
    public String userLogout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "登出成功";
    }

    /**
     * 注册
     *
     * @param request
     * @param user
     * @param userInfo
     * @param role
     * @return
     * @throws Exception
     */
    @PostMapping("/registerUser")
    public Object createUser(HttpServletRequest request, UserEntity user, UserInfoEntity userInfo, RoleEntity role) throws Exception {


        user.setPassword(DigestsUtil.sha1(user.getPassword()));

        Boolean bool = userService.insertUser(user, userInfo, role);


        if (!bool) {
            return DataFactory.fail(CodeEnum.INTERNAL_ERROR, "注册失败,出现未知错误");
        }

        return DataFactory.success(SimpleData.class, "注册成功");

    }

    @RequiresRoles(value = "admin")
    @GetMapping("/deleteUserById")
    public Object deleteUserById(HttpServletRequest request, String userId) throws Exception {

        Boolean bool = userService.deleteUserById(userId);

        if (!bool) {
            return DataFactory.fail(CodeEnum.INTERNAL_ERROR, "删除失败,出现未知错误");
        }

        return DataFactory.success(SimpleData.class, "删除用户成功");
    }


    @PostMapping("/updateUserById")
    public Object updateUserById(HttpServletRequest request, @RequestParam(required = false) String username, @RequestParam(required = false) String password) {

        // 通过jwt获取用户id进行删除
        String jwtToken = request.getHeader(HeaderConsts.JWT_TOKEN);
        try {

            jwtTokenManager.isVerifyToken(jwtToken);
            Claims claims = jwtTokenManager.decodeToken(jwtToken);
            UserEntity user = (UserEntity) claims.get("shiroUser");
            user.setUsername(username);
            user.setPassword(DigestsUtil.sha1(password));

            Boolean bool = userService.updateUserById(user);
            if (!bool) {
                return DataFactory.fail(CodeEnum.INTERNAL_ERROR, "修改失败,出现未知错误");
            }
            // 更新令牌
            Map<String, Object> claimsNew = new HashMap<>();
            claimsNew.put("shiroUser", JSONObject.toJSONString(user));
            jwtToken = jwtTokenManager.issuedToken("system", ShiroUtil.getShiroSession().getTimeout(), ShiroUtil.getShiroSessionId(), claimsNew);

        } catch (JWTVerificationException e) {
            return DataFactory.fail(CodeEnum.INTERNAL_ERROR, "头部信息和荷载信息被篡改或者校验令牌已过期");
        } catch (Exception e) {
            return DataFactory.fail(CodeEnum.INTERNAL_ERROR, "修改失败,出现未知错误");
        }

        return DataFactory.success(SimpleData.class, "修改用户成功").parseData(jwtToken);
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

        UserEntity userEntity = userService.selectUserById(userId);


        return DataFactory.success(SimpleData.class, "ok").parseData(userEntity);
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
