package com.seckill.common.utils;

import com.seckill.common.consts.EncryptionConsts;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zxy
 * @Description 生成摘要
 * @Date 2022/3/11 20:48
 */
public class DigestsUtil {

    /**
     * @Description sha1方法
     * @param input 需要散列字符串
     * @param
     * @return
     */
    public static String sha1(String input) {
       return new SimpleHash(EncryptionConsts.HASH_ALGORITHM, input,EncryptionConsts.HASH_INTERATIONS).toString();
    }

    /**
     * @Description 随机获得salt字符串  ==============未用 （省略盐值加密）============================
     * @return
     */
    public static String generateSalt(){
        SecureRandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
        return randomNumberGenerator.nextBytes().toHex();
    }


    /**
     * @Description 生成密码字符密文
     * @param
     * @return
     */
    public static Map<String,String> entryptPassword(String passwordPlain) {
       Map<String,String> map = new HashMap<>();
       String password =sha1(passwordPlain);
       map.put("password", password);
       return map;
    }
}
