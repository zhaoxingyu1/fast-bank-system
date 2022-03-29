package com.seckill.common.feign;

import com.seckill.common.entity.product.BaseProduct;
import com.seckill.common.entity.product.ProductTypeEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: 七画一只妖
 * @Date: 2022-03-25 13:44
 */
@FeignClient("productservice")
public interface ProductClient {

    @GetMapping("product/getbyid/{id}")
    BaseProduct getById(@PathVariable("id") String id);

    @PostMapping("product/getProductsBatch")
    List<BaseProduct> getProductsBatch(@RequestBody List<String> ids);

    // 传入一个id，返回对应的产品类型
    @GetMapping("product/getTypeById/{id}")
    String getProductType(@PathVariable("id") String id);
}
