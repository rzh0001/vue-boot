package org.jeecg.modules.api.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author ruanzh
 * @since 2019/11/19
 */
@Data
@Slf4j
public class CallbackBody extends ApiData {
	private String username;
	private String data;
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
}
