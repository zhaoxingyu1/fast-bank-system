package com.seckill.productservice.queue;

import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.feign.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 七画一只妖
 * @Date: 2022-03-31 21:03
 */
@Component
public class SendUserEmail {
    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender mailSender;

    @Resource
    private UserClient userClient;

    public void sendEmail(String email) throws MailException {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);

        message.setTo(email);

        message.setSubject("秒杀即将开始");
        String text="您预约的产品还有五分钟就要进行秒杀了，请尽快做好准备，以免被抢光哦！";

        message.setText(text);

        mailSender.send(message);
    }

    /**
     * 根据用户ID查询邮箱，然后发送邮件
     */
    public Boolean userSendEmail(String userId) throws Exception {
        UserEntity userEntity = userClient.selectUserById(userId);
        String email = userEntity.getUserInfo().getEmail();
        return null;
        //todo 还没写完，我先交了
    }
}
