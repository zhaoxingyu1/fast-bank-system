package com.seckill.userservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.consts.PageConst;
import com.seckill.common.entity.user.UserApplicationRecordEntity;
import com.seckill.common.entity.user.UserEntity;
import com.seckill.common.entity.user.UserInfoEntity;
import com.seckill.common.enums.CodeEnum;
import com.seckill.common.response.DataFactory;
import com.seckill.common.response.SimpleData;
import com.seckill.common.utils.RiskControl;
import com.seckill.common.utils.RiskControlUtils;
import com.seckill.userservice.dao.UserApplicationRecordDao;
import com.seckill.userservice.dao.UserInfoDao;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author zxy
 * @Classname UserApplicationRecordService
 * @Date 2022/3/25 17:31
 */
@Service
public class UserApplicationRecordService {

    @Resource
    private UserInfoDao userInfoDao;

    @Resource
    private UserApplicationRecordDao userApplicationRecordDao;


    public Object insert(UserEntity user, String productName) {

        UserApplicationRecordEntity userApplicationRecord = new UserApplicationRecordEntity();

        RiskControl riskControl = RiskControlUtils.isQualified(user);


        userApplicationRecord.setUsername(user.getUsername());
        userApplicationRecord.setAge(user.getUserInfo().getAge());
        userApplicationRecord.setWorkingState(user.getUserInfo().getWorkingState());
        userApplicationRecord.setOverdueNumber(user.getUserInfo().getOverdueNumber());
        userApplicationRecord.setThroughState(riskControl.getThroughState());
        userApplicationRecord.setCreditStatus(user.getUserInfo().getCreditStatus());
        userApplicationRecord.setCause(riskControl.getCause());
        userApplicationRecord.setProductName(productName);
        // 默认需要控制风险的只有贷款产品
        userApplicationRecord.setProductType("loan");
        int bool = userApplicationRecordDao.insert(userApplicationRecord);

        if (bool > 0) {

            if(riskControl.getThroughState()==1){
                return DataFactory.success(SimpleData.class,"ok");
            }else{
                return DataFactory.fail(CodeEnum.FORBIDDEN,riskControl.getCause());
            }
        } else {
            return DataFactory.fail(CodeEnum.INTERNAL_ERROR,"出现未知错误,请联系管理员");
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
     *
     * @param name
     * @param current
     * @return
     */
    public Page<UserApplicationRecordEntity> selectAllOrByLikeNamePage(String name, Integer current) {

        QueryWrapper<UserApplicationRecordEntity> wrapper = new QueryWrapper<>();
        Page<UserApplicationRecordEntity> page = new Page<>(current - 1, PageConst.PageSize);
        Page<UserApplicationRecordEntity> userApplicationRecordEntityPage;
        if (name == null) {
            userApplicationRecordEntityPage = userApplicationRecordDao.selectPage(page, null);
        } else {
            wrapper
                    .like("username", name)
                    .like("product_name", name);
            userApplicationRecordEntityPage = userApplicationRecordDao.selectPage(page, wrapper);
        }
        return userApplicationRecordEntityPage;
    }
    /**
     *
     * 根据多少天以内将记录顺寻查询；
     * @param time
     * @param current
     * @return
     */
    public Page<UserApplicationRecordEntity> selectByTime(Long time, Integer current){

        QueryWrapper<UserApplicationRecordEntity> wrapper = new QueryWrapper<>();
        Page<UserApplicationRecordEntity> page = new Page<>(current - 1, PageConst.PageSize);

        wrapper
                .lt("ctime",new Date().getTime()+time)
                .orderByAsc("ctime");
        Page<UserApplicationRecordEntity> userApplicationRecordEntityPage = userApplicationRecordDao.selectPage(page, wrapper);

        return userApplicationRecordEntityPage;
    }



}
