package com.seckill.common.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author zxy
 * @Classname RiskControlEntity
 * @Date 2022/3/31 21:52
 */
@Data
@TableName("risk_control")
public class RiskControlEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String riskControlId;

    private Integer overdueNumberRule;

    private Integer workingStateRule;

    private Integer creditStatusRule;

    private Integer ageRule;
}
