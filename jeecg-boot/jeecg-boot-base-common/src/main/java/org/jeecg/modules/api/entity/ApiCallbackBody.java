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
public class ApiCallbackBody extends ApiBase {
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
	public ApiCallbackBody sign(String apiKey) {
		StringBuilder sb = new StringBuilder();
		sb.append("username=").append(username);
		sb.append("&timestamp=").append(timestamp);
		sb.append("&data=").append(data);
		sb.append("&apiKey=").append(apiKey);
		sign = DigestUtils.md5Hex(sb.toString());
		return this;
	}
}
