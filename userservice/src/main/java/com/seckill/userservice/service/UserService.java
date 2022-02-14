package com.seckill.userservice.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.entity.user.CreditEntity;
import com.seckill.common.entity.user.RoleEntity;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.entity.user.UserInfoEntity;
import com.seckill.userservice.dao.CreditDao;
import com.seckill.userservice.dao.RoleDao;
import com.seckill.userservice.dao.UserDao;
import com.seckill.userservice.dao.UserInfoDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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
    @Resource
    private CreditDao creditDao;


    @Transactional
    public Boolean insertUser(UserEntity user, UserInfoEntity userInfo, RoleEntity role) {

        try {
            userDao.insert(user);
            userInfoDao.insert(userInfo);
            roleDao.insert(role);
            CreditEntity creditEntity = new CreditEntity();
            creditEntity.setUserId(user.getUserId());
            creditDao.insert(creditEntity);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Transactional
    public Boolean deleteUserById(String userId) {
        try {
            UserEntity user = userDao.selectById(userId);
            String userInfoId = user.getUserInfoId();
            String roleId = user.getRoleId();

            UserInfoEntity userInfoEntity = userInfoDao.selectById(userInfoId);
            String creditId = userInfoEntity.getCreditId();


            userDao.deleteById(userId);
            userInfoDao.deleteById(userInfoId);
            roleDao.deleteById(roleId);
            creditDao.deleteById(creditId);

        }catch (Exception e){
            return false;
        }
        return true;
    }

    public Boolean updateUserById(UserEntity user) {
        int i = userDao.updateById(user);
        if (i > 0) {
            return true;
        } else {
            return false;
        }
    }

    public UserEntity selectUserById(String userId){

        UserEntity userEntity = userDao.selectById(userId);

        UserInfoEntity userInfoEntity = userInfoDao.selectById(userEntity.getUserInfoId());
        RoleEntity roleEntity = roleDao.selectById(userEntity.getRoleId());

        userEntity.setUserInfo(userInfoEntity);
        userEntity.setUserRole(roleEntity);

        return userEntity;
    }

    /**
     * 分页获取全部用户
     * @param current
     * @param size
     * @return
     */
    public Page<UserEntity> selectAllUser(Integer current,Integer size){

        Page<UserEntity> page = new Page<>(current - 1, size);

        Page<UserEntity> userEntityPage = userDao.selectPage(page,null);

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
     * 根据名字模糊查询
     * @return
     */
    public Page<UserEntity> selectUserListByName(String name,Integer current,Integer size){

        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper
                .like("username",name);

        Page<UserEntity> page = new Page<>(current - 1, size);

        Page<UserEntity> userEntityPage = userDao.selectPage(page,wrapper);

        List<UserEntity> userEntities = userEntityPage.getRecords();

        for (UserEntity userEntity : userEntities) {

            UserInfoEntity userInfoEntity = userInfoDao.selectById(userEntity.getUserInfoId());
            RoleEntity roleEntity = roleDao.selectById(userEntity.getRoleId());

            userEntity.setUserInfo(userInfoEntity);
            userEntity.setUserRole(roleEntity);
        }

        return userEntityPage;
    }



}
