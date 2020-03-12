package org.jeecg.modules.plugin.entity;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.Data;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Data
public class TengFeiFuQuery {
	private String openid;

	/**
	 * 商户订单号（要求唯一）
	 */
	private String orderNo;

	private String sign;

	public String buildStr() {
		StringBuilder builder = new StringBuilder();
		builder.append("openid=").append(openid);
		builder.append("&orderNo=").append(orderNo);
		if (StrUtil.isNotBlank(sign)) {
			builder.append("&sign=").append(sign);
		}

		return builder.toString();
	}


	public void encode(String apiKey) {
		String s = buildStr() + "&key=" + apiKey;
		System.out.println("加密串：" + s);
		sign = SecureUtil.md5(s).toUpperCase();
	}
}
