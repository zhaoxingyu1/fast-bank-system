package com.seckill.productservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seckill.common.entity.product.FinancialProductEntity;
import com.seckill.common.entity.product.LoanProductEntity;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.entity.user.UserProductEntity;
import com.seckill.common.exception.DatabaseOperationException;
import com.seckill.common.exception.NotFoundException;
import com.seckill.productservice.dao.FinancialProductDao;
import com.seckill.productservice.dao.LoanProductDao;
import com.seckill.productservice.dao.UserProductDao;
import com.seckill.productservice.service.IUserProductService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: 七画一只妖
 * @Date: 2022/3/12 12:30
 */
@Service
@Transactional
public class UserProductService implements IUserProductService {
    @Resource
    private UserProductDao userProductDao;

    @Resource
    private RedisTemplate<String, Object> redis;

    @Resource
    private LoanProductDao loanProductDao;

    @Resource
    private FinancialProductDao financialProductDao;

    @Override
    public Boolean userAppointProduct(String userId, String type, String productId) throws Exception{
        if(type.equals("financial")){
            FinancialProductEntity financialProductEntity = financialProductDao.selectById(productId);
            // 更新状态
            UserProductEntity i = new UserProductEntity();
            i.setUserId(userId);
            i.setUserProductId(financialProductEntity.getFinancialProductId());
            i.setProductName(financialProductEntity.getFinancialProductName());
            i.setMtime(System.currentTimeMillis());
            i.setBookingStatus(1);
            i.setPrice(financialProductEntity.getPrice());
            int insert = userProductDao.insert(i);
            if (insert == 0){
                throw new DatabaseOperationException("添加产品失败");
            }else{
                return true;
            }
        }else if(type.equals("loan")){
            LoanProductEntity loanProductEntity = loanProductDao.selectById(productId);
            // 更新状态
            UserProductEntity i = new UserProductEntity();
            i.setUserId(userId);
            i.setUserProductId(loanProductEntity.getLoanProductId());
            i.setProductName(loanProductEntity.getLoanProductName());
            i.setMtime(System.currentTimeMillis());
            i.setBookingStatus(1);
            i.setPrice(loanProductEntity.getPrice());
            int insert = userProductDao.insert(i);
            if (insert == 0){
                throw new DatabaseOperationException("添加产品失败");
            }else{
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean userBuyProduct(String userId, String type, String productId) throws Exception {
        ValueOperations<String, Object> ops = redis.opsForValue();
        Integer count = (Integer) ops.get(productId);
        if (count != null && count > 0){
            // 用户购买成功，缓存减一
            Integer newCount = count - 1;
            ops.set(productId,newCount);
        }else {
            throw new NotFoundException("未找到这个产品");
        }

        // todo 用户抢购成功，调用订单模块生成订单
        if(type.equals("financial")){
        }else if(type.equals("loan")){
        }
        return null;
    }

    @Override
    public List<UserProductEntity> userGetAppointment(String userId) {
        QueryWrapper<UserProductEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        return userProductDao.selectList(queryWrapper);
    }

    @Override
    public List<UserProductEntity> adminGetUserByProductId(String productId) {
        QueryWrapper<UserProductEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_product_id",productId);
        return userProductDao.selectList(queryWrapper);
    }

    @Override
    public Boolean adminDeleteAppointByUserId(String productId, String userId) throws Exception{
        QueryWrapper<UserProductEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("user_product_id",productId);
        List<UserProductEntity> userProductEntities = userProductDao.selectList(queryWrapper);
        if (userProductEntities.size() != 0){
            int delete = userProductDao.delete(queryWrapper);
            if (delete == 0){
                throw new DatabaseOperationException("删除预约失败");
            }else{
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean userIsAppoint(String userId, String productId) {
        QueryWrapper<UserProductEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("user_product_id",productId);
        List<UserProductEntity> userProductEntities = userProductDao.selectList(queryWrapper);
        if (userProductEntities.size() != 0){
            return true;
        } else {
            return false;
        }
    }
}
