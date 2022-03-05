package com.seckill.common.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zxy
 * @Classname RoleEntity
 * @Date 2022/2/11 14:44
 */
@Data
@TableName("role")
public class RoleEntity {

    @NotNull(message = "id不能为空")
    @TableId(type = IdType.ASSIGN_UUID)
    private String roleId;

    @NotNull(message = "权限不能为空")
    private String role;
}
