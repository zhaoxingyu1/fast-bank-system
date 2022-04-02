package com.seckill.userservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.consts.PageConst;
import com.seckill.common.entity.user.RoleEntity;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.entity.user.UserInfoEntity;
import com.seckill.common.entity.user.UserProductEntity;
import com.seckill.common.exception.DatabaseOperationException;
import com.seckill.userservice.dao.RoleDao;
import com.seckill.userservice.dao.UserDao;
import com.seckill.userservice.dao.UserInfoDao;
import com.seckill.userservice.dao.UserProductDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zxy
 * @Classname UserService
 * @Date 2022/2/11 14:42
 */
@Service
public class UserService {

    @Resource
    private UserDao userDao;
    @Resource
    private UserInfoDao userInfoDao;
    @Resource
    private RoleDao roleDao;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Resource
    private RedisTemplate<String, Object> redis;


    // 待修改
    @Transactional
    public Boolean insertUser(UserEntity user, UserInfoEntity userInfo, RoleEntity role) throws Exception {

        userInfo.setCreditStatus(1);
        int i = userInfoDao.insert(userInfo);
        int i1 = roleDao.insert(role);

        user.setUserInfoId(userInfo.getUserInfoId());
        user.setRoleId(role.getRoleId());

        int i2 = userDao.insert(user);

        if (i < 1 && i1 < 1 && i2 < 1) {
            new DatabaseOperationException("插入失败");
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserById(String userId) throws Exception {

        UserEntity user = userDao.selectById(userId);
        String userInfoId = user.getUserInfoId();
        String roleId = user.getRoleId();


        int i = userDao.deleteById(userId);

        int i1 = userInfoDao.deleteById(userInfoId);

        int i2 = roleDao.deleteById(roleId);


        if (i < 1 && i1 < 1 && i2 < 1) {
            new DatabaseOperationException("删除失败");
        }
        return true;
    }

    @Transactional
    public Boolean updateUserById(UserEntity user) {

        int i = userDao.updateById(user);
        if (i > 0) {
            return true;
        }
        return false;
    }

    public UserEntity selectUserById(String userId) {

        UserEntity userEntity = userDao.selectById(userId);

        UserInfoEntity userInfoEntity = userInfoDao.selectById(userEntity.getUserInfoId());
        RoleEntity roleEntity = roleDao.selectById(userEntity.getRoleId());

        userEntity.setUserInfo(userInfoEntity);
        userEntity.setUserRole(roleEntity);

        return userEntity;
    }

    /**
     * 分页获取全部用户
     *
     * @param current
     * @return
     */
    public Page<UserEntity> selectAllUser(Integer current) {

        Page<UserEntity> page = new Page<>(current - 1, PageConst.PageSize);

        Page<UserEntity> userEntityPage = userDao.selectPage(page, null);

        List<UserEntity> userEntities = userEntityPage.getRecords();

        for (UserEntity userEntity : userEntities) {

            UserInfoEntity userInfoEntity = userInfoDao.selectById(userEntity.getUserInfoId());
            RoleEntity roleEntity = roleDao.selectById(userEntity.getRoleId());

            userEntity.setUserInfo(userInfoEntity);
            userEntity.setUserRole(roleEntity);
        }
        userEntityPage.setRecords(userEntities);
        return userEntityPage;
    }

    /**
     * 根据名字模糊查询用户
     *
     * @return
     */
    public Page<UserEntity> selectUserListByName(String name, Integer current) {

        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper
                .like("username", name);
        Page<UserEntity> page = new Page<>(current - 1, PageConst.PageSize);

        Page<UserEntity> userEntityPage = userDao.selectPage(page, wrapper);

        List<UserEntity> userEntities = userEntityPage.getRecords();

        for (UserEntity userEntity : userEntities) {

            UserInfoEntity userInfoEntity = userInfoDao.selectById(userEntity.getUserInfoId());
            RoleEntity roleEntity = roleDao.selectById(userEntity.getRoleId());

            userEntity.setUserInfo(userInfoEntity);
            userEntity.setUserRole(roleEntity);
        }

        return userEntityPage;
    }

    public UserEntity selectUserByUsername(String name) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();

        wrapper
                .eq("username", name);
        UserEntity userEntity = userDao.selectOne(wrapper);

        if (userEntity == null) {
            return null;
        }

        UserInfoEntity userInfoEntity = userInfoDao.selectById(userEntity.getUserInfoId());
        RoleEntity roleEntity = roleDao.selectById(userEntity.getRoleId());

        userEntity.setUserInfo(userInfoEntity);
        userEntity.setUserRole(roleEntity);

        return userEntity;
    }


    public void sendEmail(String email) throws MailException {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);

        message.setTo(email);

        message.setSubject("三湘银行验证码");
        Integer code = 0;
        String text = "你的验证码为：";
        for (int i = 1; i <= 6; i++) {
            int random = (int) (Math.random() * 10);
            code = code * 10 + random;
        }
        text = text + code + "，此验证码过期时间为5分钟，请在有效时间内使用";
        message.setText(text);

        mailSender.send(message);

        // 5分钟的过期时间
        ValueOperations<String, Object> opsForValue = redis.opsForValue();
        opsForValue.set(email, code, 5, TimeUnit.MINUTES);

    }

    public UserEntity selectByUserInfoId(String userInfoId) {

        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();

        wrapper
                .eq("user_info_id", userInfoId);

        UserEntity user = userDao.selectOne(wrapper);
        return user;
    }

}
