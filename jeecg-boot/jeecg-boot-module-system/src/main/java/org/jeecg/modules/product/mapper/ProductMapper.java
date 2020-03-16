package org.jeecg.modules.product.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.product.entity.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 产品表
 * @Author: jeecg-boot
 * @Date:   2020-03-05
 * @Version: V1.0
 */
public interface ProductMapper extends BaseMapper<Product> {

    @Select("select * from pay_product")
    List<Product> getAllProduct();
}
