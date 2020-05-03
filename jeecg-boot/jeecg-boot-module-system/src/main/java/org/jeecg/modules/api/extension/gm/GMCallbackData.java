package org.jeecg.modules.api.extension.gm;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GMCallbackData {
	private String orderId;
	private String payType;
	private BigDecimal amount;
}
