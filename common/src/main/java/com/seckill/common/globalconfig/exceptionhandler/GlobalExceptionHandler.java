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
        return DataFactory.fail(CodeEnum.INTERNAL_ERROR, "未知错误");
    }

    @ResponseBody
    @ExceptionHandler({NotFoundException.class})
    private Object orderNotfound(HttpServletRequest request, Exception e) {
        log.error(e.getClass().toString());
        e.printStackTrace();
        if (request.getHeader(FeignConsts.HEADER_NAME) != null) {
            return null;
        }
        return DataFactory.fail(CodeEnum.NOT_FOUND, e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler({ForbiddenException.class})
    private Object orderForbidden(HttpServletRequest request, Exception e) {
        log.error(e.getClass().toString());
        e.printStackTrace();
        if (request.getHeader(FeignConsts.HEADER_NAME) != null) {
            return null;
        }
        return DataFactory.fail(CodeEnum.FORBIDDEN, e.getMessage());
    }
}
