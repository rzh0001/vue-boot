package org.jeecg.modules.pay.service;

import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.entity.UserRateEntity;

/**
 * 订单工具类
 * 因 OrderService 太庞大，将一些关联性不强的代码独立出来
 *
 * @author ruanzh
 */
public interface IOrderToolsService {
	/**
	 * 检查外部订单号唯一性
	 *
	 * @param username
	 * @param outerOrderId
	 */
	void checkOuterOrderId(String username, String outerOrderId);

	/**
	 * 生成四方系统的订单号，线程安全，保证生成的订单不一致
	 *
	 * @return
	 */
	String generateOrderId();

	/**
	 * 获取用户通道费率设置
	 *
	 * @param username
	 * @param channelCode
	 * @return
	 */
	UserRateEntity getUserRate(String username, String channelCode);

	UserBusinessEntity getUserChannelConfig(OrderInfoEntity orderInfo);

//	UserBusinessEntity getUserChannelConfig(String userId, String channelCode);

}
