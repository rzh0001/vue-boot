package org.jeecg.modules.plugin.entity;

import cn.hutool.core.util.HashUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.MD5;
import lombok.Data;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Data
public class TengFeiFuOrder {
	private String openid;
	/**
	 * 金额，单位元，保留2位小数，例如100.53（最低下单金额为200元）
	 */
	private String orderPrice;
	/**
	 * 商户订单号（要求唯一）
	 */
	private String orderNo;
	/**
	 * 支付类型（微信二维码：WX,支付宝：ZFB）
	 */
	private String payType;
	private String notifyUrl;
	/**
	 * 订单Uninx时间戳如1548434984
	 */
	private String orderTimestamp;
	/**
	 * 订单备注 非必填
	 */
	private String remark;
	private String member_name;
	private String sign;

	public String buildStr() {
		StringBuilder builder = new StringBuilder();
		builder.append("member_name=").append(member_name);
		try {
			builder.append("&notifyUrl=").append(URLEncoder.encode(notifyUrl.toString(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		builder.append("&openid=").append(openid);
		builder.append("&orderNo=").append(orderNo);
		builder.append("&orderPrice=").append(orderPrice);
		builder.append("&orderTimestamp=").append(orderTimestamp);
		builder.append("&payType=").append(payType);
		if (StrUtil.isNotBlank(remark)) {
			builder.append("&remark=").append(remark);
		}
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
