package com.seckill.common.globalconfig.exceptionhandler;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.seckill.common.consts.FeignConsts;
import com.seckill.common.enums.CodeEnum;
import com.seckill.common.exception.ForbiddenException;
import com.seckill.common.exception.NotFoundException;
import com.seckill.common.response.DataFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : 陈征
 * @date : 2022-02-19 05:06
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    private Object baseException(HttpServletRequest request, HttpServletResponse response, Exception e) {
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
        } else if (e instanceof AuthorizationException) {
            code = CodeEnum.UNAUTHORIZED;
        } else if (e instanceof BindException) {

            BindException ex = (BindException) e;
            String message = ex.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .reduce((s1, s2) -> s1 + s2)
                    .get();
            return DataFactory.fail(CodeEnum.BAD_REQUEST, message);


        } else if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            String message = ex.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .reduce((s1, s2) -> s1 + s2)
                    .get();
            return DataFactory.fail(CodeEnum.BAD_REQUEST, message);
        } else if (e instanceof ValidationException) {

            ValidationException ex = (ValidationException) e;
            String message = "";
            String[] split = ex.getMessage().split(",");
            List<String[]> messages = Arrays.stream(split).map(i -> i.split(":")).collect(Collectors.toList());
            for (String[] s : messages) {
                message += s[1] + " ";
            }
            return DataFactory.fail(CodeEnum.BAD_REQUEST, message);
        } else if (e instanceof TokenExpiredException) {
            return DataFactory.fail(CodeEnum.TOKEN_EXPIRED, "令牌已过期");
        }

        return DataFactory.fail(code, e.getMessage());
    }


}
