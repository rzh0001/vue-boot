package org.jeecg.modules.df.constant;

/**
 * @author ruanzh
 * @since 2019/10/27
 */
public class DfConstant {

	/**
	 * 代付充值流程状态 订单状态：0-已保存;1-已打款,待审核;2-已确认;3-审核拒绝
	 */
	public static final String STATUS_SAVE = "0";
	public static final String RECHARGE_STATUS_PAID = "1";
	public static final String RECHARGE_STATUS_CHECKED = "2";
	public static final String RECHARGE_STATUS_REJECTED = "3";

	/**
	 * 代付充值流程状态 订单状态：0-已保存;1-已接单;2-已支付;3-已拒绝
	 */
	public static final String PAY_STATUS_TAKEN = "1";
	public static final String PAY_STATUS_PAID = "2";
	public static final String PAY_STATUS_REJECTED = "3";


}
