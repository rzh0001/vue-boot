package org.jeecg.common.constant;

/**
 * 支付常量
 *
 * @author ruanzh
 * @date 2019-08-04
 */
public interface PayConstant {

	/**
	 * 会员类型
	 */
	public static final String MEMBER_TYPE_AGENT = "1";
	public static final String MEMBER_TYPE_SALESMAN = "2";
	public static final String MEMBER_TYPE_MEMBER = "3";


	public static final String ROLE_CODE_AGENT = "pay_agent";
	public static final String ROLE_CODE_SALESMAN = "pay_salesman";
	public static final String ROLE_CODE_MEMBER = "pay_member";


	public static final String REDIS_CHANNEL_CONFIG = "channelConfig";
}
