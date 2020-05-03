package org.jeecg.modules.api.extension.ant;

import org.jeecg.modules.api.extension.PayChannelStrategy;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component("antAlipay")
public class AntPay implements PayChannelStrategy {
	@Override
	public String pay(OrderInfoEntity orderInfo) {
		return null;
	}

	@Override
	public String callback(String orderId, HttpServletRequest req) {
		return null;
	}
}
