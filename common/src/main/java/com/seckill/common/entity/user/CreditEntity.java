package com.seckill.common.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author zxy
 * @Classname CreditEntity
 * @Date 2022/2/14 22:44
 */
@Data
@TableName("credit")
public class CreditEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String creditId;
    private String userId;
    private Integer creditStatus;
    private Long mtime;
}
