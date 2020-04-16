package org.jeecg.modules.api.exception;

import org.jeecg.modules.api.exception.ApiException;

/**
 * 签名异常
 * @author ruanzh
 * @since 2019/11/17
 */
public class SignatureException extends ApiException {
    public SignatureException(String msg) {
        super(msg);
        this.setCode(1003);
    }
}
