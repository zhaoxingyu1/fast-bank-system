package com.seckill.common.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author zxy
 * @Classname UserInfoEntity
 * @Date 2022/2/11 14:47
 */
@Data
@TableName("user_info")
public class UserInfoEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private String userInfoId;
    private String nickname;
    private String realName;
    private Integer age;
    private Integer gender;
    private String phone;
    private String idCard;
    private String email;
    private Integer workingState;
    private String bankCard;
    private Integer overdue;
    private Integer creditStatus;
    private Long ctime;
    private Long mtime;


}
