package org.jeecg.modules.df.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.df.entity.PayOrder;
import org.jeecg.modules.df.entity.RechargeOrder;
import org.jeecg.modules.df.mapper.RechargeOrderMapper;
import org.jeecg.modules.df.service.IRechargeOrderService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Map;

/**
 * @Description: 代付充值订单
 * @Author: jeecg-boot
 * @Date: 2019-10-26
 * @Version: V1.0
 */
@Service
public class RechargeOrderServiceImpl extends ServiceImpl<RechargeOrderMapper, RechargeOrder> implements IRechargeOrderService {

	@Override
	public Map<String, Object> summary(QueryWrapper<RechargeOrder> queryWrapper) {
		return baseMapper.summary(queryWrapper);
	}
}
