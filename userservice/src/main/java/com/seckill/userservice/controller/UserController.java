package com.seckill.userservice.controller;

import com.seckill.common.consts.HeaderConsts;
import com.seckill.common.entity.user.RoleEntity;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.entity.user.UserInfoEntity;
import com.seckill.common.enums.CodeEnum;
import com.seckill.common.jwt.JwtToken;

import com.seckill.common.jwt.TokenUtil;
import com.seckill.common.response.DataFactory;
import com.seckill.common.response.SimpleData;

import com.seckill.userservice.service.RoleService;
import com.seckill.userservice.service.UserInfoService;


import com.seckill.userservice.service.UserService;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.MailException;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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
    @Resource
    private RedisTemplate<String, Object> redis;


    @PostMapping("/sendEmail")
    public Object sendEmail(String email){

        try {
            userService.sendEmail(email);
        }catch (MailException e){

            return DataFactory.fail(CodeEnum.INTERNAL_ERROR,"验证码发送失败,请重试");
        }
        return DataFactory.success(SimpleData.class,"验证码发送成功,请注意查收");
    }


    @PostMapping("/login")
    public Object userLogin(HttpServletRequest request, HttpServletResponse response, @RequestParam("username") String username, @RequestParam("password") String password) {

        String jwtToken = null;
        try {

            UserEntity user = userService.selectUserByUsername(username);

            if (user == null) {
                new UnknownAccountException();
            }
            if (!user.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))) {
                new IncorrectCredentialsException();
            }



            JwtToken token = new JwtToken();

            token= TokenUtil.convertJwtToken(user);

            jwtToken = TokenUtil.issuedToken(token);



        } catch (UnknownAccountException e) {
            //用户名错误
            return DataFactory.fail(CodeEnum.FORBIDDEN, "用户名错误");
        } catch (IncorrectCredentialsException e) {
            // 密码错误
            return DataFactory.fail(CodeEnum.FORBIDDEN, "密码错误");
        } catch (Exception e) {
            // 登录异常 请联系管理员
            return DataFactory.fail(CodeEnum.INTERNAL_ERROR, "未知错误，请联系管理员");
        }

        response.setHeader(HeaderConsts.JWT_TOKEN, jwtToken);
        response.setHeader("Access-control-Expose-Headers", HeaderConsts.JWT_TOKEN);

        return DataFactory.success(SimpleData.class, "登录成功");
    }


    // 注销

    @GetMapping("/logout")
    public Object userLogout(HttpServletResponse response) {

        String jwtToken = "";
        response.setHeader(HeaderConsts.JWT_TOKEN, jwtToken);
        response.setHeader("Access-control-Expose-Headers", HeaderConsts.JWT_TOKEN);

        return DataFactory.success(SimpleData.class, "登出成功");
    }

    /**
     * 注册
     *
     * @param user
     * @param userInfo
     * @param role
     * @return
     * @throws Exception
     */
    @PostMapping("/registerUser")
    public Object createUser(UserEntity user, UserInfoEntity userInfo, RoleEntity role,String emailCode) throws Exception {


        ValueOperations<String, Object> opsForValue = redis.opsForValue();



        Object code =opsForValue.get(userInfo.getEmail());
        code = ""+code+"";
        if (code==null || code.equals("") || !code.equals(emailCode)){
            return DataFactory.fail(CodeEnum.FORBIDDEN,"验证码错误,或者验证码已过期");
        }

        if (userService.selectUserByUsername(user.getUsername())!=null){
            return DataFactory.fail(CodeEnum.FORBIDDEN,"此用户名已经注册过");
        }

        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
//        DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        Boolean bool = userService.insertUser(user, userInfo, role);

        if (!bool) {
            return DataFactory.fail(CodeEnum.INTERNAL_ERROR, "注册失败,出现未知错误");
        }

        return DataFactory.success(SimpleData.class, "注册成功");

    }


    @GetMapping("/deleteUserById")
    public Object deleteUserById(HttpServletRequest request) throws Exception {

        String jwtToken = request.getHeader(HeaderConsts.JWT_TOKEN);

        JwtToken token = TokenUtil.decodeToken(jwtToken);

        String userId = token.getUserId();

        Boolean bool = userService.deleteUserById(userId);

        if (!bool) {
            return DataFactory.fail(CodeEnum.INTERNAL_ERROR, "删除失败,出现未知错误");
        }

        return DataFactory.success(SimpleData.class, "删除用户成功");
    }


    @PostMapping("/updateUserById")
    public Object updateUserById(HttpServletRequest request, HttpServletResponse response, @RequestParam(required = false) String username, @RequestParam(required = false) String password) {

        // 通过jwt获取用户id进行删除
        String jwtToken = request.getHeader(HeaderConsts.JWT_TOKEN);
        try {

            JwtToken token = TokenUtil.decodeToken(jwtToken);

            UserEntity user = new UserEntity();

            user.setUserId(token.getUserId());
            user.setUsername(username);
            user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));

            Boolean bool = userService.updateUserById(user);
            if (!bool) {
                return DataFactory.fail(CodeEnum.INTERNAL_ERROR, "修改失败,出现未知错误");
            }

            // 更新令牌
            //登录完成之后需要颁发令牌
            // 将更新的用户名存进token
            token.setUsername(username);
            jwtToken = TokenUtil.issuedToken(token);

        } catch (Exception e) {
            return DataFactory.fail(CodeEnum.INTERNAL_ERROR, "修改失败,出现未知错误");
        }

        response.setHeader(HeaderConsts.JWT_TOKEN, jwtToken);
        response.setHeader("Access-control-Expose-Headers", HeaderConsts.JWT_TOKEN);

        return DataFactory.success(SimpleData.class, "修改用户成功");
    }

    /**
     * 根据id查询用户
     *
     * @param request
     * @return
     */
    @GetMapping("/selectUserById")
    public Object selectUserById(HttpServletRequest request) throws Exception {

        String jwtToken = request.getHeader(HeaderConsts.JWT_TOKEN);
        JwtToken token = TokenUtil.decodeToken(jwtToken);

        String userId = token.getUserId();

        UserEntity userEntity = userService.selectUserById(userId);


        return DataFactory.success(SimpleData.class, "ok").parseData(userEntity);
    }


    /**
     * 修改用户信息
     *
     * @param request
     * @param userInfo
     * @return
     */

    @PostMapping("/updateUserInfo")
    public Object updateUserInfo(HttpServletRequest request, UserInfoEntity userInfo) throws Exception {

        String jwtToken = request.getHeader(HeaderConsts.JWT_TOKEN);

        JwtToken token = TokenUtil.decodeToken(jwtToken);

        userInfo.setUserInfoId(token.getUserInfoId());
        Boolean bool = userInfoService.updateUserInfo(userInfo);

        if (!bool) {
            return DataFactory.fail(CodeEnum.INTERNAL_ERROR, "修改失败,出现未知错误");
        }

        return DataFactory.success(SimpleData.class, "ok");
    }


}
