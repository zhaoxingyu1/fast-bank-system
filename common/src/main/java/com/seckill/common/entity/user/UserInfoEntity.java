package com.seckill.common.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author zxy
 * @Classname UserInfoEntity
 * @Date 2022/2/11 14:47
 */
@Data
@TableName("user_info")
public class UserInfoEntity {

    @NotNull(message = "id不能为空")
    @TableId(type = IdType.ASSIGN_UUID)
    private String userInfoId;

    @NotNull(message = "昵称不能为空")
    private String nickname;

    @NotNull(message = "真实名字不能为空")
    private String realName;

    @Range(min = 0,max = 100,message = "年龄必须大于等于0，小于等于100")
    private Integer age;

    @NotNull(message = "性别不能为空")
    private Integer gender;

    @NotNull(message = "电话号码不能为空")
    @Pattern(regexp = "^1[3|4|5|7|8][0-9]{9}$",message = "电话号码格式错误")
    private String phone;

    @Pattern(regexp = "^\\d{15}|\\d{18}$",message = "身份证号码错误")
    private String idCard;

    @Email(message = "邮箱格式错误")
    private String email;
    private Integer workingState;

    @CreditCardNumber(message = "银行卡号错误")
    private String bankCard;
    private Integer overdueNumber;
    private Integer creditStatus;
    private Long ctime;
    private Long mtime;


}
