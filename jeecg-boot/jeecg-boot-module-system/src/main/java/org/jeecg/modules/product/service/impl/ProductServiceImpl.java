package org.jeecg.modules.product.service.impl;

import org.jeecg.modules.product.entity.Product;
import org.jeecg.modules.product.mapper.ProductMapper;
import org.jeecg.modules.product.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 产品表
 * @Author: jeecg-boot
 * @Date:   2020-03-05
 * @Version: V1.0
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    @Autowired
    private ProductMapper mapper;
    @Override
    public List<Product> getAllProduct() {
        return mapper.getAllProduct();
    }

    @Override
    public List<Product> getProductByCodes(List<String> codes) {
        return mapper.getProductByCodes(codes);
    }
}
