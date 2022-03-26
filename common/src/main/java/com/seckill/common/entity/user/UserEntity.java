package com.seckill.common.entity.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * @author zxy
 * @Classname UserEntity
 * @Date 2022/2/11 14:16
 */
@Data
@TableName("user")
public class UserEntity {

    @NotNull(message = "id不能为空")
    @TableId(type = IdType.ASSIGN_UUID)
    private String userId;

    @NotNull(message = "用户名不能为空")
    private String username;

    @NotNull(message = "密码不能为空")
    private String password;

    @NotNull(message = "id不能为空")
    private String roleId;

    @NotNull(message = "id不能为空")
    private String userInfoId;

    @TableField(fill = FieldFill.INSERT)
    private Long ctime;

    @TableField(exist = false)
    private RoleEntity userRole;
    @TableField(exist = false)
    private UserInfoEntity userInfo;

}
