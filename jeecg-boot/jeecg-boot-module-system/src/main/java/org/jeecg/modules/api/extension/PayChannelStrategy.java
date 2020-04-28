package org.jeecg.modules.api.extension;

import org.jeecg.modules.pay.entity.OrderInfoEntity;

import javax.servlet.http.HttpServletRequest;

public interface PayChannelStrategy {
	/**
	 * 请求外部支付通道
	 *
	 * @param orderInfo
	 * @return
	 */
	String pay(OrderInfoEntity orderInfo);

	String callback(HttpServletRequest req);
}
