package com.seckill.common.feign;

import com.seckill.common.entity.product.BaseProduct;
import com.seckill.common.entity.product.LoanProductEntity;
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
    LoanProductEntity getById(@PathVariable("id") String id);

    @PostMapping("product/getProductsBatch")
    List<BaseProduct> getProductsBatch(@RequestBody List<String> ids);

    // 传入一个id，返回对应的产品类型
    @GetMapping("product/getTypeById/{id}")
    String getProductType(@PathVariable("id") String id);

    //传一个用户id和一个产品id，返回一个Boolean，判断用户是否预约这个产品
    @GetMapping("product/userProduct/isReserved/{userId}/{productId}")
    Boolean isReserved(@PathVariable("userId") String userId,
                       @PathVariable("productId") String productId);

    //传入一个贷款产品ID，对其库存减一
    @GetMapping("product/userProduct/userProductStock/{productId}")
    Object loanProductStockReduce(@PathVariable("productId")String productId);
}
