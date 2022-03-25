package com.seckill.productservice.controller;

import com.seckill.common.consts.FeignConsts;
import com.seckill.common.entity.product.FinancialProductEntity;
import com.seckill.common.entity.product.LoanProductEntity;
import com.seckill.common.entity.product.ProductTypeEntity;
import com.seckill.common.enums.CodeEnum;
import com.seckill.common.response.DataFactory;
import com.seckill.common.response.ListData;
import com.seckill.common.response.SimpleData;
import com.seckill.productservice.dao.ProductTypeDao;
import com.seckill.productservice.service.IFinancialProductService;
import com.seckill.productservice.service.ILoanProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 七画一只妖
 * @Classname OrderController
 * @Date 2022/2/13 19:33
 */
@RestController
@RequestMapping("/product")
public class ProductController {
    private final IFinancialProductService financialProductService;
    private final ILoanProductService loanProductService;

    @Autowired
    public ProductController(IFinancialProductService financialProductService,
                             ILoanProductService loanProductService) {
        this.financialProductService = financialProductService;
        this.loanProductService = loanProductService;
    }

    @Resource
    private ProductTypeDao productTypeDao;

    /**
     * 根据类型新增一个新产品
     * @param type 产品类型
     * @param financialProductEntity financial产品
     * @param loanProductEntity loan产品
     */
    @PostMapping("/{type}/create")
    public Object createNewProduct(@PathVariable("type") String type,
                                   FinancialProductEntity financialProductEntity,
                                   LoanProductEntity loanProductEntity)
    throws Exception{
        if(financialProductEntity.getFinancialProductName() != null){
            financialProductService.addFinancialProduct(financialProductEntity);
        }else if (loanProductEntity.getLoanProductName() != null){
            loanProductService.addLoanProduct(loanProductEntity);
        }
        return DataFactory.success(SimpleData.class,"ok");
    }

    /**
     * 根据ID和产品类型，删除产品
     * @param type 产品类型
     * @param financialProductEntity financial对象
     * @param loanProductEntity loan对象
     */
    @PostMapping("/{type}/delete")
    public Object deleteProduct(@PathVariable("type") String type,
                                FinancialProductEntity financialProductEntity,
                                LoanProductEntity loanProductEntity)
    throws Exception{
        if (financialProductEntity.getFinancialProductName() != null){
            financialProductService.deleteFinancialProduct(financialProductEntity.getFinancialProductId());
        }else if (loanProductEntity.getLoanProductName() != null){
            loanProductService.deleteLoanProduct(loanProductEntity.getLoanProductId());
        }
        return DataFactory.success(SimpleData.class,"ok");
    }

    /**
     * 更新产品
     * @param type 产品类型
     * @param financialProductEntity financial对象
     * @param loanProductEntity loan对象
     */
    @PostMapping("/{type}/update")
    public Object updateProduct(@PathVariable("type") String type,
                                FinancialProductEntity financialProductEntity,
                                LoanProductEntity loanProductEntity)
    throws Exception{
        if (financialProductEntity.getFinancialProductName() != null){
            financialProductService.updateFinancialProduct(financialProductEntity);
        }else if (loanProductEntity.getLoanProductName() != null){
            loanProductService.updateLoanProduct(loanProductEntity);
        }
        return DataFactory.success(SimpleData.class,"ok");
    }

    /**
     * 根据ID查找产品
     * @param id 产品ID
     * @param type 产品类型
     * @return 对象 数据（单个实体）
     */
    @GetMapping("/{type}/getbyid/{id}")
    public Object findFinancialProduct(@PathVariable("id")String id,
                                       @PathVariable("type") String type,
                                       HttpServletRequest request){

        if (request.getHeader(FeignConsts.HEADER_NAME) != null){
            if(type.equals("financial")){
                return financialProductService.findFinancialProductById(id);
            }else if(type.equals("loan")){
                return loanProductService.findLoanProductById(id);
            }
        }else{
            if(type.equals("financial")){
                return DataFactory.success(SimpleData.class, "ok").parseData(financialProductService.findFinancialProductById(id));
            }else if(type.equals("loan")){
                return DataFactory.success(SimpleData.class, "ok").parseData(loanProductService.findLoanProductById(id));
            }
        }
        return DataFactory.fail(CodeEnum.INTERNAL_ERROR,"出现了未知错误");
    }

