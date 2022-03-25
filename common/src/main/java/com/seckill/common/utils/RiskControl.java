package com.seckill.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zxy
 * @Classname RishControl
 * @Date 2022/3/25 18:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiskControl {

    private Integer throughState;
    private String cause;
}
