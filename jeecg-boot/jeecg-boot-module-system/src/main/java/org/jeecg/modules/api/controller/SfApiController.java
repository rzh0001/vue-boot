package org.jeecg.modules.api.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.api.entity.ApiRequestBody;
import org.jeecg.modules.api.entity.PayOrderRequestData;
import org.jeecg.modules.api.entity.PayOrderUrlResponse;
import org.jeecg.modules.api.exception.AccountAbnormalException;
import org.jeecg.modules.api.exception.BusinessException;
import org.jeecg.modules.api.exception.SignatureException;
import org.jeecg.modules.api.service.ISfApiService;
import org.jeecg.modules.exception.RRException;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.util.BaseConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v2")
@Slf4j
public class SfApiController {

	@Autowired
	private ISfApiService apiService;

	@Autowired
	private ISysUserService userService;

	/**
	 * 订单创建
	 *
	 * @param reqBody
	 * @return
	 */
	@RequestMapping(value = "/order/create", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public PayOrderUrlResponse createPayOrder(@Valid @RequestBody ApiRequestBody reqBody) throws Exception {
		log.info("=======>商户[{}]创建订单", reqBody.getUsername());
		log.info("=======>入参为：{}",reqBody);
		SysUser user = apiService.verifyAccountInfo(reqBody);
		apiService.verifySignature(reqBody,user.getApiKey());
		PayOrderRequestData payOrderData = apiService.decodeData(reqBody,user);
		// 转换订单实体
		OrderInfoEntity orderInfoEntity = payOrderData.toPayOrder(user,reqBody.getRemark());
		// 创建订单
		PayOrderUrlResponse response = apiService.createOrder(orderInfoEntity);
		return response;
	}

	@RequestMapping(value = "/order/callback/{payType}/{orderId}")
	@ResponseBody
	public Object callback(@PathVariable String payType, @PathVariable String orderId) throws Exception {
		return apiService.callback(payType, orderId);
	}


	@RequestMapping(value = "/order/query/{outerOrderId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String callback(@PathVariable String outerOrderId, @Valid @RequestBody ApiRequestBody reqBody) {
		SysUser user = userService.getUserByName(reqBody.getUsername());
		String data = reqBody.decodeData(user.getApiKey());
		JSONObject jsonObject = JSONUtil.parseObj(data);
		if (outerOrderId.compareTo(jsonObject.get("outerOrderId").toString()) != 0) {
			throw BusinessException.Fuck("外部订单号[{}]异常", outerOrderId);
		}

		return apiService.queryOrder(outerOrderId, reqBody.getUsername()).toJsonString();
	}


}
