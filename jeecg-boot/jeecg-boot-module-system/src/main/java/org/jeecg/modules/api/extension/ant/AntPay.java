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
import org.jeecg.modules.pay.service.IOrderToolsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

@Slf4j
@Component("antAlipay")
public class AntPay implements PayChannelStrategy {

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
	public String callback(OrderInfoEntity orderInfo, HttpServletRequest req) {
		UserBusinessEntity userChannelConfig = orderTools.getUserChannelConfig(orderInfo);
		String jsonStr = HttpRequestUtil.getJsonString(req);
		log.info("订单[{}]回调[{}]", orderInfo.getOrderId(), jsonStr);
		//验签
		checkSign(userChannelConfig.getApiKey(), jsonStr);
		AntPayCallbackBody callback = JSONObject.parseObject(jsonStr, AntPayCallbackBody.class);
		if ("0000".equals(callback.getCode()) & "0000".equals(callback.getSub_code()) & callback.getTrade_status() == 1) {
			log.info("订单[{}]回调处理成功", orderInfo.getOrderId());
			//异步通知客户
			orderTools.orderPaid(orderInfo);
			return "success";
		}
		return "fail";
	}

	private void checkSign(String apikey, String jsonStr) {
		Map map = JSONObject.parseObject(jsonStr);
		String sign = (String) map.get("sign");
		map.remove("sign");
		map.remove("code");
		map.remove("msg");
		map.remove("sub_code");
		map.remove("sub_msg");
		String s = formatUrlMap(map, true, false, false);
		s = s + "&key=" + apikey;
		String localSign = MD5Util.MD5Encode(s, "UTF-8").toUpperCase();
		if (sign.compareTo(localSign) != 0) {
			throw SignatureException.Fuck("验签失败");
		}
	}


	public String formatUrlMap(Map<String, String> paraMap, boolean removeEmptyValue, boolean urlEncode, boolean keyToLower) {
		String buff = "";
		Map<String, String> tmpMap = paraMap;
		//开启空值筛选，则移除数据
		try {
			List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(tmpMap.entrySet());
			// 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
			Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
				@Override
				public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
					return (o1.getKey()).toString().compareTo(o2.getKey());
				}
			});
			// 构造URL 键值对的格式
			StringBuilder buf = new StringBuilder();
			for (Map.Entry<String, String> item : infoIds) {
				if (StringUtils.isNotBlank(item.getKey())) {
					String key = item.getKey();
					String val = item.getValue();
					if (removeEmptyValue && StringUtils.isBlank(val)) {
						continue;
					}
					if (urlEncode) {
						val = URLEncoder.encode(val, "utf-8");
					}
					if (keyToLower) {
						key = key.toLowerCase();
					}
					buf.append(key + "=" + val).append("&");
				}
			}
			buff = buf.toString();
			if (buff.isEmpty() == false) {
				buff = buff.substring(0, buff.length() - 1);
			}
		} catch (Exception e) {
			log.info("异常信息为：{}", e);
			return "";
		}
		return buff;
	}
}
