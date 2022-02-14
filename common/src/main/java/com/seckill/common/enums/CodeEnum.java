package com.seckill.common.enums;

/**
 * @author : 陈征
 * @date : 2022-02-14 17:29
 */

public enum CodeEnum {
    INTERNAL_ERROR(500),
    OK(200),
    NOT_FOUND(404),
    FORBIDDEN(403);


    private final Integer code;

    private CodeEnum(int code) {
        this.code = code;
    }

    public int value() {
        return code;
    }
}
