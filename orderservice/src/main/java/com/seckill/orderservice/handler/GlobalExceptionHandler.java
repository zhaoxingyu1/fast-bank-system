package com.seckill.orderservice.handler;

import com.seckill.common.enums.CodeEnum;
import com.seckill.common.response.BaseData;
import com.seckill.common.response.DataFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author : 陈征
 * @date : 2022-02-14 16:41
 */

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(Exception.class)
    private BaseData baseException(Exception e) {
        log.error(e.getMessage());
        return DataFactory.fail(CodeEnum.INTERNAL_ERROR, e.getMessage());
    }
}
