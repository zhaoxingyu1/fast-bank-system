package com.seckill.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
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

    @TableId
    private Long roleId;
    private String role;
}
