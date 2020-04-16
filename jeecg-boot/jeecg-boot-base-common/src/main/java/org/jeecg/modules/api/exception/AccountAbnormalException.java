package org.jeecg.modules.api.exception;

import org.jeecg.modules.api.exception.ApiException;

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
