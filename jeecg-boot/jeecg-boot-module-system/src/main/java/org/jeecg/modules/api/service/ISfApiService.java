package org.jeecg.modules.api.service;

import org.jeecg.modules.api.entity.ApiRequestBody;
import org.jeecg.modules.api.entity.ApiResponseBody;
import org.jeecg.modules.pay.entity.OrderInfoEntity;

import javax.servlet.http.HttpServletRequest;

public interface ISfApiService {

	ApiResponseBody createOrder(OrderInfoEntity orderInfo);

	ApiResponseBody queryOrder(ApiRequestBody req);

	String callback(String payType, HttpServletRequest req);
}
