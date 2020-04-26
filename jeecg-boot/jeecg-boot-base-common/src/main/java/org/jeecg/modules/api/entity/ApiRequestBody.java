package org.jeecg.modules.api.entity;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.common.util.encryption.AES128Util;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author ruanzh
 * @since 2019/11/16
 */
@Data
@Slf4j
public class ApiRequestBody {
	@NotBlank(message = "username不能为空")
	private String username;
	@NotBlank(message = "data不能为空")
	private String data;
	@NotNull(message = "timestamp不能为空")
	private Long timestamp;
	private String remark;
	@NotBlank(message = "sign不能为空")
	private String sign;

	public ApiRequestBody() {
	}

	/**
	 * 签名验证算法
	 *
	 * @param apiKey
	 * @return
	 */
	public boolean verifySignature(String apiKey) {
		String localSign = sign(apiKey);
		log.info("===>系统加密的sign值为：{}", localSign);
		log.info("===>商户传递的sign值为：{}", sign);
		return StrUtil.equals(localSign, sign);
	}

	/**
	 * 签名拼接规则
	 *
	 * @param apiKey
	 * @return
	 */
	public String sign(String apiKey) {
		StringBuilder sb = new StringBuilder();
		sb.append("username=").append(username);
		sb.append("&timestamp=").append(timestamp);
		sb.append("&data=").append(data);
		sb.append("&data=").append(data);
		sb.append("&apiKey=").append(apiKey);
		log.info("===>系统拼接的sign串为：{}", sb.toString());
		return DigestUtils.md5Hex(sb.toString());
	}

	public String decodeData(String apiKey) {
		return AES128Util.decryptBase64(data, apiKey);
	}

	public String toJsonString() {
		return JSON.toJSONString(this);
	}
}