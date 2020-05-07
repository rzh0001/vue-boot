package org.jeecg.modules.api.service;

import org.jeecg.modules.api.entity.ApiRequestBody;
import org.jeecg.modules.api.entity.ApiResponseBody;
import org.jeecg.modules.api.entity.PayOrderRequestData;
import org.jeecg.modules.api.entity.PayOrderUrlResponse;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.system.entity.SysUser;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface ISfApiService {

	PayOrderUrlResponse createOrder(OrderInfoEntity orderInfo) throws Exception;

	ApiResponseBody queryOrder(String outerOrderId, String username);

	String callback(String payType, String orderId, HttpServletRequest req);

	/**
	 * 校验用户信息
	 * @param reqBody
	 * @return
	 * @throws Exception
	 */
	SysUser verifyUser(ApiRequestBody reqBody)throws Exception;

	/**
	 * 数据解密
	 * @param reqBody
	 * @param user
	 * @return
	 * @throws Exception
	 */
	PayOrderRequestData decodeData(ApiRequestBody reqBody, SysUser user) throws Exception;

	String verifyUserChannel(OrderInfoEntity orderInfo)throws Exception;
}
