package com.seckill.orderservice.exception;

/**
 * @author : 陈征
 * @date : 2022-02-16 19:42
 */

// 重复下单时抛出的异常
public class DuplicateOrderException extends Exception {
    public DuplicateOrderException(String message) {
        super(message);
    }
}
