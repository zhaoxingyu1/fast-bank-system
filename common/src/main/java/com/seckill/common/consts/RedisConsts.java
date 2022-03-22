package com.seckill.common.consts;

/**
 * @author : 陈征
 * @date : 2022-03-22 14:56
 */

public class RedisConsts {
    public static String ORDER_KEY_PREFIX_EXPIRED = "order_expired";
    // 订单等待15分钟付款
    public static long ORDER_WAITING_TIME = 15 * 60 * 1000;
}
