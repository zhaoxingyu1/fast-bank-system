//package com.seckill.productservice.queue;
//
//import com.rabbitmq.client.Channel;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//
//@Slf4j
//@Component
//public class QueueConsumer {
//    /**
//     *
//     */
//    @RabbitListener(queues = "delayed_queue")
//    void receiveDelayed(Message msg){
//        String str = new String(msg.getBody());
//        log.info("收到延迟队列消息，当前时间{}，消息为{}", new Date(),str);
//    }
//}
