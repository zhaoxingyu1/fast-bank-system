package com.seckill.productservice.exception;

/**
 * @Author: 七画一只妖
 * @Date: 2022/2/18 16:46
 */
// 产品已存在
public class AlreadyExistsProductException extends Exception{
    public AlreadyExistsProductException(String message) {
        super(message);
    }
}
