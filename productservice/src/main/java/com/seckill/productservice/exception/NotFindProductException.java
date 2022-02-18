package com.seckill.productservice.exception;

/**
 * @Author: 七画一只妖
 * @Date: 2022/2/18 16:21
 */
// 找不到产品
public class NotFindProductException extends Exception {
    public NotFindProductException(String message) {
        super(message);
    }
}
