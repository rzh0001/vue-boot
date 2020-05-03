package org.jeecg.modules.api.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.api.entity.ApiRequestBody;
import org.jeecg.modules.api.entity.PayOrderData;
import org.jeecg.modules.api.entity.PayOrderResponse;
import org.jeecg.modules.api.exception.AccountAbnormalException;
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
	public PayOrderResponse createPayOrder(@Valid @RequestBody ApiRequestBody reqBody) {
		log.info("=======>商户[{}]创建订单", reqBody.getUsername());

		// 检查账户状态
		SysUser user = userService.getUserByName(reqBody.getUsername());
		if (BeanUtil.isEmpty(user)) {
			log.error("=======>商户[{}]创建订单：商户不存在，请检查参数", reqBody.getUsername());
			throw new AccountAbnormalException("商户不存在，请检查参数");
		}
		if (!BaseConstant.USER_MERCHANTS.equals(user.getMemberType())) {
			log.info("用户类型不是商户，无法提交订单，用户名为：{}", reqBody.getUsername());
			throw new RRException("用户类型不是商户，无法提交订单");
		}
		// 验签
		if (!reqBody.verifySignature(user.getApiKey())) {
			log.error("=======>商户[{}]创建订单：签名失败", reqBody.getUsername());
			throw new SignatureException("签名失败");
		}
		log.info("=======>商户[{}]创建订单：验签成功", reqBody.getUsername());

		if (!CommonConstant.USER_UNFREEZE.equals(user.getStatus())) {
			log.error("=======>商户[{}]创建订单：商户状态异常，请联系管理员！", reqBody.getUsername());
			throw new AccountAbnormalException("商户状态异常，请联系管理员！");
		}
		// 检查代理状态
		SysUser agent = userService.getUserById(user.getAgentId());
		if (!CommonConstant.USER_UNFREEZE.equals(agent.getStatus())) {
			log.error("=======>商户[{}]创建订单：商户上级代理[{}]状态异常，请联系管理员！", reqBody.getUsername(), agent.getUsername());
			throw new AccountAbnormalException("商户上级代理状态异常，请联系管理员！");
		}
		// 解析数据，验证数据合法性
		String data = reqBody.decodeData(user.getApiKey());
		JSONObject jsonObject = JSONUtil.parseObj(data);
		PayOrderData payOrderData = jsonObject.toBean(PayOrderData.class);
		log.info("=======>商户[{}]创建订单[{}]：解密成功", reqBody.getUsername(), payOrderData.getOuterOrderId());
		log.info("=======>订单[{}]：{}", payOrderData.getOuterOrderId(), payOrderData.toJsonString());
		//
		// 检查参数合法性
		payOrderData.checkData();

		// 转换订单实体
		OrderInfoEntity orderInfoEntity = payOrderData.toPayOrder(user);
		orderInfoEntity.setRemark(reqBody.getRemark());
		// 创建订单
		PayOrderResponse response = apiService.createOrder(orderInfoEntity);

		return response;
	}

	@RequestMapping(value = "/order/callback/{payType}/{orderId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String callback(@PathVariable String payType, @PathVariable String orderId, HttpServletRequest req) {
		log.info("payType={}", payType);
		apiService.callback(payType, orderId, req);
		return "ok";
	}


}
