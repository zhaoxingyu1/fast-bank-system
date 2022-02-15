package com.seckill.orderservice.exception;

/**
 * @author : 陈征
 * @date : 2022-02-15 19:48
 */

public class OrderNotFoundException extends Exception{
    public OrderNotFoundException(String message) {
        super(message);
    }
}
