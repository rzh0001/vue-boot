package org.jeecg.modules.api.service;

import org.jeecg.modules.api.entity.ApiRequestBody;
import org.jeecg.modules.api.entity.ApiResponseBody;
import org.jeecg.modules.api.entity.PayOrderData;

public interface ISfApiService {

	ApiResponseBody createOrder(PayOrderData req);

	ApiResponseBody queryOrder(ApiRequestBody req);

	boolean callback(String orderId);
}
