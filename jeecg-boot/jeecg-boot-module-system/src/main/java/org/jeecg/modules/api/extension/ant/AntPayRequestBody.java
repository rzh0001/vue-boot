package org.jeecg.modules.api.extension.ant;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.MD5Util;

import java.io.Serializable;

@Data
@Slf4j
public class AntPayRequestBody implements Serializable {

	/**
	 * 商户注册签约后，支付平台分配的唯
	 * 一标识号
	 */
	private String app_id;
	/**
	 * 随机字符串，不长于32位。推荐使用UUID
	 */
	private String nonce_str;
	/**
	 * 支付宝：ali_qr，微信：wx_qr
	 */
	private String trade_type = "ali_qr";
	/**
	 * 订单总金额，单位为分的整数
	 */
	private String total_amount;
	private String out_trade_no;
	private String trade_time = DateUtil.now();
	private String notify_url;
	private String user_ip = "127.0.0.1";
	private String sign;


	public String sign(String apiKey) {
		StringBuilder sb = new StringBuilder();
		sb.append("app_id=").append(app_id);
		sb.append("&nonce_str=").append(nonce_str);
		sb.append("&notify_url=").append(notify_url);
		sb.append("&out_trade_no=").append(out_trade_no);
		sb.append("&total_amount=").append(total_amount);
		sb.append("&trade_time=").append(trade_time);
		sb.append("&trade_type=").append(trade_type);
		sb.append("&user_ip=").append(user_ip);
		sb.append("&key=").append(apiKey);
		log.info("===>系统拼接的sign串为：{}", sb.toString());
		return MD5Util.MD5Encode(sb.toString(), "UTF-8").toUpperCase();
	}


	public String toJsonString() {
		return JSON.toJSONString(this);
	}
}
