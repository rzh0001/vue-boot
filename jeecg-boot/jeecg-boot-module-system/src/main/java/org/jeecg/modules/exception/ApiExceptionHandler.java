package org.jeecg.modules.exception;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.df.entity.CommonResponseBody;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ruanzh
 * @since 2019/11/17
 */
@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {
    @ExceptionHandler(value = ApiException.class)
    @ResponseBody
    public CommonResponseBody defaultErrorHandler(HttpServletRequest req, ApiException e) throws Exception {
        log.error(e.getMsg());
        return CommonResponseBody.error(e.getCode(), e.getMsg());
    }
}
