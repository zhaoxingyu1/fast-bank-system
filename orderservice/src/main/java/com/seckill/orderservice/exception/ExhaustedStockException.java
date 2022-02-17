package com.seckill.orderservice.exception;

/**
 * @author : 陈征
 * @date : 2022-02-16 19:48
 */

// 库存耗尽时抛出的异常
public class ExhaustedStockException extends Exception {
    public ExhaustedStockException(String message) {
        super(message);
    }
}
