package com.seckill.common.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author zxy
 * @Classname UserApplicationRecordEntity
 * @Date 2022/3/25 17:24
 */
@Data
@TableName("user_application_record")
@AllArgsConstructor
@NoArgsConstructor
public class UserApplicationRecordEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String userApplicationRecordId;

    private String productName;
    //产品类型：loan：贷款
    private String productType;

    private String username;
    private Integer age;
    //0：失业 1：工作
    private Integer workingState;
    //逾期次数
    private Integer overdueNumber;
    //0: 失信 1：有信用
    private Integer creditStatus;
    //0 : 不通过 ，1：通过
    private Integer throughState;
    // 申请通不通过的原因
    private String cause;
    // 申请时间
    private Long ctime;


}
