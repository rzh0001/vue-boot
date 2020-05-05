package org.jeecg.modules.api.entity;

import lombok.Data;

@Data
public class PayOrderUrlResponse {
	private int code;
	private String msg;
	private String url;

	public static PayOrderUrlResponse success(String payUrl) {
		PayOrderUrlResponse response = new PayOrderUrlResponse();
		response.setCode(0);
		response.setMsg("success");
		response.setUrl(payUrl);
		return response;
	}
}
