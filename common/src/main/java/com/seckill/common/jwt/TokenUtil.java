package com.seckill.common.jwt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.seckill.common.entity.user.UserEntity;


import java.util.Date;

public class TokenUtil {

    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;//token到期时间10小时
    private static final String TOKEN_SECRET = "123456789*jwtToken";  //密钥盐


    public static String issuedToken(JwtToken jwtToken) {

        String token = null;

        Date expireAt = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        token = JWT.create()
                .withIssuer("system")//发行人
                .withClaim("jwtToken", JSONObject.toJSONString(jwtToken))//存放数据
                .withExpiresAt(expireAt)//过期时间
                .sign(Algorithm.HMAC256(TOKEN_SECRET));

        return token;
    }


    public static JwtToken convertJwtToken(UserEntity user){

        JwtToken jwtToken = new JwtToken(user.getUserId(), user.getRoleId(), user.getUserInfoId(), user.getUsername(), user.getUserRole().getRole());


        return jwtToken;
    }




    public static JwtToken decodeToken(String token){


        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("system").build();//创建token验证器
        DecodedJWT decodedJWT = jwtVerifier.verify(token);


//        System.out.println("认证通过：");
//        System.out.println("username: " + decodedJWT.getClaim("username").asString());
//        System.out.println("过期时间：      " + decodedJWT.getExpiresAt());

        String jwtTokenString = decodedJWT.getClaim("jwtToken").asString();



        JwtToken jwtToken = JSONObject.parseObject(jwtTokenString, JwtToken.class);


//         catch (IllegalArgumentException | JWTVerificationException e) {
        //抛出错误即为验证不通过
//            return false;

        return jwtToken;
    }

}

