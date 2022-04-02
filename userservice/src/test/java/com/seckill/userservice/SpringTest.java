package com.seckill.userservice;

import com.seckill.common.entity.user.RiskControlEntity;
import com.seckill.userservice.dao.RiskControlDao;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author zxy
 * @Classname SpringTest
 * @Date 2022/3/31 22:20
 */
//@SpringBootTest
public class SpringTest {

    @Resource
    private RiskControlDao riskControlDao;

    @Test
    void name() {

        System.out.println(new Date(2022,4,1,12,30).getTime());
    }
}
