package com.seckill.common.jwt;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zxy
 * @Classname JwtToken
 * @Date 2022/3/19 22:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtToken {

    private String userId;

    private String roleId;

//    private String userProductId;

    private String userInfoId;

    private String username;
    private String role;


}
