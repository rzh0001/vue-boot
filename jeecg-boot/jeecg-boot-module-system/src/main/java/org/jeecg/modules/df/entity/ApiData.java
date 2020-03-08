package org.jeecg.modules.df.entity;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.jeecg.modules.exception.MissingRequiredParameterException;
import org.jeecg.modules.util.AES128Util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * API请求数据体data基类
 */
@Data
public class ApiData {

	public void check() {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<ApiData>> validate = validator.validate(this);
		if (!validate.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			validate.forEach(item -> {
				builder.append("[").append(item.getMessage()).append("]").append(System.lineSeparator());
			});
			throw new MissingRequiredParameterException(builder.toString());
		}
	}

	public String toJsonString() {
		return JSON.toJSONString(this);
	}

	/**
	 * data数据加密算法
	 *
	 * @param apiKey 用户接口密钥
	 * @return
	 */
	public String encodeData(String apiKey) {
		return AES128Util.encryptBase64(toJsonString(), apiKey);
	}

}
