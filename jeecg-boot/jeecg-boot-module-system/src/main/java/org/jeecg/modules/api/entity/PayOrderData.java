package org.jeecg.modules.api.entity;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.system.entity.SysUser;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author ruanzh
 * @since 2019/11/16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PayOrderData extends ApiData {
	@NotBlank(message = "productCode 不能为空")
	@Length(max = 45, message = "productCode 最大长度为45")
	private String productCode;

	@NotBlank(message = "outerOrderId 不能为空")
	@Length(max = 32, message = "outerOrderId 最大长度为32")
	private String outerOrderId;

	@NotNull(message = "amount 不能为空")
	private BigDecimal amount;

	@NotNull(message = "callbackUrl 不能为空")
	@Length(max = 360, message = "callbackUrl 最大长度为360")
	private String callbackUrl;

	@NotBlank(message = "clientId 不能为空")
	@Length(max = 45, message = "clientId 最大长度为45")
	private String clientId;

	@Length(max = 32, message = "clientIp 最大长度为32")
	private String clientIp;


	public OrderInfoEntity toPayOrder(SysUser u) {

		OrderInfoEntity o = new OrderInfoEntity();
		BeanUtil.copyProperties(this, o);
		o.setSubmitAmount(amount);
		o.setSuccessCallbackUrl(callbackUrl);
		o.setIp(clientIp);

		o.setUserId(u.getId());
		o.setUserName(u.getUsername());
		o.setUserRealname(u.getRealname());
		o.setAgentId(u.getAgentId());
		o.setAgentUsername(u.getAgentUsername());
		o.setAgentRealname(u.getAgentRealname());
		o.setSalesmanId(u.getSalesmanId());
		o.setSalesmanUsername(u.getSalesmanUsername());
		o.setSalesmanRealname(u.getSalesmanRealname());

		o.setParentUser(u.getAgentUsername());
		return o;
	}
}
