package org.jeecg.modules.api.extension.niunan;

import org.jeecg.modules.api.extension.PayChannelStrategy;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component("")
public class NiuNanPay implements PayChannelStrategy {
	@Override
	public String pay(OrderInfoEntity orderInfo) {
		return null;
	}

	@Override
	public String callback(OrderInfoEntity orderId, HttpServletRequest req) {
		return null;
	}
}
