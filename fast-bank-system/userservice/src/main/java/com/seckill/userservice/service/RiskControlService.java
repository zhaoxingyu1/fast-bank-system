package com.seckill.userservice.service;

import com.seckill.common.entity.user.RiskControlEntity;
import com.seckill.userservice.dao.RiskControlDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zxy
 * @Classname RiskControlService
 * @Date 2022/3/31 22:05
 */
@Service
public class RiskControlService {

    @Resource
    private RiskControlDao riskControlDao;

    public Boolean insertOrUpdate(RiskControlEntity riskControl){
        int bool=0;

        RiskControlEntity riskControlEntity = riskControlDao.selectOne(null);

        if (riskControlEntity!=null){
            riskControl.setRiskControlId(riskControlEntity.getRiskControlId());
            bool = riskControlDao.updateById(riskControl);
        }else{
            bool = riskControlDao.insert(riskControl);
        }

        if(bool>0){
            return true;
        }else{
            return false;
        }
    }

    public RiskControlEntity getRiskControl(){

        RiskControlEntity riskControlEntity = riskControlDao.selectOne(null);

        return riskControlEntity;
    }


}
