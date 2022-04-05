package com.seckill.common.entity.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : 陈征
 * @date : 2022-04-05 18:34
 */

@Data
public class OrderUserEntity extends OrderEntity {
    Boolean auth;
    String name;

    public OrderUserEntity(OrderEntity entity, Boolean auth, String name) {
        super(entity);
        this.auth = auth;
        this.name = name;
    }
}
