package org.jeecg.modules.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.api.entity.ApiResponseBody;
import org.jeecg.modules.api.entity.PayOrderResponseData;
import org.jeecg.modules.api.entity.PayOrderUrlResponse;
import org.jeecg.modules.api.exception.BusinessException;
import org.jeecg.modules.api.extension.PayChannelContext;
import org.jeecg.modules.api.service.ISfApiService;
import org.jeecg.modules.pay.entity.*;
import org.jeecg.modules.pay.service.IChannelEntityService;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.pay.service.IOrderToolsService;
import org.jeecg.modules.pay.service.IProductService;
import org.jeecg.modules.util.BaseConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Service
public class SfApiServiceImpl implements ISfApiService {

	@Autowired
	IOrderInfoEntityService orderService;

	@Autowired
	IOrderToolsService orderTools;

	@Autowired
	private IProductService productService;

	@Autowired
	private PayChannelContext payChannel;

	@Autowired
	private IChannelEntityService channelEntityService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public PayOrderUrlResponse createOrder(OrderInfoEntity orderInfo) {
		// 检查产品、通道配置
		UserChannelEntity channel =
				productService.getChannelByProduct(orderInfo.getUserName(), orderInfo.getProductCode());
		orderInfo.setOrderId(orderTools.generateOrderId());
		orderInfo.setPayType(channel.getChannelCode());

		UserBusinessEntity userChannelConfig = orderTools.getUserChannelConfig(orderInfo);

		// 交易金额检查
		if (!this.checkSubmitAmountLegal(orderInfo.getSubmitAmount(), channel)) {
			throw new BusinessException("单笔交易金额限额[" + channel.getLowerLimit() + "," + channel.getUpperLimit() + "]");
		}
		orderInfo.setBusinessCode(userChannelConfig.getBusinessCode());
		// 重复订单校验
		orderTools.checkOuterOrderId(orderInfo.getUserName(), orderInfo.getOuterOrderId());
		// 创建本平台订单
		orderInfo.setStatus(BaseConstant.ORDER_STATUS_NOT_PAY);
		orderInfo.setCreateTime(new Date());
		orderInfo.setCreateBy("api");
		// 计算手续费
		String rate = this.getRate(orderInfo.getUserName(), channel.getChannelCode());
		BigDecimal commission =
				orderInfo.getSubmitAmount().multiply(new BigDecimal(rate)).setScale(2, BigDecimal.ROUND_HALF_UP);
		orderInfo.setPoundage(commission);
		orderInfo.setActualAmount(orderInfo.getSubmitAmount().subtract(commission));

		orderService.save(orderInfo);
		// 请求外部平台,生成支付链接
		String payUrl = payChannel.request(orderInfo);
		return PayOrderUrlResponse.success(payUrl);
	}

	@Override
	public ApiResponseBody queryOrder(String outerOrderId, String username) {
		QueryWrapper<OrderInfoEntity> qw = new QueryWrapper<>();
		qw.lambda().eq(OrderInfoEntity::getOuterOrderId, outerOrderId)
				.eq(OrderInfoEntity::getUserName, username);
		OrderInfoEntity orderInfo = orderService.getOne(qw);
		PayOrderResponseData data = PayOrderResponseData.fromPayOrder(orderInfo);

		return new ApiResponseBody(data);
	}


	/**
	 * 校验金额合法性
	 *
	 * @param submitAmount
	 * @param channel
	 * @return
	 */
	private boolean checkSubmitAmountLegal(BigDecimal submitAmount, UserChannelEntity channel) {
		if (channel.getLowerLimit() != null && submitAmount.compareTo(channel.getLowerLimit()) < 0) {
			return false;
		}
		if (channel.getUpperLimit() != null && submitAmount.compareTo(channel.getUpperLimit()) > 0) {
			return false;
		}
		return true;
	}

	/**
	 * 获取用户的通道费率
	 *
	 * @param userName
	 * @param channelCode
	 * @return
	 */
	private String getRate(String userName, String channelCode) {
		UserRateEntity userRate = orderTools.getUserRate(userName, channelCode);
		if (userRate == null) {
			// 如果用户未配置费率，则使用通道费率
			ChannelEntity channel = channelEntityService.queryChannelByCode(channelCode);
			return channel.getRate();
		}
		return userRate.getUserRate();
	}

	@Override
	public String callback(String payType, String orderId, HttpServletRequest req) {
		// 检验回调IP TODO 通道服务器域名、IP统一配置到通道表里，修改后在这里校验对调IP地址

		// 查询订单
		OrderInfoEntity orderInfo = orderTools.queryOrderByOrderIdAndPayType(orderId, payType);
		if (BeanUtil.isEmpty(orderInfo)) {
			throw BusinessException.Fuck("订单[{}]不存在", orderId);
		}
		if (orderInfo.getStatus() == 2) {
			throw BusinessException.Fuck("订单[{}]已成功完结", orderId);
		}

		return payChannel.callback(orderInfo, req);
	}
}
