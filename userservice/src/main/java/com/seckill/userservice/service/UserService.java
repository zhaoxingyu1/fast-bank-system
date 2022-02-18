package com.seckill.userservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.entity.user.RoleEntity;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.entity.user.UserInfoEntity;
import com.seckill.common.entity.user.UserProductEntity;
import com.seckill.userservice.dao.RoleDao;
import com.seckill.userservice.dao.UserDao;
import com.seckill.userservice.dao.UserInfoDao;
import com.seckill.userservice.dao.UserProductDao;
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
    private UserProductDao userProductDao;

    @Transactional
    public Boolean insertUser(UserEntity user, UserInfoEntity userInfo, RoleEntity role) {

        try {
            userInfoDao.insert(userInfo);
            roleDao.insert(role);

            user.setUserInfoId(userInfo.getUserInfoId());
            user.setRoleId(role.getRoleId());
            userDao.insert(user);

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
            String userProductId = user.getUserProductId();
            UserInfoEntity userInfoEntity = userInfoDao.selectById(userInfoId);

            int i = userDao.deleteById(userId);

            int i1 = userInfoDao.deleteById(userInfoId);

            int i2 = roleDao.deleteById(roleId);

            int i3 = userProductDao.deleteById(userProductId);

            if(i<1 && i1<1 && i2<1 && i3<1){
                new Exception();
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Transactional
    public Boolean updateUserById(UserEntity user) {

        int i = userDao.updateById(user);
        if(i>0){
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
     * @param size
     * @return
     */
    public Page<UserEntity> selectAllUser(Integer current, Integer size) {

        Page<UserEntity> page = new Page<>(current - 1, size);

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
    public Page<UserEntity> selectUserListByName(String name, Integer current, Integer size) {

        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper
                .like("username", name);

        Page<UserEntity> page = new Page<>(current - 1, size);

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


}
