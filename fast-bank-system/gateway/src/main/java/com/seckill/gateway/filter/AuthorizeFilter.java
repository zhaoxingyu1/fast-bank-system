package com.seckill.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.seckill.common.consts.HeaderConsts;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.jwt.JwtToken;
import com.seckill.common.jwt.TokenUtil;
import io.jsonwebtoken.Claims;
import lombok.SneakyThrows;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Order(-1)
@Component
public class AuthorizeFilter implements GlobalFilter {

    @Resource
    private RedisTemplate<String, Object> redis;

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.获取请求参数
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();

        MultiValueMap<String, String> params = request.getQueryParams();
        // 2.获取authorization参数
        String jwtToken = request.getHeaders().getFirst(HeaderConsts.JWT_TOKEN);

        ValueOperations<String, Object> opsForValue = redis.opsForValue();

        if ("/user/login".equals(path)) {

            String hostAddress = request.getRemoteAddress().getAddress().getHostAddress();

            if (opsForValue.get("switch")!=null && opsForValue.get("switch").equals("true")) {

                if (opsForValue.get(hostAddress) != null) {
                    opsForValue.increment(hostAddress);
                } else {
                    // 设置十分钟
                    opsForValue.set(hostAddress, 1, 10, TimeUnit.MINUTES);
                }
                Integer value = (Integer) opsForValue.get(hostAddress);

                if (value >= 100) {
                    // 10分钟 同一ip 超过 100次请求登录则锁死
                    opsForValue.set(hostAddress, 9999);
                    exchange.getResponse().setStatusCode(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);
                    return exchange.getResponse().setComplete();
                }
            }
            return chain.filter(exchange);

        }

        // 3.校验
        if ("/user/registerUser".equals(path) || "/user/sendEmail".equals(path) || "/user/retrievePassword".equals(path)) {
            // 登录接口 ，放行
            return chain.filter(exchange);
        }

        if (jwtToken == null || jwtToken.equals("")) {
            exchange.getResponse().setStatusCode(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
            return exchange.getResponse().setComplete();
        }


        // 解析校验token
        JwtToken token;


        try {
            token = TokenUtil.decodeToken(jwtToken);
        } catch (TokenExpiredException e) {

            exchange.getResponse().setStatusCode(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
            return exchange.getResponse().setComplete();
        }


        String[] split = path.split("/", 3);

        if (split[1].equals("product") || split[1].equals("order") || (split[1].equals("admin") && token.getRole().equals("admin")) || split[1].equals("user")) {
            return chain.filter(exchange);
        }


        return exchange.getResponse().setComplete();

    }
}