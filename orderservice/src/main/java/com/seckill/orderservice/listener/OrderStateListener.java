package com.seckill.orderservice.listener;

import com.alibaba.fastjson.JSONObject;
import com.seckill.common.consts.RedisConsts;
import com.seckill.common.enums.OrderStateEnum;
import com.seckill.common.feign.OrderClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Locale;

/**
 * @author : 陈征
 * @date : 2022-03-22 14:16
 */

@Component
@Slf4j
public class OrderStateListener implements MessageListener {
    @Resource
    private RedisTemplate<String, Object> redis;

    @Resource
    private OrderClient orderClient;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = String.valueOf(redis.getKeySerializer().deserialize(message.getBody()));
        if (key.startsWith(RedisConsts.ORDER_KEY_PREFIX_EXPIRED)) {
            log.info("order key expired: " + key);
            Object o = orderClient.updateState(
                    key.substring(RedisConsts.ORDER_KEY_PREFIX_EXPIRED.length()),
                    OrderStateEnum.REJECTED.name()
            );
            log.info("------- feign resp -----");
            log.info(JSONObject.toJSONString(o, true));
        }
    }
}
