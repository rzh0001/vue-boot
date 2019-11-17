package org.jeecg.modules.exception;

/**
 * @author ruanzh
 * @since 2019/11/17
 */
public class SignatureException extends ApiException {
    public SignatureException(String msg) {
        super(msg);
        this.setCode(1003);
    }
}
