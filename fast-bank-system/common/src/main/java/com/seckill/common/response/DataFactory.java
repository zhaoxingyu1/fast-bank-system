package com.seckill.common.response;

import com.seckill.common.enums.CodeEnum;

/**
 * @author : 陈征
 * @date : 2022-01-10 18:17
 */

public class DataFactory {
    public static <T extends BaseData> T success(Class<T> dataClass, String msg) {
        try {
            return dataClass.getConstructor(String.class).newInstance(msg);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BaseData fail(CodeEnum errCode, String message) {
        return new BaseData() {
            final int code = errCode.value();

            final String msg = message;

            @Override
            public int getCode() {
                return code;
            }

            @Override
            public String getMsg() {
                return msg;
            }

            @Override
            protected void parse(Object entity) {
            }

            @Override
            public String toString() {
                return "{\"code\":" + code +
                        ",\"msg\":\"" + msg +
                        "\"}";
            }
        };
    }
}
