package org.jeecg.modules.product.service;

import org.jeecg.modules.product.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 产品表
 * @Author: jeecg-boot
 * @Date:   2020-03-05
 * @Version: V1.0
 */
public interface IProductService extends IService<Product> {
    List<Product> getAllProduct();
}
