package com.seckill.productservice.controller;

import com.seckill.common.entity.product.FinancialProductEntity;
import com.seckill.common.entity.product.LoanProductEntity;
import com.seckill.productservice.service.IFinancialProductService;
import com.seckill.productservice.service.ILoanProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 七画一只妖
 * @Classname OrderController
 * @Date 2022/2/13 19:33
 */
@Controller
@RequestMapping("/product")
public class ProductController {
    private final IFinancialProductService financialProductService;
    private final ILoanProductService loanProductService;

    @Autowired
    public ProductController(IFinancialProductService financialProductService, ILoanProductService loanProductService) {
        this.financialProductService = financialProductService;
        this.loanProductService = loanProductService;
    }

    /**
     * 根据类型新增一个新产品
     * @param type 产品类型
     * @param financialProductEntity financial产品
     * @param loanProductEntity loan产品
     * @return 布尔值 是否成功
     */
    @PostMapping("/{type}/create")
    public Boolean createNewProduct(@PathVariable("type") String type,
                                    FinancialProductEntity financialProductEntity,LoanProductEntity loanProductEntity){
        if(financialProductEntity.getFinancialProductName() != null){
            return financialProductService.addFinancialProduct(financialProductEntity);
        }else if (loanProductEntity.getLoanProductName() != null){
            return loanProductService.addLoanProduct(loanProductEntity);
        }else{
            return false;
        }
    }

    /**
     * 根据ID和产品类型，删除产品
     * @param type 产品类型
     * @param id 产品ID
     * @param financialProductEntity financial对象
     * @param loanProductEntity loan对象
     * @return 布尔值 是否成功
     */
    @GetMapping("/{type}/delete/{id}")
    public Boolean deleteProduct(@PathVariable("type") String type, @PathVariable("id") Long id,
                                 FinancialProductEntity financialProductEntity,LoanProductEntity loanProductEntity){
        if (financialProductEntity.getFinancialProductName() != null){
            return financialProductService.deleteFinancialProduct(id);
        }else if (loanProductEntity.getLoanProductName() != null){
            return loanProductService.deleteLoanProduct(id);
        }else{
            return false;
        }
    }

    /**
     * 更新产品
     * @param type 产品类型
     * @param financialProductEntity financial对象
     * @param loanProductEntity loan对象
     * @return 布尔值 是否成功
     */
    @PostMapping("/{type}/update")
    public Boolean updateProduct(@PathVariable("type") String type,
                                 FinancialProductEntity financialProductEntity,LoanProductEntity loanProductEntity){
        if (financialProductEntity.getFinancialProductName() != null){
            return financialProductService.updateFinancialProduct(financialProductEntity);
        }else if (loanProductEntity.getLoanProductName() != null){
            return loanProductService.updateLoanProduct(loanProductEntity);
        }else{
            return false;
        }
    }

    /**
     * 根据ID查找financial产品
     * @param id 产品ID
     * @return FinancialProductEntity 产品对象
     */
    @GetMapping("/financial/find/{id}")
    public FinancialProductEntity findFinancialProduct(@PathVariable("id")long id){
        return financialProductService.findFinancialProductById(id);
    }

    /**
     * 根据ID查找loan产品
     * @param id 产品ID
     * @return LoanProductEntity 产品对象
     */
    @GetMapping("/loan/findById/{id}")
    public LoanProductEntity findLoanProduct(@PathVariable("id")long id){
        return loanProductService.findLoanProductById(id);
    }

    /**
     * 查找所有financial产品
     * @return List<FinancialProductEntity> financial产品列表
     */
    @GetMapping("/financial/findAll")
    public List<FinancialProductEntity> findAllFinancialProduct(){
        return financialProductService.findAll();
    }

    /**
     * 查找所有loan产品
     * @return List<LoanProductEntity> loan产品列表
     */
    @GetMapping("/loan/findAll")
    public List<LoanProductEntity> findAllLoanProduct(){
        return loanProductService.findAll();
    }

    /**
     * 名称模糊查询financial产品
     * @param name 查询名
     * @return List<FinancialProductEntity> financial产品列表
     */
    @GetMapping("/financial/findByName/{name}")
    public List<FinancialProductEntity> findFinancialProductByName(@PathVariable("name")String name){
        return financialProductService.findProductByName(name);
    }

    /**
     * 名称模糊查询loan产品
     * @param name 查询名
     * @return List<LoanProductEntity> loan产品列表
     */
    @GetMapping("/financial/loan/{name}")
    public List<LoanProductEntity> findLoanProductByName(@PathVariable("name")String name){
        return loanProductService.findProductByName(name);
    }
}
