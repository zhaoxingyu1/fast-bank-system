package com.seckill.common.entity.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author zxy
 * @Classname UserProductEntity
 * @Date 2022/2/18 19:23
 */
@Data
@TableName("user_product")
public class UserProductEntity {

    @NotNull(message = "id不能为空")
    @TableId(type = IdType.ASSIGN_UUID)
    private String userProductId;

    @NotNull(message = "id不能为空")
    private String userId;

    @NotNull(message = "产品名字不能为空")
    private String productName;

    private BigDecimal price;
    private Integer bookingStatus;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long mtime;

}
