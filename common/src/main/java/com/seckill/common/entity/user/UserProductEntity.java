package com.seckill.common.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zxy
 * @Classname UserProductEntity
 * @Date 2022/2/18 19:23
 */
@Data
@TableName("user_product")
public class UserProductEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String userProductId;
    private String userId;
    private String productName;
    private BigDecimal price;
    private Integer buyState;
    private Integer number;
    private Long mtime;

}
