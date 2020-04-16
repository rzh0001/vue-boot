package org.jeecg.modules.api.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.api.entity.ApiRequestBody;
import org.jeecg.modules.api.entity.ApiResponseBody;
import org.jeecg.modules.api.entity.PayOrderData;
import org.jeecg.modules.api.exception.AccountAbnormalException;
import org.jeecg.modules.api.exception.SignatureException;
import org.jeecg.modules.api.service.ISfApiService;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/order/create", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public ApiResponseBody createPayOrder(@Valid @RequestBody ApiRequestBody req) {
		log.info("=======>商户[{}]创建订单", req.getUsername());

		// 检查商户状态
		SysUser user = userService.getUserByName(req.getUsername());
		if (BeanUtil.isEmpty(user)) {
			log.error("=======>商户[{}]创建订单：商户不存在，请检查参数", req.getUsername());
			throw new AccountAbnormalException("商户不存在，请检查参数");
		}
		// 验签
		if (!req.verifySignature(user.getApiKey())) {
			log.error("=======>商户[{}]创建订单：签名失败", req.getUsername());
			throw new SignatureException("签名失败");
		}
		log.info("=======>商户[{}]创建订单：验签成功", req.getUsername());

		if (!CommonConstant.USER_UNFREEZE.equals(user.getStatus())) {
			log.error("=======>商户[{}]创建订单：商户状态异常，请联系管理员！", req.getUsername());
			throw new AccountAbnormalException("商户状态异常，请联系管理员！");
		}
		// 检查代理状态
		SysUser agent = userService.getUserById(user.getAgentId());
		if (!CommonConstant.USER_UNFREEZE.equals(agent.getStatus())) {
			log.error("=======>商户[{}]创建订单：商户上级代理[{}]状态异常，请联系管理员！", req.getUsername(), agent.getUsername());
			throw new AccountAbnormalException("商户上级代理状态异常，请联系管理员！");
		}
		// 解析数据，验证数据合法性
		String data = req.decodeData(user.getApiKey());
		JSONObject jsonObject = JSONUtil.parseObj(data);
		PayOrderData payOrderData = jsonObject.toBean(PayOrderData.class);
		log.info("=======>商户[{}]创建订单[{}]：解密成功", req.getUsername(), payOrderData.getBizOrderNo());
		log.info("=======>订单[{}]：{}", payOrderData.getBizOrderNo(), payOrderData.toJsonString());
		//

		// 创建订单
		apiService.createOrder(payOrderData);

		return null;
	}
}
