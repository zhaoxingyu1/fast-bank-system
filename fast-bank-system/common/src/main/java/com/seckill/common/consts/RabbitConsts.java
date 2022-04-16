package com.seckill.common.consts;

/**
 * @author : 陈征
 * @date : 2022-02-18 16:34
 */

public class RabbitConsts {
//    // 库存减少的队列
//    public static String PRODUCT_DECREASE_QUEUE = "productdecreasequeue";
//
//    // 新增产品但是还没开始抢购的延时队列（financial）
//    public static String PRODUCT_DELAY_QUEUE_F = "financialdelayqueue";
//
//    // 新增产品但是还没开始抢购的延时队列（loan）
//    public static String PRODUCT_DELAY_QUEUE_L = "loandelayqueue";

    // 订单削峰使用的队列
    public static String LOAN_ORDER_QUEUE = "loan_order_queue";
    public static String LOAN_ORDER_EXCHANGE = "loan_order_exchange";
    public static String LOAN_ORDER_ROUTING = "loan_order_routing";
}
