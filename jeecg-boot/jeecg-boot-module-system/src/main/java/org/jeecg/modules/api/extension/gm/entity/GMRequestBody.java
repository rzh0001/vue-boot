package org.jeecg.modules.api.extension.gm.entity;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

@Data
@Slf4j
public class GMRequestBody {

	private String agentcode;
	private String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
	private String sign;
	private String data;

	public String sign(String md5Key) {
		return DigestUtils.md5Hex(agentcode + timestamp + md5Key);
	}

	public String toJsonString() {
		return JSON.toJSONString(this);
	}

}
