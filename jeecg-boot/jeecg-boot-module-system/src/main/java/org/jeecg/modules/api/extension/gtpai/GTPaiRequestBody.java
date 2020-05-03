package org.jeecg.modules.api.extension.gtpai;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.MD5Util;

import java.io.Serializable;

/**
 * @Author: heihei
 * @Date: 2020/4/20 21:14
 */
@Data
@Slf4j
public class GTPaiRequestBody implements Serializable {

	/**
	 * 机构号
	 */
	private String store_id = "9999";

	//交易商户号
	private String mch_id;
	/**
	 * 31：银联快捷
	 * 37：网关支付
	 * 35：手QH5
	 * 41：支付宝H5
	 * 38：快捷支付
	 * 51：复制转卡H5
	 * 53：支付宝个码H5
	 * 54：复制网银转账
	 */
	private String pay_type = "53";

	/**
	 * 订单总金额，单位为分的整数
	 */
	private String trans_amt;
	private String out_trade_no;
	private String body = "alipay";
	private String notify_url;
	private String sign;

	public String sign(String apiKey) {
		StringBuilder sb = new StringBuilder();
		sb.append("body=").append(body);
		sb.append("&mch_id=").append(mch_id);
		sb.append("&notify_url=").append(notify_url);
		sb.append("&out_trade_no=").append(out_trade_no);
		sb.append("&pay_type=").append(pay_type);
		sb.append("&store_id=").append(store_id);
		sb.append("&trans_amt=").append(trans_amt);
		sb.append("&key=").append(apiKey);
		log.info("===>系统拼接的sign串为：{}", sb.toString());
		return MD5Util.MD5Encode(sb.toString(), "UTF-8").toUpperCase();
	}

	public String toJsonString() {
		return JSON.toJSONString(this);
	}
}
