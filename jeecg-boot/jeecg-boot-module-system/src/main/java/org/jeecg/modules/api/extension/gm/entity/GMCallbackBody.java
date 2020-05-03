package org.jeecg.modules.api.extension.gm.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.modules.api.exception.BusinessException;

@Data
@Slf4j
public class GMCallbackBody {
	private String username;
	private String timestamp;
	private String sign;
	private String data;

	public void checkSign(String apiKey) {
		String tmp = username + timestamp + data + apiKey;
		if (sign.compareTo(DigestUtils.md5Hex(tmp)) != 0) {
			throw BusinessException.Fuck("GM验签失败，请联系管理员");
		}
	}
}
