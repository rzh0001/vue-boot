package org.jeecg.modules.v2.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.v2.entity.PayProduct;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 产品
 * @Author: jeecg-boot
 * @Date:   2020-05-28
 * @Version: V1.0
 */
public interface PayProductMapper extends BaseMapper<PayProduct> {

    @Select("select * from pay_v2_product")
    List<PayProduct> getAllProducts();

    @Select("update pay_v2_product set del_flag=1 where id=#{id}")
    void deleteById(@Param("id") String id);

    List<PayProduct> getProductsByProductCodes(@Param("productCodes") List<String> productCodes);
}
