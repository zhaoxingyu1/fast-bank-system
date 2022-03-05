package com.seckill.userservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.entity.user.UserProductEntity;
import com.seckill.userservice.dao.UserProductDao;
import org.apache.tomcat.util.http.parser.Host;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zxy
 * @Classname UserProductService
 * @Date 2022/2/18 19:42
 */
@Service
public class UserProductService {

    @Resource
    private UserProductDao userProductDao;

    public Boolean insertUserProduct(UserProductEntity userProductEntity) {
        int i = userProductDao.insert(userProductEntity);
        if (i > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean deleteUserProductById(String userProductId) {
        int i = userProductDao.deleteById(userProductId);
        if (i > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean updateUserProductById(UserProductEntity userProductEntity){
        int i = userProductDao.updateById(userProductEntity);
        if (i > 0) {
            return true;
        } else {
            return false;
        }
    }

    public UserProductEntity selectUserProductById(String userProductId){
        UserProductEntity userProductEntity = userProductDao.selectById(userProductId);
        return userProductEntity;
    }


    /**
     * 分页查询用户预约的所有产品
     * @param current
     * @param size
     * @return
     */
    public Page<UserProductEntity> selectAllUserProduct(String userId, Integer current, Integer size){

        QueryWrapper<UserProductEntity> wrapper = new QueryWrapper<>();
        wrapper
                .eq("user_id", userId);

        Page<UserProductEntity> page = new Page<>(current - 1, size);

        Page<UserProductEntity> userEntityPage = userProductDao.selectPage(page, wrapper);

        return userEntityPage;
    }

    /**
     * 根据用户id分页查询模糊查询用户预约的产品
     * @param current
     * @param size
     * @return
     */
    public Page<UserProductEntity> selectUserProductListByName(String userId,String productName, Integer current, Integer size){

        QueryWrapper<UserProductEntity> wrapper = new QueryWrapper<>();
        wrapper
                .like("product_name", productName)
                .eq("user_id", userId);

        Page<UserProductEntity> page = new Page<>(current - 1, size);

        Page<UserProductEntity> userEntityPage = userProductDao.selectPage(page, wrapper);

        return userEntityPage;
    }

    /**
     * 用户查看预约产品
     * @param current
     * @param size
     * @return
     */
    public Page<UserProductEntity> selectUserProductByState(String userId,Integer buyState, Integer current, Integer size){

        QueryWrapper<UserProductEntity> wrapper = new QueryWrapper<>();
        wrapper
                .eq("user_id", userId)
                .eq("buy_state",buyState);

        Page<UserProductEntity> page = new Page<>(current - 1, size);
        Page<UserProductEntity> userEntityPage = userProductDao.selectPage(page, wrapper);

        
        return userEntityPage;
    }



}


