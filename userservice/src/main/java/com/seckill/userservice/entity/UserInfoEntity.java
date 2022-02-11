package com.seckill.userservice.entity;

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
    private Long userInfoId;
    private String nickname;
    private String realName;
    private Integer age;
    private Integer gender;
    private String phone;
    private String idCard;
    private String email;
    private Integer workingState;
    private Integer overdue;
    private Long creditId;
    private Long ctime;
    private Long mtime;
}
