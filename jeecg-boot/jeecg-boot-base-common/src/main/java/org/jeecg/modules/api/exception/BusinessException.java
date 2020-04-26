package org.jeecg.modules.api.exception;

/**
 * 业务异常
 *
 * @author ruanzh
 */
public class BusinessException extends ApiException {
	public BusinessException(String msg) {
		super("交易失败，" + msg);
		this.setCode(1003);
	}
}