    /**
     * 查找所有产品
     * @param type 产品类型
     * @return 对象 数据（列表）
     */
    @GetMapping("/{type}/findAll")
    public Object findAllFinancialProduct(@PathVariable("type") String type,
                                          HttpServletRequest request){
        if (request.getHeader(FeignConsts.HEADER_NAME) != null){
            if(type.equals("financial")){
                return financialProductService.findAll();
            }else if(type.equals("loan")){
                return loanProductService.findAll();
            }
        }else{
            if(type.equals("financial")){
                return DataFactory.success(ListData.class, "ok").parseData(financialProductService.findAll());
            }else if(type.equals("loan")){
                return DataFactory.success(ListData.class, "ok").parseData(loanProductService.findAll());
            }
        }
        return DataFactory.fail(CodeEnum.INTERNAL_ERROR,"出现了未知错误");
    }


    /**
     * 名称模糊查询产品
     * @param name 产品名称
     * @param type 产品类型
     * @return 对象 数据（列表）
     */
    @GetMapping("/{type}/findByName/{name}")
    public Object findFinancialProductByName(@PathVariable("name")String name,
                                             @PathVariable("type") String type,
                                             HttpServletRequest request){
        if (request.getHeader(FeignConsts.HEADER_NAME) != null){
            if(type.equals("financial")){
                return financialProductService.findProductByName(name);
            }else if(type.equals("loan")){
                return loanProductService.findProductByName(name);
            }
        }else{
            if(type.equals("financial")){
                return DataFactory.success(ListData.class, "ok").parseData(financialProductService.findProductByName(name));
            }else if(type.equals("loan")){
                return DataFactory.success(ListData.class, "ok").parseData(loanProductService.findProductByName(name));
            }
        }
        return DataFactory.fail(CodeEnum.INTERNAL_ERROR,"出现了未知错误");
    }

    /**
     * 产品的分页查询（对于所有产品）
     * @param type 产品类型
     * @param request req
     * @param page 页数
     * @return 返回数据或者对象
     * @throws Exception 异常
     */
    @GetMapping("/{type}/findAllByPage")
    public Object findAllByPage(@PathVariable("type") String type,
                                HttpServletRequest request,
                                int page) throws Exception{
        if (request.getHeader(FeignConsts.HEADER_NAME) != null){
            if(type.equals("financial")){
                return financialProductService.getProductById(page);
            }else if(type.equals("loan")){
                return loanProductService.getProductById(page);
            }
        }else{
            if(type.equals("financial")){
                return DataFactory.success(ListData.class, "ok").parseData(financialProductService.getProductById(page));
            }else if(type.equals("loan")){
                return DataFactory.success(ListData.class, "ok").parseData(loanProductService.getProductById(page));
            }
        }
        return DataFactory.fail(CodeEnum.INTERNAL_ERROR,"出现了未知错误");
    }

    /**
     * 传入一个id的list，遍历这个list，根据对应的type，查询出对应的产品对象List
     * @param ids 产品类型
     * @return 对象 数据（列表）
     */
    // 传入一个id的list，遍历这个list，根据对应的type，查询出对应的产品对象List
    @PostMapping("/getProductsBatch")
    public Object getProductsBatch(HttpServletRequest request,
                                   @RequestBody List<ProductTypeEntity> ids) throws Exception{
        // 遍历ids，根据ProductTypeEntity中的type属性，查询出对应的产品
        List<Object> flProducts = new ArrayList<>();
        for (ProductTypeEntity productTypeEntity : ids) {
            if(productTypeEntity.getType().equals("financial")){
                flProducts.add(financialProductService.findFinancialProductById(productTypeEntity.getProductId()));
            } else {
                flProducts.add(loanProductService.findLoanProductById(productTypeEntity.getProductId()));
            }
        }
        if (request.getHeader(FeignConsts.HEADER_NAME) != null){
            return flProducts;
        }else {
            return DataFactory.success(ListData.class, "ok").parseData(flProducts);
        }
    }
}
