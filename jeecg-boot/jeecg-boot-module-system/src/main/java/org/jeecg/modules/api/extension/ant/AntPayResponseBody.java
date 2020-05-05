package org.jeecg.modules.api.extension.ant;

import lombok.Data;

@Data
public class AntPayResponseBody {
	private String code;
	private String msg;
	private String sub_code;
	private String sub_msg;
	private String app_id;
	private String nonce_str;
	private String trade_no;
	private String trade_type;
	private String out_trade_no;
	private String code_url;
	private String code_wap;
	private String sign;

}
