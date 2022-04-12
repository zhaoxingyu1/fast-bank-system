package com.seckill.userservice.controller;

import com.seckill.common.consts.FeignConsts;
import com.seckill.common.consts.HeaderConsts;
import com.seckill.common.entity.user.RoleEntity;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.entity.user.UserInfoEntity;
import com.seckill.common.enums.CodeEnum;
import com.seckill.common.feign.OrderClient;
import com.seckill.common.feign.UserClient;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;


/**
 * @author zxy
 * @Classname UserController
 * @Date 2022/2/11 14:41
 */
@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;
    @Resource
    private RedisTemplate<String, Object> redis;
    @Resource
    private OrderClient orderClient;


    @PostMapping("/sendEmail")
    public Object sendEmail(String email) {

        try {
            userService.sendEmail(email);
        } catch (MailException e) {

            return DataFactory.fail(CodeEnum.INTERNAL_ERROR, "验证码发送失败,请重试");
        }
        return DataFactory.success(SimpleData.class, "验证码发送成功,请注意查收");
    }


    @PostMapping("/login")
    public Object userLogin(HttpServletResponse response, @NotEmpty(message = "用户名不能为空") String username, @NotEmpty(message = "密码不能为空") String password) {

        String jwtToken = null;
        try {

            UserEntity user = userService.selectUserByUsername(username);

            if (user == null) {
                throw new UnknownAccountException();
            }
            if (!user.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))) {
                throw new IncorrectCredentialsException();
            }


            JwtToken token;

            token = TokenUtil.convertJwtToken(user);

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

        if (redis.opsForValue().get("userFlow") != null) {
            redis.opsForValue().increment("userFlow");
        } else {
            redis.opsForValue().set("userFlow", 1);
        }

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
    @Valid
    public Object createUser(@Valid UserEntity user, @Valid UserInfoEntity userInfo, @Valid RoleEntity role, @NotEmpty(message = "验证码不能为空") String emailCode) throws Exception {


        ValueOperations<String, Object> opsForValue = redis.opsForValue();

        if (userInfo.getNickname() == null || userInfo.getNickname().equals("")) {
            userInfo.setNickname(user.getUsername() + "123");
        }

        Object code = opsForValue.get(userInfo.getEmail());
        code = "" + code + "";
        if (code == null || code.equals("") || !code.equals(emailCode)) {
            return DataFactory.fail(CodeEnum.FORBIDDEN, "验证码错误,或者验证码已过期");
        }

        if (userService.selectUserByUsername(user.getUsername()) != null) {
            return DataFactory.fail(CodeEnum.FORBIDDEN, "此用户名已经注册过");
        }

        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));

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


    @PostMapping("/updateUserPassWd")
    public Object updateUserPassWd(HttpServletRequest request, HttpServletResponse response, String oldPassword, String newPassword) {

        // 通过jwt获取用户id进行删除
        String jwtToken = request.getHeader(HeaderConsts.JWT_TOKEN);
        try {


            JwtToken token = TokenUtil.decodeToken(jwtToken);

            UserEntity user = userService.selectUserById(token.getUserId());

            String oldPasswordMD5 = DigestUtils.md5DigestAsHex(oldPassword.getBytes());

            String newPasswordMD5 = DigestUtils.md5DigestAsHex(newPassword.getBytes());

            if (!user.getPassword().equals(oldPasswordMD5)) {
                return DataFactory.fail(CodeEnum.FORBIDDEN, "密码错误,请重新输入");
            }

            if (user.getPassword().equals(newPasswordMD5)) {
                return DataFactory.fail(CodeEnum.FORBIDDEN, "新密码不能和旧密码相同");
            }

            user.setUserId(token.getUserId());
            user.setPassword(newPasswordMD5);

            Boolean bool = userService.updateUserById(user);
            if (!bool) {
                return DataFactory.fail(CodeEnum.INTERNAL_ERROR, "修改失败,出现未知错误");
            }

//            // 更新令牌
//            //登录完成之后需要颁发令牌
//            // 将更新的用户名存进token
//            jwtToken = TokenUtil.issuedToken(token);

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
    public Object selectUserById(HttpServletRequest request, @RequestParam(required = false) String userId) {

        if (request.getHeader(FeignConsts.HEADER_NAME) != null) {
            UserEntity userEntity = userService.selectUserById(userId);
            return userEntity;
        } else {
            if (userId != null) {
                UserEntity user = userService.selectUserById(userId);
                return DataFactory.success(SimpleData.class, "ok").parseData(user);
            } else {
                String jwtToken = request.getHeader(HeaderConsts.JWT_TOKEN);
                JwtToken token = TokenUtil.decodeToken(jwtToken);
                String tokenUserId = token.getUserId();
                UserEntity userEntity = userService.selectUserById(tokenUserId);
                return DataFactory.success(SimpleData.class, "ok").parseData(userEntity);
            }
        }

    }


    /**
     * 修改用户信息
     *
     * @param request
     * @param nickname
     * @param email
     * @param phone
     * @param bankCard
     * @param workingState
     * @return
     * @throws Exception
     */
    @PostMapping("/updateUserInfo")
    public Object updateUserInfo(HttpServletRequest request, @RequestParam(required = false) String nickname, @Email @RequestParam(required = false) String email, @Pattern(regexp = "^1[3|4|5|7|8][0-9]{9}$", message = "电话号码格式错误") @RequestParam(required = false) String phone, @RequestParam(required = false) String bankCard, @RequestParam(required = false) Integer workingState) throws Exception {

        String jwtToken = request.getHeader(HeaderConsts.JWT_TOKEN);

        JwtToken token = TokenUtil.decodeToken(jwtToken);
        UserInfoEntity userInfo = userInfoService.selectUserInfoById(token.getUserInfoId());

        if (nickname != null && !nickname.equals("")) {
            userInfo.setNickname(nickname);
        }
        if (email != null && !email.equals("")) {
            userInfo.setEmail(email);
        }
        if (phone != null && !phone.equals("")) {
            userInfo.setPhone(phone);
        }
        if (bankCard != null && !bankCard.equals("")) {
            userInfo.setBankCard(bankCard);
        }
        if (workingState != null && !workingState.equals("")) {
            userInfo.setWorkingState(workingState);
        }
        Boolean bool = userInfoService.updateUserInfo(userInfo);

        if (!bool) {
            return DataFactory.fail(CodeEnum.INTERNAL_ERROR, "修改失败,出现未知错误");
        }

        return DataFactory.success(SimpleData.class, "ok");
    }

    @PostMapping("/retrievePassword")
    public Object retrievePassword(String email, String emailCode, String newPassword) {

        ValueOperations<String, Object> opsForValue = redis.opsForValue();
        Object code = opsForValue.get(email);
        code = "" + code + "";
        if (code == null || code.equals("") || !code.equals(emailCode)) {
            return DataFactory.fail(CodeEnum.FORBIDDEN, "验证码错误,或者验证码已过期");
        }

        UserInfoEntity userInfoEntity = userInfoService.selectByEmail(email);

        UserEntity user = userService.selectByUserInfoId(userInfoEntity.getUserInfoId());

        user.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
        Boolean bool = userService.updateUserById(user);

        if (!bool) {
            return DataFactory.fail(CodeEnum.INTERNAL_ERROR, "修改失败,请联系管理员");
        }

        return DataFactory.success(SimpleData.class, "修改成功");
    }

    @PostMapping("/payment")
    public Object Payment(HttpServletRequest request,@RequestParam String orderId,@RequestParam  BigDecimal money) {


        String jwtToken = request.getHeader(HeaderConsts.JWT_TOKEN);

        JwtToken token = TokenUtil.decodeToken(jwtToken);

        UserInfoEntity userInfoEntity = userInfoService.selectUserInfoById(token.getUserInfoId());

        // -1：小于； 0 ：等于； 1 ：大于；
        if (userInfoEntity.getWalletBalance().compareTo(money)==1 || userInfoEntity.getWalletBalance().compareTo(money)==0 ) {

            userInfoEntity.setWalletBalance(userInfoEntity.getWalletBalance().subtract(money));

            Boolean bool = userInfoService.updateUserInfo(userInfoEntity);

            if(!bool){
                return DataFactory.fail(CodeEnum.INTERNAL_ERROR,"未知错误,请联系管理员");
            }
            orderClient.updateState(orderId,"Fulfilled");
            return DataFactory.success(SimpleData.class,"ok");

        }else {
            return DataFactory.fail(CodeEnum.FORBIDDEN,"您的余额不足,请充值");
        }
    }


}
