import com.seckill.productservice.ProductApplication;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;

/**
 * @Author: 七画一只妖
 * @Date: 2022-04-02 13:54
 */
@SpringBootTest(classes = ProductApplication.class)
public class RabbitmqTest {
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    public void send() {
//        rabbitTemplate.convertAndSend("delayed_exchange", "delayed_routingKey","我是消息1", message -> {
//            message.getMessageProperties().setExpiration("20000");
//            System.out.println("成功发出消息1" + new Date());
//            return message;
//        });
//        rabbitTemplate.convertAndSend("delayed_exchange", "delayed_routingKey","我是消息2", message -> {
//            message.getMessageProperties().setExpiration("5000");
//            System.out.println("成功发出消息2" + new Date());
//            return message;
//        });
        HashMap<Object, Object> map1 = new HashMap<>();
        map1.put("name", "我是消息1");
        HashMap<Object, Object> map2 = new HashMap<>();
        map2.put("name", "我是消息2");
        rabbitTemplate.convertAndSend("delayProductQueue",map1, message -> {
            message.getMessageProperties().setExpiration("20000");
            System.out.println("成功发出消息1" + new Date());
            return message;
        });
        rabbitTemplate.convertAndSend("delayProductQueue",map2, message -> {
            message.getMessageProperties().setExpiration("5000");
            System.out.println("成功发出消息2" + new Date());
            return message;
        });
    }
}
