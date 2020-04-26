package org.jeecg.modules.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.api.entity.ApiRequestBody;
import org.jeecg.modules.api.entity.ApiResponseBody;
import org.jeecg.modules.api.exception.BusinessException;
import org.jeecg.modules.api.extension.PayChannelContext;
import org.jeecg.modules.api.service.ISfApiService;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.entity.UserChannelEntity;
import org.jeecg.modules.pay.entity.UserRateEntity;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.pay.service.IOrderToolsService;
import org.jeecg.modules.pay.service.IProductService;
import org.jeecg.modules.util.BaseConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Service
public class ISfApiServiceImpl implements ISfApiService {

	@Autowired
	IOrderInfoEntityService orderService;

	@Autowired
	IOrderToolsService orderTools;

	@Autowired
	private IProductService productService;

	@Autowired
	private PayChannelContext payChannel;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ApiResponseBody createOrder(OrderInfoEntity orderInfo) {

		// 检查产品、通道配置
		UserChannelEntity channel = productService.getChannelByProduct(orderInfo.getUserName(), orderInfo.getProductCode());
		orderInfo.setOrderId(orderTools.generateOrderId());
		orderInfo.setPayType(channel.getChannelCode());

		UserBusinessEntity userChannelConfig = orderTools.getUserChannelConfig(orderInfo);

		// 交易金额检查
		if (orderInfo.getSubmitAmount().compareTo(channel.getLowerLimit()) < 0 ||
				orderInfo.getSubmitAmount().compareTo(channel.getUpperLimit()) > 0) {
			throw new BusinessException("单笔交易金额限额[" + channel.getLowerLimit() + "," + channel.getUpperLimit() + "]");
		}
		orderInfo.setBusinessCode(userChannelConfig.getBusinessCode());
		UserRateEntity rate = orderTools.getUserRate(orderInfo.getUserName(), channel.getChannelCode());
		// 重复订单校验
		orderTools.checkOuterOrderId(orderInfo.getUserName(), orderInfo.getOuterOrderId());
		// 创建本平台订单
		orderInfo.setStatus(BaseConstant.ORDER_STATUS_NOT_PAY);
		orderInfo.setCreateTime(new Date());
		orderInfo.setCreateBy("api");
		// 计算手续费
		BigDecimal commission = orderInfo.getSubmitAmount().multiply(new BigDecimal(rate.getUserRate())).setScale(2, BigDecimal.ROUND_HALF_UP);
		orderInfo.setPoundage(commission);
		orderInfo.setActualAmount(orderInfo.getSubmitAmount().subtract(commission));

		orderService.save(orderInfo);
		// 请求外部平台
		String response = payChannel.request(orderInfo);
		throw BusinessException.Fuck(response);
//		return null;
	}

	@Override
	public ApiResponseBody queryOrder(ApiRequestBody req) {
		return null;
	}

	@Override
	public boolean callback(String orderId) {
		return false;
	}
}
