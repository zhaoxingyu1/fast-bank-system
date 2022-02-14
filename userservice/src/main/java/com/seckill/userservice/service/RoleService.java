package com.seckill.userservice.service;

import com.seckill.common.entity.user.RoleEntity;

import com.seckill.userservice.dao.RoleDao;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author zxy
 * @Classname RoleService
 * @Date 2022/2/11 14:47
 */
@Service
public class RoleService {

    @Resource
    private RoleDao roleDao;

    public Boolean insertUserRole(String role) {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRole(role);
        int i = roleDao.insert(roleEntity);
        if (i > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean deleteRoleById(String roleId){
        int i = roleDao.deleteById(roleId);
        if (i > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean updateUserRoleById(RoleEntity roleEntity){
        int i = roleDao.updateById(roleEntity);

        if (i > 0) {
            return true;
        } else {
            return false;
        }
    }


}
