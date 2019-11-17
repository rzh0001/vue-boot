package org.jeecg.modules.exception;

/**
 * @author ruanzh
 * @since 2019/11/17
 */
public class AccountAbnormalException extends ApiException {
    public AccountAbnormalException(String msg) {
        super(msg);
        this.setCode(1002);
    }
}
