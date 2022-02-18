package com.seckill.productservice.exception;

/**
 * @Author: 七画一只妖
 * @Date: 2022/2/18 17:47
 */
// 删除产品失败
public class DeleteProductionException extends Exception{
    public DeleteProductionException(String message) {
        super(message);
    }
}
