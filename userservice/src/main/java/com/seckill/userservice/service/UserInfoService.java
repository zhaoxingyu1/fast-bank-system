package com.seckill.userservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.consts.PageConst;
import com.seckill.common.entity.user.RoleEntity;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.entity.user.UserInfoEntity;
import com.seckill.userservice.dao.UserInfoDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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

    public UserInfoEntity selectUserInfoById(String userInfoId){

        UserInfoEntity userInfoEntity = userInfoDao.selectById(userInfoId);
        return userInfoEntity;
    }

    /**
     * 分页查询信用状态的的用户
     * @param creditStatus
     * @param current
     * @return
     */
    public Page<UserInfoEntity> selectUserInfoByCreditStatus(Integer creditStatus,Integer current){

        Page<UserInfoEntity> page = new Page<>(current - 1, PageConst.PageSize);

        QueryWrapper<UserInfoEntity> wrapper = new QueryWrapper<>();
        wrapper
                .eq("credit_status",creditStatus);

        Page<UserInfoEntity> userInfoEntityPage = userInfoDao.selectPage(page,wrapper);
        return userInfoEntityPage;
    }

    /**
     * 根据真实名字查询个人信息
     * @return
     */
    public Page<UserInfoEntity> selectAllByInfo(String info,Integer current){

        QueryWrapper<UserInfoEntity> wrapper = new QueryWrapper<>();

        wrapper
                .like("real_name",info)
                .or()
                .like("nickname",info)
                .or()
                .like("phone",info)
                .or()
                .like("email",info);

        Page<UserInfoEntity> page = new Page<>(current - 1, PageConst.PageSize);

        Page<UserInfoEntity> userInfoEntityPage = userInfoDao.selectPage(page,wrapper);

        return userInfoEntityPage;
    }


}
