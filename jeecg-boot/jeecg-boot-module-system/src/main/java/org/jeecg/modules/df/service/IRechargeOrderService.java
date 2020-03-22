package org.jeecg.modules.df.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.df.entity.PayOrder;
import org.jeecg.modules.df.entity.RechargeOrder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @Description: 代付充值订单
 * @Author: jeecg-boot
 * @Date: 2019-10-26
 * @Version: V1.0
 */
public interface IRechargeOrderService extends IService<RechargeOrder> {
	Map<String, Object> summary(QueryWrapper<RechargeOrder> queryWrapper);

	void add(RechargeOrder order);
}
