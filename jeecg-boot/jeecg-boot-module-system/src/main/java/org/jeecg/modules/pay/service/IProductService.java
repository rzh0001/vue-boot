package org.jeecg.modules.pay.service;

import org.jeecg.modules.pay.entity.Product;
import org.jeecg.modules.pay.entity.UserChannelEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 产品表
 * @Author: jeecg-boot
 * @Date: 2020-03-05
 * @Version: V1.0
 */
public interface IProductService extends IService<Product> {
	List<Product> getAllProduct();

	List<Product> getProductByCodes(List<String> codes);

	/**
	 * 根据产品名称获取通道
	 *
	 * @param userName
	 * @param product
	 * @return
	 */
	UserChannelEntity getChannelByProduct(String userName, String product);
}
