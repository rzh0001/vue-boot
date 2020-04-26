package org.jeecg.modules.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.api.entity.ApiResponseBody;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * API异常处理器
 *
 * @author ruanzh
 * @since 2019/11/17
 */
@RestControllerAdvice
@Order(1)
@Slf4j
public class ApiExceptionHandler {
	@ExceptionHandler(value = ApiException.class)
	@ResponseBody
	public ApiResponseBody defaultErrorHandler(HttpServletRequest req, ApiException e) {
		log.error(e.getMsg());
		return ApiResponseBody.error(e.getCode(), e.getMsg());
	}
}
