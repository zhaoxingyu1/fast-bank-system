package com.seckill.productservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seckill.common.entity.product.FinancialProductEntity;
import com.seckill.common.entity.product.LoanProductEntity;
import com.seckill.common.entity.product.ProductTypeEntity;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.entity.user.UserProductEntity;
import com.seckill.common.exception.DatabaseOperationException;
import com.seckill.common.exception.NotFoundException;
import com.seckill.common.feign.UserClient;
import com.seckill.productservice.dao.FinancialProductDao;
import com.seckill.productservice.dao.LoanProductDao;
import com.seckill.productservice.dao.ProductTypeDao;
import com.seckill.productservice.dao.UserProductDao;
import com.seckill.productservice.response.AdminGetAppointment;
import com.seckill.productservice.response.UserGetAppointmentProduct;
import com.seckill.productservice.service.IUserProductService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    @Resource
    private ProductTypeDao productTypeDao;

    @Resource
    private UserClient userClient;

    @Override
    public Boolean userAppointProduct(String userId, String type, String productId) throws Exception{
        // 先判断在user_product表中是否有符合user_id和product_id的记录
        UserProductEntity userProductEntity = userProductDao.selectOne(new QueryWrapper<UserProductEntity>()
                .eq("user_id", userId)
                .eq("product_id", productId));
        if (userProductEntity != null){
            // 记录存在，只需要更新booking_status为1即可
            userProductEntity.setBookingStatus(1);
            int update = userProductDao.updateById(userProductEntity);
            if (update == 0){
                throw new DatabaseOperationException("预约失败，可能是数据库操作失败");
            }
        }

        // 没有记录，则需要新增一条记录
        if(type.equals("financial")){
            FinancialProductEntity financialProductEntity = financialProductDao.selectById(productId);
            // 更新状态
            UserProductEntity i = new UserProductEntity();
            i.setUserId(userId);
            i.setProductId(financialProductEntity.getFinancialProductId());
            i.setProductName(financialProductEntity.getFinancialProductName());
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
            i.setProductId(loanProductEntity.getLoanProductId());
            i.setProductName(loanProductEntity.getLoanProductName());
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
    public Boolean userCancelAppointment(String userId, String type, String productId) throws Exception {
        // 在user_product表中查询是否有user_id和product_id都符合的记录
        UserProductEntity userProductEntity = userProductDao.selectOne(new QueryWrapper<UserProductEntity>()
                .eq("user_id", userId)
                .eq("product_id", productId));
        if (userProductEntity == null){
            throw new NotFoundException("未找到相关记录，可能是这个用户不存在或者产品不存在");
        }else {
            // 判断是否已经预约
            if (userProductEntity.getBookingStatus() == 1){
                // 将数据库钟这条记录的booking_status改为0
                userProductEntity.setBookingStatus(0);
                int update = userProductDao.updateById(userProductEntity);
                if (update == 0){
                    throw new DatabaseOperationException("取消预约失败，可能是数据库操作失败");
                }
            }else {
                throw new NotFoundException("该用户没有预约这个产品");
            }
        }
        return true;
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
    public List<Object> userGetAppointment(String userId) throws Exception {
        QueryWrapper<UserProductEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId)
                    .eq("booking_status",1);
        List<UserProductEntity> userProductEntities = userProductDao.selectList(queryWrapper);
        List<Object> responseList = new ArrayList<>();
        for (UserProductEntity userProductEntity : userProductEntities){
            // 获取产品类型
            String type = productTypeDao.selectOne(new QueryWrapper<ProductTypeEntity>()
                    .eq("product_id",userProductEntity.getProductId())).getType();
            if (type.equals("financial")){
                // 获取理财产品信息
                FinancialProductEntity financialProductEntity = financialProductDao.selectOne(new QueryWrapper<FinancialProductEntity>()
                        .eq("financial_product_id",userProductEntity.getProductId()));
                UserGetAppointmentProduct<FinancialProductEntity> userGetAppointmentProduct = new UserGetAppointmentProduct<>();
                userGetAppointmentProduct.setUserProductEntity(userProductEntity);
                userGetAppointmentProduct.setProductEntity(financialProductEntity);

                responseList.add(userGetAppointmentProduct);
            }else if (type.equals("loan")){
                // 获取贷款产品信息
                LoanProductEntity loanProductEntity = loanProductDao.selectOne(new QueryWrapper<LoanProductEntity>()
                        .eq("loan_product_id",userProductEntity.getProductId()));
                UserGetAppointmentProduct<LoanProductEntity> userGetAppointmentProduct = new UserGetAppointmentProduct<>();
                userGetAppointmentProduct.setUserProductEntity(userProductEntity);
                userGetAppointmentProduct.setProductEntity(loanProductEntity);

                responseList.add(userGetAppointmentProduct);
            }else{
                throw new NotFoundException("未找到该产品");
            }
        }
        return responseList;
    }

    @Override
    public List<AdminGetAppointment> adminGetUserByProductId(String productId) {
        QueryWrapper<UserProductEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id",productId);
        List<UserProductEntity> userProductEntities = userProductDao.selectList(queryWrapper);
        List<AdminGetAppointment> adminGetAppointmentList = new ArrayList<>();
        for (UserProductEntity userProductEntity : userProductEntities){
            AdminGetAppointment adminGetAppointment = new AdminGetAppointment();
            // 获取用户信息，远程调用
            UserEntity userEntity = userClient.selectUserById(userProductEntity.getUserId());
            adminGetAppointment.setUserEntity(userEntity);
            adminGetAppointment.setUserProductEntity(userProductEntity);

            adminGetAppointmentList.add(adminGetAppointment);
        }

        return adminGetAppointmentList;
    }

    @Override
    public Boolean adminDeleteAppointByUserId(String productId, String userId) throws Exception{
        QueryWrapper<UserProductEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("product_id",productId);
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
        queryWrapper.eq("product_id",productId);
        List<UserProductEntity> userProductEntities = userProductDao.selectList(queryWrapper);
        if (userProductEntities.size() != 0){
            return true;
        } else {
            return false;
        }
    }
}
