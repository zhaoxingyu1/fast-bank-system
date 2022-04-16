package com.seckill.common.exception;

/**
 * @Author: 七画一只妖
 * @Date: 2022/2/21 18:25
 */
//数据库操作异常
public class DatabaseOperationException extends Exception{
    public DatabaseOperationException(String message) {
        super(message);
    }
}
