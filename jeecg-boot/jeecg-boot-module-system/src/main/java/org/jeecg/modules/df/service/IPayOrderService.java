package org.jeecg.modules.df.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.df.entity.PayOrder;
import org.jeecg.modules.api.entity.PayOrderResult;

import java.util.List;
import java.util.Map;

/**
 * @Description: 1
 * @Author: jeecg-boot
 * @Date: 2019-10-27
 * @Version: V1.0
 */
public interface IPayOrderService extends IService<PayOrder> {

	/**
	 * 创建订单
	 *
	 * @return
	 */
	boolean create(PayOrder order);

	/**
	 * 获取分配的订单
	 * @param deviceCode
	 * @return
	 */
	 PayOrder findOrderByDevice(String deviceCode);
	/**
	 * 审核通过
	 *
	 * @param order
	 * @return
	 */
	boolean checked(PayOrder order);

	/**
	 * 审核拒绝
	 *
	 * @param order
	 * @return
	 */
	boolean rejected(PayOrder order);

	/**
	 * 创建api订单
	 *
	 * @param order
	 * @return
	 */

	PayOrderResult apiOrder(PayOrder order);

	int count(String userId, String outerOrderId);

	PayOrder getByOuterOrderId(String outerOrderId);

	Map<String, Object> summary(QueryWrapper<PayOrder> queryWrapper);

	/**
	 * 分配订单
	 * @param userNames
	 * @return
	 */
	PayOrder assignOrderByCreateTime(List<String> userNames);
}
