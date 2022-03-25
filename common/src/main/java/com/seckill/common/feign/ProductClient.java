package com.seckill.common.feign;

import com.seckill.common.entity.product.ProductTypeEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: 七画一只妖
 * @Date: 2022-03-25 13:44
 */
@FeignClient("productservice")
public interface ProductClient {

    @GetMapping("/{type}/getbyid/{id}")
    String getById(@PathVariable("type")String type, @PathVariable("id")String id);

    @PostMapping("/getProductsBatch")
    String getProductsBatch(@RequestBody List<ProductTypeEntity> ids);
}
