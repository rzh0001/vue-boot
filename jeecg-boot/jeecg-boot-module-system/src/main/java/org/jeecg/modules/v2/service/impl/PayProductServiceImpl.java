package org.jeecg.modules.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.v2.entity.PayProduct;
import org.jeecg.modules.v2.mapper.PayProductMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 产品
 * @Author: jeecg-boot
 * @Date:   2020-05-28
 * @Version: V1.0
 */
@Service
public class PayProductServiceImpl extends ServiceImpl<PayProductMapper, PayProduct> implements IService<PayProduct> {

    public List<PayProduct> getAllProducts(){
        return getBaseMapper().getAllProducts();
    }

    public PayProduct getProductByProductCode(String produceCode){
        return getBaseMapper().selectOne(new LambdaQueryWrapper<PayProduct>().eq(PayProduct::getProductCode,produceCode));
    }

    public void deleteById(String id){
        getBaseMapper().deleteById(id);
    }

    public List<PayProduct> getProductsByProductCodes(List<String> productCodes){
        return getBaseMapper().getProductsByProductCodes(productCodes);
    }
}
