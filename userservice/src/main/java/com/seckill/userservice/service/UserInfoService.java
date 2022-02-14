package com.seckill.userservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seckill.common.entity.user.UserInfoEntity;
import com.seckill.userservice.dao.UserInfoDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zxy
 * @Classname UserInfoService
 * @Date 2022/2/11 14:53
 */
@Service
public class UserInfoService {

    @Resource
    private UserInfoDao userInfoDao;

    public Boolean updateUserInfo(UserInfoEntity userInfo){
        int i = userInfoDao.updateById(userInfo);
        if (i > 0) {
            return true;
        } else {
            return false;
        }
    }

    public UserInfoEntity selectUserInfoByName(String name){
        QueryWrapper<UserInfoEntity> wrapper = new QueryWrapper<>();
        wrapper
                .eq("real_name",name);
        UserInfoEntity userInfoEntity = userInfoDao.selectOne(wrapper);
        return userInfoEntity;
    }


}
