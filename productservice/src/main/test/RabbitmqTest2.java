import com.seckill.productservice.ProductApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resources;
import java.util.Date;
import java.util.HashMap;

/**
 * @Author: 七画一只妖
 * @Date: 2022-04-13 21:17
 */
@Slf4j
@SpringBootTest(classes = ProductApplication.class)
public class RabbitmqTest2 {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void send(){
        HashMap<Object, Object> map4 = new HashMap<>();
        map4.put("product_id", "4");
        map4.put("count", 4);
        map4.put("con_time", 4);
        map4.put("type", 4);
        rabbitTemplate.convertAndSend("delayed_exchange","routinkey",map4,message -> {
            message.getMessageProperties().setDelay(20000);
            log.info("发送，当前时间{}，消息为{}", new Date(),"4");
            return message;
        });

        HashMap<Object, Object> map1 = new HashMap<>();
        map1.put("product_id", "1");
        map1.put("count", 1);
        map1.put("con_time", 1);
        map1.put("type", 1);
        rabbitTemplate.convertAndSend("delayed_exchange","routinkey",map1,message -> {
            message.getMessageProperties().setDelay(1000);
            log.info("发送，当前时间{}，消息为{}", new Date(),"1");
            return message;
        });

        HashMap<Object, Object> map3 = new HashMap<>();
        map3.put("product_id", "3");
        map3.put("count", 3);
        map3.put("con_time", 3);
        map3.put("type", 3);
        rabbitTemplate.convertAndSend("delayed_exchange","routinkey",map3,message -> {
            message.getMessageProperties().setDelay(10000);
            log.info("发送，当前时间{}，消息为{}", new Date(),"3");
            return message;
        });

        HashMap<Object, Object> map2 = new HashMap<>();
        map2.put("product_id", "2");
        map2.put("count", 2);
        map2.put("con_time", 2);
        map2.put("type", 2);
        rabbitTemplate.convertAndSend("delayed_exchange","routinkey",map2,message -> {
            message.getMessageProperties().setDelay(5000);
            log.info("发送，当前时间{}，消息为{}", new Date(),"2");
            return message;
        });
    }


    @Test
    void test2(){
        // 删除delayed_queue队列中指定的消息
    }
}
