package com.seckill.userservice.service;

import com.seckill.userservice.dao.CreditDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zxy
 * @Classname CreditService
 * @Date 2022/2/15 2:04
 */
@Service
public class CreditService {

    @Resource
    private CreditDao creditDao;



}
