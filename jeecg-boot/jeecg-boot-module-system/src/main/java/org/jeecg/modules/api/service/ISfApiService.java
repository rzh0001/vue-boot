package org.jeecg.modules.api.service;

import org.jeecg.modules.api.entity.ApiRequestBody;
import org.jeecg.modules.api.entity.ApiResponseBody;
import org.jeecg.modules.pay.entity.OrderInfoEntity;

public interface ISfApiService {

	ApiResponseBody createOrder(OrderInfoEntity orderInfo);

	ApiResponseBody queryOrder(ApiRequestBody req);

	boolean callback(String orderId);
}
