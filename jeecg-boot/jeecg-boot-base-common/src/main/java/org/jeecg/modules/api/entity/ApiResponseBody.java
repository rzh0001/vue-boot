package org.jeecg.modules.api.entity;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * @author ruanzh
 * @since 2019/11/16
 */
@Data
public class ApiResponseBody<T> {
	private int code;
	private String msg;
	private T data;

	private ApiResponseBody() {
		code = 0;
		msg = "success";
	}

	public static ApiResponseBody ok() {
		return new ApiResponseBody();
	}

	public ApiResponseBody(T dataObject) {
		code = 0;
		msg = "success";
		data = dataObject;
	}

	public static ApiResponseBody error(int code, String msg) {
		ApiResponseBody body = new ApiResponseBody();
		body.setCode(code);
		body.setMsg(msg);
		return body;
	}

	public String toJsonString() {
		return JSON.toJSONString(this);
	}

}
