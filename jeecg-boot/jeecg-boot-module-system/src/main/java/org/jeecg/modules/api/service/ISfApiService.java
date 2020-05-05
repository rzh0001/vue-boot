package org.jeecg.modules.api.service;

import org.jeecg.modules.api.entity.ApiResponseBody;
import org.jeecg.modules.api.entity.PayOrderUrlResponse;
import org.jeecg.modules.pay.entity.OrderInfoEntity;

import javax.servlet.http.HttpServletRequest;

public interface ISfApiService {

	PayOrderUrlResponse createOrder(OrderInfoEntity orderInfo);

	ApiResponseBody queryOrder(String outerOrderId, String username);

	String callback(String payType, String orderId, HttpServletRequest req);
}
