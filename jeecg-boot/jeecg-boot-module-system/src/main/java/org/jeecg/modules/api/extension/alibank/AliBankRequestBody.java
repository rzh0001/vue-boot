package org.jeecg.modules.api.extension.alibank;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.modules.pay.entity.OrderInfoEntity;

@Data
@Slf4j
public class AliBankRequestBody {
	private String account_id;
	private String content_type;
	private String thoroughfare;
	private String type;
	private String out_trade_no;
	private String robin;
	private String keyId;
	private String amount;
	private String callback_url;
	private String success_url;
	private String error_url;
	private String sign;
	private String userName;
	private String payType;


	public String sign(OrderInfoEntity order, String key) {
		StringBuilder sb = new StringBuilder();
		sb.append(key).append(order.getSubmitAmount()).append(order.getOrderId());
		log.info("===支付宝签名内容===》：{}", sb.toString());
		return DigestUtils.md5Hex(sb.toString());
	}

	public String toJsonString() {
		return JSON.toJSONString(this);
	}

}
