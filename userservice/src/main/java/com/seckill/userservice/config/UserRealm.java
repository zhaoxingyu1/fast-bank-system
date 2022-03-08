package com.seckill.userservice.config;

//import com.huang.pojo.User;
//import com.huang.service.UserService;
//import org.apache.catalina.security.SecurityUtil;
//import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

public class UserRealm extends AuthorizingRealm {
//    @Autowired
//    UserService userService;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行了授权");

        SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();

        //info.addStringPermission("user:add");
        //拿到当前用户登陆对象
//        Subject subject= SecurityUtils.getSubject();
//        User currentUser= (User) subject.getPrincipal();//拿到User对象
//        info.addStringPermission(currentUser.getPerms());//设置当前用户对象

        return info;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("执行了认证");

        //用户名，密码，数据库中获取

        UsernamePasswordToken userToken=(UsernamePasswordToken) authenticationToken;

//        User user=userService.queryUserByName(userToken.getUsername());//获取用户名
//
//        String name=user.getName();
//        String password=user.getPwd();
//        if(user==null){//说明查无此人
//            return null;
//        }

        //密码认证,shiro做
//        return new SimpleAuthenticationInfo(user,password,"");//放入User对象
        return null;
    }


}
