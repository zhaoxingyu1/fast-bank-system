package com.seckill.common.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
    @TableId(type = IdType.ASSIGN_UUID)
    private String userId;
    private String username;
    private String password;
    private String roleId;
    private String userInfoId;
    private Long ctime;
    @TableField(exist = false)
    private RoleEntity userRole;
    @TableField(exist = false)
    private UserInfoEntity userInfo;
}
