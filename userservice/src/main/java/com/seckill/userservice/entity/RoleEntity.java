package com.seckill.userservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author zxy
 * @Classname RoleEntity
 * @Date 2022/2/11 14:44
 */
@Data
@TableName("role")
public class RoleEntity {

    private Long roleId;
    private String role;
}
