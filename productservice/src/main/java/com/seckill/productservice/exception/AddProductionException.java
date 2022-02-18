package com.seckill.productservice.exception;

/**
 * @Author: 七画一只妖
 * @Date: 2022/2/18 16:27
 */
// 添加失败
public class AddProductionException extends Exception{
    public AddProductionException(String message) {
        super(message);
    }
}
