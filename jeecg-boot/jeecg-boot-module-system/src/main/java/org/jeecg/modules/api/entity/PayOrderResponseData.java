package org.jeecg.modules.api.entity;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import org.jeecg.modules.pay.entity.OrderInfoEntity;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PayOrderResponseData {

	private String orderId;
	private String outerOrderId;
	private String userName;
	private BigDecimal submitAmount;
	private BigDecimal poundage;
	private Integer status;
	private Date successTime;
	private String remark;
	private String productCode;

	public static PayOrderResponseData fromPayOrder(OrderInfoEntity orderInfo) {
		PayOrderResponseData r = new PayOrderResponseData();
		BeanUtil.copyProperties(orderInfo, r);
		return r;
	}

}
