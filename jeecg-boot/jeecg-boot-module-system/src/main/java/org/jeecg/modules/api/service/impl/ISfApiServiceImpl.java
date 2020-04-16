package org.jeecg.modules.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.api.entity.ApiRequestBody;
import org.jeecg.modules.api.entity.ApiResponseBody;
import org.jeecg.modules.api.entity.PayOrderData;
import org.jeecg.modules.api.service.ISfApiService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ISfApiServiceImpl implements ISfApiService {
	@Override
	public ApiResponseBody createOrder(PayOrderData req) {
		return null;
	}

	@Override
	public ApiResponseBody queryOrder(ApiRequestBody req) {
		return null;
	}

	@Override
	public boolean callback(String orderId) {
		return false;
	}
}
