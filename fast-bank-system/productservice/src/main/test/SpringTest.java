import com.seckill.common.entity.product.FinancialProductEntity;
import com.seckill.productservice.ProductApplication;
import com.seckill.productservice.dao.FinancialProductDao;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: 七画一只妖
 * @Date: 2022-04-01 22:20
 */
@SpringBootTest(classes = ProductApplication.class)
public class SpringTest {

    @Resource
    private FinancialProductDao productDao;

    @Test
    public void test(){
        List<FinancialProductEntity> financialProductEntities =  productDao.selectList(null);
        System.out.println(financialProductEntities);
    }
}
