package org.jeecg.modules.api.extension;

import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PayChannelContext {
	@Autowired
	private final Map<String, PayChannelStrategy> strategyMap = new ConcurrentHashMap<>();

	public PayChannelContext(Map<String, PayChannelStrategy> strategyMap) {
		strategyMap.forEach(this.strategyMap::put);
	}

	public String request(OrderInfoEntity orderInfo) {
		return strategyMap.get(orderInfo.getPayType()).pay(orderInfo);
	}

	public String callback(OrderInfoEntity orderInfo, HttpServletRequest req) {
		return strategyMap.get(orderInfo.getPayType()).callback(orderInfo, req);
	}

}
