package com.seckill.common.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

/**
 * @author : 陈征
 * @date : 2022-02-14 22:17
 */

@Configuration
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(FeignConsts.HEADER_NAME, FeignConsts.HEADER_VALUE);
    }
}
