package com.seckill.common.globalconfig.exceptionhandler;

import com.seckill.common.consts.FeignConsts;
import com.seckill.common.enums.CodeEnum;
import com.seckill.common.exception.ForbiddenException;
import com.seckill.common.exception.NotFoundException;
import com.seckill.common.response.DataFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : 陈征
 * @date : 2022-02-19 05:06
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(Exception.class)
    private Object baseException(HttpServletRequest request, Exception e) {
        log.error(e.getClass().toString());
        e.printStackTrace();
        if (request.getHeader(FeignConsts.HEADER_NAME) != null) {
            return null;
        }
        CodeEnum code = CodeEnum.INTERNAL_ERROR;
        if (e instanceof ForbiddenException) {
            code = CodeEnum.FORBIDDEN;
        } else if (e instanceof NotFoundException) {
            code = CodeEnum.NOT_FOUND;
        }
        return DataFactory.fail(code, e.getMessage());
    }
}
