package com.seckill.common.feign;

import com.seckill.common.entity.product.ProductTypeEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: 七画一只妖
 * @Date: 2022-03-25 13:44
 */
@FeignClient("productservice")
public interface ProductClient {

    @GetMapping("/{type}/getbyid/{id}")
    String getById(@PathVariable("id")String type, @PathVariable("id")String id);

    @PostMapping("/getProductsBatch")
    String getProductsBatch(@RequestParam("ids") List<ProductTypeEntity> ids);
}
