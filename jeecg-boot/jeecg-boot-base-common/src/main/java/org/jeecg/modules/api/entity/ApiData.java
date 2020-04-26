package org.jeecg.modules.api.entity;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.encryption.AES128Util;
import org.jeecg.modules.api.exception.MissingRequiredParameterException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * API请求数据体data基类
 */
@Data
@Slf4j
public class ApiData {

	/**
	 * 根据实体注解校验参数合法性
	 * -> 如何拓展：若实体需要逻辑性校验，可在实体类重写此方法 { super.checkData(); ....;}
	 */
	public void checkData() {
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
		log.info("JSON序列化[{}]", JSON.toJSONString(this));
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
