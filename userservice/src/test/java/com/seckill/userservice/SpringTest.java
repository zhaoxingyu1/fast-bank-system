package com.seckill.userservice;

import com.seckill.common.entity.user.RiskControlEntity;
import com.seckill.userservice.dao.RiskControlDao;
import com.seckill.userservice.service.UserApplicationRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author zxy
 * @Classname SpringTest
 * @Date 2022/3/31 22:20
 */
@SpringBootTest
public class SpringTest {

    @Resource
    private RiskControlDao riskControlDao;
    @Resource
    UserApplicationRecordService userApplicationRecordService;

    @Test
    void name() {

//        System.out.println(userApplicationRecordService.selectAllOrByLikeNamePage("z", 1).getRecords());
        System.out.println(System.currentTimeMillis());
//        System.out.println(new Date(2022,4,1,12,30).getTime());
    }

    @Test
    void test() {
        RiskControlEntity riskControlEntity = riskControlDao.selectOne(null);
        System.out.println(riskControlEntity);
    }
}
