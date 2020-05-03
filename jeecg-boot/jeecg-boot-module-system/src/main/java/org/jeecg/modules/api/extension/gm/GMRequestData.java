package org.jeecg.modules.api.extension.gm;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.encryption.AES128Util;

@Data
@Slf4j
public class GMRequestData {
	private int orderchannel;
	private String trscode = "dynamicunionpay";
	private String agentorderid;
	private String applyamount;
	private String web_username;
	private String clienttype = "H5";
	private String callbackurl;

	public String encrypt(String aesKey) {

		return AES128Util.encryptBase64(toJsonString(), aesKey);
	}

	public String toJsonString() {
		return JSON.toJSONString(this);
	}

}
