package org.jeecg.modules.api.extension.niunan;

import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.api.extension.APayChannelStrategy;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.externalUtils.niunan.NiunanUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("")
public class NiuNanAPay extends APayChannelStrategy {
	@Override
	public String pay(OrderInfoEntity orderInfo) {
		return null;
	}

	@Override
	public Object reply() throws Exception {
		return "success";
	}

	@Override
	public boolean checkSign(Map<String, Object> map, String apiKey) throws Exception {
		log.info("==>牛腩支付 回调，回调参数为：{}", map);
		String merCode = (String)map.get("merCode");
		String orderNo = (String)map.get("orderNo");
		String orderAmount = (String)map.get("orderAmount");
		String payDate = (String)map.get("payDate");
		String payCompletionDate = (String)map.get("payCompletionDate");
		String resultCode = (String)map.get("resultCode");
		String resultStatus = (String)map.get("resultStatus");
		String resultMsg = (String)map.get("resultMsg");
		String resultTime = (String)map.get("resultTime");
		String sign = (String)map.get("sign");
		StringBuilder md5buffer = new StringBuilder();
		md5buffer.append("merCode=").append(merCode).append("&orderAmount=").append(orderAmount).append("&orderNo=")
			.append(orderNo).append("&payCompletionDate=").append(payCompletionDate).append("&payDate=").append(payDate)
			.append("&resultCode=").append(resultCode).append("&resultStatus=").append(resultStatus)
			.append("&resultTime=").append(resultTime);
		if (StringUtils.isNotBlank(resultMsg)) {
			md5buffer.append("&resultMsg=").append(resultMsg);
		}
		md5buffer.append(apiKey);

		log.info("==>牛腩支付 回调，签名：{}", md5buffer.toString());
		String localSign = NiunanUtils.md5Hash(md5buffer.toString());
		log.info("==>牛腩支付 回调，MD5 :{}", localSign);
		if (!localSign.equals(sign)) {
			log.error("==>牛腩支付 签名验证不通过，入参sign:{},本地：{}", sign, localSign);
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
