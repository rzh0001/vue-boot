package org.jeecg.modules.exception;

/**
 * @author ruanzh
 * @since 2019/11/17
 */
public class MissingRequiredParameterException extends ApiException {
    public MissingRequiredParameterException(String msg) {
        super(msg);
        setCode(1001);
    }
}
