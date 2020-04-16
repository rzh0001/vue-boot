package org.jeecg.modules.api.exception;

import org.jeecg.modules.api.exception.ApiException;

/**
 * 缺少必要参数异常
 * @author ruanzh
 * @since 2019/11/17
 */
public class MissingRequiredParameterException extends ApiException {
    public MissingRequiredParameterException(String msg) {
        super(msg);
        setCode(1001);
    }
}
