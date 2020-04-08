package org.jeecg.modules.df.entity;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.modules.util.AES128Util;

/**
 * @author ruanzh
 * @since 2019/11/19
 */
@Builder
@Slf4j
public class CallbackBody {
	private String username;
	private String data;
	@Builder.Default
	private Long timestamp = System.currentTimeMillis();
	private String remark;
	private String sign;

	/**
	 * 签名算法
	 *
	 * @param apiKey
	 * @return
	 */
	public CallbackBody sign(String apiKey) {
		StringBuilder sb = new StringBuilder();
		sb.append(username).append(timestamp).append(data).append(apiKey);
		log.info("===>系统拼接的sign串为：{}", sb.toString());
		sign = DigestUtils.md5Hex(sb.toString());
		return this;
	}

	public String decodeData(String apiKey) {
		return AES128Util.decryptBase64(data, apiKey);
	}

	public String toJsonString() {
		return JSON.toJSONString(this);
	}
}
