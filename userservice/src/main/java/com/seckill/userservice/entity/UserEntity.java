package com.seckill.userservice.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author zxy
 * @Classname UserEntity
 * @Date 2022/2/11 14:16
 */
@Data
@TableName("user")
public class UserEntity {
    @TableId
    private Long userId;
    private String username;
    private String password;
    private Long roleId;
    private Long userInfoId;
    private Long ctime;
}
