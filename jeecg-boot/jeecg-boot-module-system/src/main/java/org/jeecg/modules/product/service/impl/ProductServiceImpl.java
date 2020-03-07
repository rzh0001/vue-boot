package org.jeecg.modules.product.service.impl;

import org.jeecg.modules.product.entity.Product;
import org.jeecg.modules.product.mapper.ProductMapper;
import org.jeecg.modules.product.service.IProductService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 产品表
 * @Author: jeecg-boot
 * @Date:   2020-03-05
 * @Version: V1.0
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

}
