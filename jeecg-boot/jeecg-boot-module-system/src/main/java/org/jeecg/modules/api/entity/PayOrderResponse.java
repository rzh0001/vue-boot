package org.jeecg.modules.api.entity;

import lombok.Data;

@Data
public class PayOrderResponse {
	int code;
	String msg;
	String url;

	public static PayOrderResponse success(String payUrl) {
		PayOrderResponse response = new PayOrderResponse();
		response.setCode(0);
		response.setMsg("success");
		response.setUrl(payUrl);
		return response;
	}
}
