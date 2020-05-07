package org.jeecg.modules.api.extension.ant;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.util.MD5Util;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.api.exception.SignatureException;
import org.jeecg.modules.api.extension.PayChannelStrategy;
import org.jeecg.modules.api.util.HttpRequestUtil;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.externalUtils.antUtil.AntUtil;
import org.jeecg.modules.pay.service.IOrderToolsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

@Slf4j
@Component("antAlipay")
public class AntPay extends PayChannelStrategy {

	@Autowired
	private IOrderToolsService orderTools;

	@Override
	public String pay(OrderInfoEntity orderInfo) {
		UserBusinessEntity userChannelConfig = orderTools.getUserChannelConfig(orderInfo);

		AntPayRequestBody body = new AntPayRequestBody();
		body.setApp_id(userChannelConfig.getBusinessCode());
		body.setNonce_str(UUIDGenerator.generate());
		body.setTotal_amount(orderInfo.getSubmitAmount().multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString());
		body.setOut_trade_no(orderInfo.getOrderId());
		body.setNotify_url(orderTools.generateCallbackUrl(orderInfo));
		body.setSign(body.sign(userChannelConfig.getApiKey()));

		String serverGateway = orderTools.getChannelGateway(orderInfo.getPayType());
		log.info("订单[{}]向[{}]发送请求[{}]", orderInfo.getOrderId(), serverGateway, body.toJsonString());
		String response = HttpUtil.post(serverGateway, body.toJsonString());
		log.info("订单[{}]接受响应[{}]", orderInfo.getOrderId(), response);

		JSONObject json = JSON.parseObject(response);
		return json.get("code_url").toString();
	}

	@Override
	public Object reply() throws Exception {
		return  "success";
	}

	@Override
	public boolean checkSign(Map<String, Object> map, String apiKey) throws Exception {
		log.info("==>蚁支付，回调参数为：{}", map);
		String sign = (String)map.get("sign");
		map.remove("sign");
		map.remove("code");
		map.remove("msg");
		map.remove("sub_code");
		map.remove("sub_msg");
		String localSign = AntUtil.generateSignature(map, apiKey);
		if (!localSign.equals(sign)) {
			log.info("==>蚁支付验证签名异常，回调签名为：{}，本地签名为：{}", sign, localSign);
			return false;
		}
		return true;
	}

	@Override
	public Map<String, Object> getCallBackParam(Map<String, Object> map) {
		return map;
	}

	@Override
	public boolean checkOrderStatusIsOK(Map<String, Object> map, String apiKey) throws Exception {
		return true;
	}
}
