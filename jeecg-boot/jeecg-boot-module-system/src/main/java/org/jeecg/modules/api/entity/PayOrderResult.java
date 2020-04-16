package org.jeecg.modules.api.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.modules.df.entity.PayOrder;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author ruanzh
 * @since 2019/11/17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PayOrderResult extends ApiData {
	private String orderNo;
	private String bizOrderNo;
	private BigDecimal amount;
	private BigDecimal bizFee;
	private Date payTime;
	private String status;

	public static PayOrderResult fromPayOrder(PayOrder o) {
		PayOrderResult data = new PayOrderResult();
		data.setOrderNo(o.getOrderId());
		data.setBizOrderNo(o.getOuterOrderId());
		data.setAmount(o.getAmount());
		data.setBizFee(o.getOrderFee());
		data.setStatus(o.getStatus());
		if (o.getSuccessTime() != null) {
			data.setPayTime(o.getSuccessTime());
		}
		return data;
	}

}
