package org.jeecg.modules.api.extension.gm;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.encryption.AES128Util;
import org.jeecg.modules.api.exception.AccountAbnormalException;
import org.jeecg.modules.api.exception.BusinessException;
import org.jeecg.modules.api.extension.PayChannelStrategy;
import org.jeecg.modules.api.extension.gm.entity.*;
import org.jeecg.modules.api.util.HttpRequestUtil;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.pay.service.IOrderToolsService;
import org.jeecg.modules.pay.service.IUserBusinessEntityService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * GM支付接口
 */
@Slf4j
public class GMPay implements PayChannelStrategy {

	@Autowired
	private IUserBusinessEntityService businessService;

	@Autowired
	private IOrderToolsService orderTools;

	@Autowired
	private IOrderInfoEntityService orderService;


	@Override
	public String pay(OrderInfoEntity orderInfo) {
		UserBusinessEntity userChannelConfig = orderTools.getUserChannelConfig(orderInfo);
		String gmApiKey = userChannelConfig.getApiKey();
		if (StringUtils.isBlank(gmApiKey)) {
			log.error("通道未配置apikey");
			throw new AccountAbnormalException("通道未配置apikey");
		}
		String[] keys = gmApiKey.split("=");
		if (keys.length != 2) {
			log.error("通道未配置apikey");
			throw new AccountAbnormalException("通道未配置apikey");
		}
		String md5Key = keys[0];
		String aesKey = keys[1];

		GMRequestData data = new GMRequestData();
		data.setAgentorderid(orderInfo.getOrderId());
		data.setApplyamount(String.valueOf(orderInfo.getSubmitAmount()));
		data.setOrderchannel(6);
		data.setWeb_username(orderInfo.getUserName());
		data.setCallbackurl(orderInfo.getSuccessCallbackUrl());

		GMRequestBody body = new GMRequestBody();
		body.setAgentcode(orderInfo.getBusinessCode());
		body.setData(data.encrypt(aesKey));
		body.setSign(body.sign(md5Key));

		String serverGateway = orderTools.getChannelGateway(orderInfo.getPayType());
		log.info("订单[{}]向[{}]发送请求[{}]", orderInfo.getOrderId(), serverGateway, body.toJsonString());
		String response = HttpUtil.post(serverGateway, body.toJsonString());
		log.info("订单[{}]接受响应[{}]", orderInfo.getOrderId(), response);

		// 处理响应
		GMResponse gmResponse = JSONObject.parseObject(response, GMResponse.class);
		if (gmResponse.getCode() != 0) {
			throw BusinessException.Fuck("支付连接生成失败，请联系管理员。失败原因：{}", gmResponse.getMsg());
		}
		return gmResponse.getPayurl();
	}

	@Override
	public String callback(OrderInfoEntity orderInfo, HttpServletRequest req) {
		// 1.从HttpServletRequest获取数据
//		Map<String, String[]> parameterMap = req.getParameterMap(); //获取以form提交的数据
		String jsonStr = HttpRequestUtil.getJsonString(req); // 获取以json提交的数据
		GMCallbackBody body = JSONObject.parseObject(jsonStr, GMCallbackBody.class);
		// 验签
		UserBusinessEntity userChannelConfig = orderTools.getUserChannelConfig(orderInfo);
		String gmApiKey = userChannelConfig.getApiKey();
		if (StringUtils.isBlank(gmApiKey)) {
			log.error("通道未配置apikey");
			throw new AccountAbnormalException("通道未配置apikey");
		}
		String[] keys = gmApiKey.split("=");
		if (keys.length != 2) {
			log.error("通道未配置apikey");
			throw new AccountAbnormalException("通道未配置apikey");
		}
		String md5Key = keys[0];
		String aesKey = keys[1];

		body.checkSign(md5Key);

		String s = AES128Util.decryptBase64(body.getData(), aesKey);
		GMCallbackData data = JSONObject.parseObject(s, GMCallbackData.class);
		if (data == null) {
			throw BusinessException.Fuck("data数据解密失败");
		}

		//异步通知客户
		orderTools.notifyClient(orderInfo);

		JSONObject json = new JSONObject();
		json.put("code", "200");

		return json.toJSONString();
	}
}
