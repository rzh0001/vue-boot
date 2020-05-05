package org.jeecg.modules.api.extension.ant;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Slf4j
public class AntPayCallbackBody implements Serializable {
	private String code;
	private String msg;
	private String sub_code;
	private String sub_msg;

	private String app_id;
	private String nonce_str;
	private String trade_no;
	private String trade_type;
	private String out_trade_no;
	private BigDecimal total_amount;
	private BigDecimal act_amount;
	private Date deal_time;
	private String notify_url;
	private int trade_status;
	private String sign;


	public String toJsonString() {
		return JSON.toJSONString(this);
	}
}
