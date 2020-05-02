package org.jeecg.modules.pay.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.api.exception.AccountAbnormalException;
import org.jeecg.modules.api.exception.BusinessException;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.entity.UserRateEntity;
import org.jeecg.modules.pay.mapper.OrderInfoEntityMapper;
import org.jeecg.modules.pay.service.IOrderToolsService;
import org.jeecg.modules.pay.service.IUserBusinessEntityService;
import org.jeecg.modules.pay.service.IUserRateEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class OrderToolsServiceImpl implements IOrderToolsService {
	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private OrderInfoEntityMapper orderMapper;

	@Autowired
	IUserRateEntityService rateService;

	@Autowired
	IUserBusinessEntityService businessService;

	@Override
	public void checkOuterOrderId(String username, String outerOrderId) {
		String redisKey = username + "-" + outerOrderId;
		String redisValue = (String) redisUtil.get(redisKey);
		log.info("===>校验订单号[{}]，redis[{}]-[{}]", outerOrderId, redisKey, redisValue);
		//如果redis中存在值，则说明该订单是重复创建的
		if (StrUtil.isNotBlank(redisValue)) {
			throw new BusinessException("订单号重复");
		}
		boolean r = redisUtil.setIfAbsent(redisKey, DateUtil.now(), 100);
		if (!r) {
			log.info("==>redis setnx 返回false,redisKey[{}]", redisKey);
			throw new BusinessException("订单号重复");
		}
		int count = orderMapper.checkOuterOrderId(outerOrderId, username);
		if (count > 0) {
			throw new BusinessException("订单号重复");
		}
	}

	@Override
	public synchronized String generateOrderId() {
		StringBuilder sb = new StringBuilder();
		String dateStr = DateUtils.date2Str(DateUtils.yyyymmddhhmmss);
		String randomStr = RandomStringUtils.randomAlphabetic(5);
		return sb.append(dateStr).append(randomStr).toString();
	}

	@Override
	public UserRateEntity getUserRate(String username, String channelCode) {
		return rateService.getUserRate(username, channelCode);
	}

//	@Override
////	public UserBusinessEntity getUserChannelConfig(String userId, String channelCode) {
////		return businessService.getUserChannelConfig(userId, channelCode);
////	}

	@Override
	public UserBusinessEntity getUserChannelConfig(OrderInfoEntity orderInfo) {
		//校验在此通道下的该商户对应的代理是否有定义挂码账号
		List<UserBusinessEntity> userBusinesses = businessService.queryBusinessCodeByUserName(orderInfo.getAgentUsername(), orderInfo.getPayType());
		if (CollectionUtils.isEmpty(userBusinesses)) {
			throw AccountAbnormalException.Fuck("通道[{}]未配置账号或账号未激活，请联系管理员", orderInfo.getPayType());
		}
		Collections.shuffle(userBusinesses);
		for (UserBusinessEntity b : userBusinesses) {
			BigDecimal total = b.getIncomeAmount() == null ? orderInfo.getSubmitAmount() : b.getIncomeAmount().add(orderInfo.getSubmitAmount());
			if (b.getRechargeAmount() == null || b.getRechargeAmount().compareTo(total) < 0) {
				continue;
			}
			return b;
		}
		throw AccountAbnormalException.Fuck("通道[{}]余额不足，请联系管理员", orderInfo.getPayType());

	}

}
