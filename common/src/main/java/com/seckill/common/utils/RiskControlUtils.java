package com.seckill.common.utils;

import com.seckill.common.entity.user.UserEntity;

/**
 * @author zxy
 * @Classname RiskControlUtils
 * 风险控制工具类
 * @Date 2022/3/25 15:29
 */
public class RiskControlUtils {

    public static RiskControl isQualified(UserEntity user){
        // 逾期次数
        Integer overdueNumber = user.getUserInfo().getOverdueNumber();
        // 工作状态 0失业 , 1 就业
        Integer workingState = user.getUserInfo().getWorkingState();
        // 失信状态 0：失信 1：不失信
        Integer creditStatus = user.getUserInfo().getCreditStatus();
        // age
        Integer age = user.getUserInfo().getAge();

        if(creditStatus==0){
            return new RiskControl(0,"信用状态为失信");
        }

        if(overdueNumber>3){
            return new RiskControl(0,"逾期次数超过三次");
        }
        if(workingState==0){
            return new RiskControl(0,"工作状态为失业");
        }
        if(age<18){
            return new RiskControl(0,"年龄小于18岁");
        }


        return new RiskControl(1,"信用没有问题,成功通过审核");
    }

}

