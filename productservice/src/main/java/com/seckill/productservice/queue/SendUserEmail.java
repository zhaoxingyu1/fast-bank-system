package com.seckill.productservice.queue;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.entity.user.UserProductEntity;
import com.seckill.common.feign.UserClient;
import com.seckill.productservice.dao.UserProductDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

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

    @Resource
    private UserProductDao userProductDao;


    /**
     * 根据产品ID发送提醒邮件
     */
    public void sendEmailToUser(String productId) throws Exception {
        List<UserProductEntity> entities = getUserIdList(productId);
        for (UserProductEntity entity : entities) {
            String userId = entity.getUserId(); // 获得用户ID
            userSendEmail(userId); // 根据用户ID查询邮箱，然后发送邮件
        }
    }


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
    public void userSendEmail(String userId) throws Exception {
        UserEntity userEntity = userClient.selectUserById(userId);
        String email = userEntity.getUserInfo().getEmail();
        sendEmail(email);
    }

    /**
     * 根据产品ID查询已预约的人的ID列表
     */
    public List<UserProductEntity> getUserIdList(String productId) {
        // 查询user_product表中符合productId且status为1的数据
        return userProductDao.selectList(new QueryWrapper<>(new UserProductEntity())
        .eq("product_id", productId)
        .eq("status", 1));
    }
}
