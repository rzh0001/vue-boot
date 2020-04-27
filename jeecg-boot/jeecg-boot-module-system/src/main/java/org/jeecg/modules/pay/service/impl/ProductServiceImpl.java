package org.jeecg.modules.pay.service.impl;

import org.jeecg.modules.pay.entity.Product;
import org.jeecg.modules.pay.mapper.ProductMapper;
import org.jeecg.modules.pay.service.IProductService;
import org.jeecg.modules.api.exception.BusinessException;
import org.jeecg.modules.pay.entity.UserChannelEntity;
import org.jeecg.modules.pay.service.IUserChannelEntityService;
import org.jeecg.modules.pay.service.IProductChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Description: 产品表
 * @Author: jeecg-boot
 * @Date: 2020-03-05
 * @Version: V1.0
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

	@Autowired
	private IProductChannelService productChannelService;

	@Autowired
	private IUserChannelEntityService channelUserDao;

	@Override
	public List<Product> getAllProduct() {
		return baseMapper.getAllProduct();
	}

	@Override
	public List<Product> getProductByCodes(List<String> codes) {
		return baseMapper.getProductByCodes(codes);
	}

	@Override
	public UserChannelEntity getChannelByProduct(String userName, String product) {
		//根据product获取通道
		List<String> channels = productChannelService.getChannelByProductCode(product);
		if (CollectionUtils.isEmpty(channels)) {
			throw new BusinessException("未配置支付通道，请联系管理员");
		}
		//根据 通道列表和用户，查看用户具备哪些通道
		List<UserChannelEntity> channelCodes = channelUserDao.getUserChannel(channels, userName);
		if (CollectionUtils.isEmpty(channelCodes)) {
			throw new BusinessException("未配置支付通道，请联系管理员");
		}
		UserChannelEntity channel = channelCodes.get(0);
		channelUserDao.updateUseTime(channel.getChannelCode(), userName);
		return channel;
	}
}
