package org.jeecg.modules.api.extension;

import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PayChannelContext {
	@Autowired
	private final Map<String, APayChannelStrategy> strategyMap = new ConcurrentHashMap<>();

	public PayChannelContext(Map<String, APayChannelStrategy> strategyMap) {
		strategyMap.forEach(this.strategyMap::put);
	}

	public String request(OrderInfoEntity orderInfo) {
		return strategyMap.get(orderInfo.getPayType()).pay(orderInfo);
	}

	public Object callback(String payType, String orderId) throws Exception {
		return strategyMap.get(payType).callBack(orderId, payType);
	}

}
