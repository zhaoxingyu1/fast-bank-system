package com.seckill.userservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.consts.PageConst;
import com.seckill.common.entity.product.LoanProductEntity;
import com.seckill.common.entity.user.UserApplicationRecordEntity;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.feign.ProductClient;
import com.seckill.common.utils.RiskControl;
import com.seckill.userservice.dao.UserApplicationRecordDao;
import com.seckill.userservice.dao.UserInfoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zxy
 * @Classname UserApplicationRecordService
 * @Date 2022/3/25 17:31
 */
@Service
public class UserApplicationRecordService {

    @Resource
    private UserInfoDao userInfoDao;
    @Autowired
    ProductClient productClient;

    @Resource
    private UserApplicationRecordDao userApplicationRecordDao;


    public Object insert(UserEntity user, String productId, RiskControl riskControl) {

        UserApplicationRecordEntity userApplicationRecord = new UserApplicationRecordEntity();

        userApplicationRecord.setUsername(user.getUsername());
        userApplicationRecord.setAge(user.getUserInfo().getAge());
        userApplicationRecord.setWorkingState(user.getUserInfo().getWorkingState());
        userApplicationRecord.setOverdueNumber(user.getUserInfo().getOverdueNumber());
        userApplicationRecord.setThroughState(riskControl.getThroughState());
        userApplicationRecord.setCreditStatus(user.getUserInfo().getCreditStatus());
        userApplicationRecord.setCause(riskControl.getCause());
        userApplicationRecord.setProductId(productId);

        LoanProductEntity loanProduct = productClient.getById(productId);

        userApplicationRecord.setProductName(loanProduct.getLoanProductName());

        // 默认需要控制风险的只有贷款产品
        userApplicationRecord.setProductType("loan");
        int bool = userApplicationRecordDao.insert(userApplicationRecord);

        if (bool > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean deleteById(String id) {

        int bool = userApplicationRecordDao.deleteById(id);

        if (bool > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean updateById(UserApplicationRecordEntity userApplicationRecord) {

        int bool = userApplicationRecordDao.updateById(userApplicationRecord);
        if (bool > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param name
     * @param current
     * @return
     */
    public Page<UserApplicationRecordEntity> selectAllOrByLikeNamePage(String name, Integer day, Integer current) {

        QueryWrapper<UserApplicationRecordEntity> wrapper = new QueryWrapper<>();
        Page<UserApplicationRecordEntity> page = new Page<>(current - 1, PageConst.PageSize);
        Page<UserApplicationRecordEntity> userApplicationRecordEntityPage;
        Long time1 = null;
        if(day!=null){
            time1 = System.currentTimeMillis() + day.longValue();
        }


        if (name == null) {
            userApplicationRecordEntityPage = userApplicationRecordDao.selectPage(page, null);
        } else {
            Long finalTime = time1;
            wrapper
                    .like("username", name)
                    .or()
                    .like("product_name", name)
                    .func(i -> {
                        if (day!=null && !day.equals(0)) i.lt("ctime", finalTime);
                    });
            userApplicationRecordEntityPage = userApplicationRecordDao.selectPage(page, wrapper);
        }

        return userApplicationRecordEntityPage;
    }

    /**
     * 根据多少天以内将记录顺寻查询；
     *
     * @param day
     * @param current
     * @return
     */
    public Page<UserApplicationRecordEntity> selectByUserName(String username, Integer day, Integer current) {

        QueryWrapper<UserApplicationRecordEntity> wrapper = new QueryWrapper<>();
        Page<UserApplicationRecordEntity> page = new Page<>(current - 1, PageConst.PageSize);


        if(day==null){
            day = 0 ;
        }
        Long time1 = System.currentTimeMillis() + day.longValue();


        wrapper
                .eq("username", username)
                .lt("ctime",time1)
                .orderByAsc("ctime");
        Page<UserApplicationRecordEntity> userApplicationRecordEntityPage = userApplicationRecordDao.selectPage(page, wrapper);

        return userApplicationRecordEntityPage;
    }


}
