package org.jeecg.modules.api.entity;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.jeecg.modules.api.entity.ApiData;
import org.jeecg.modules.df.entity.PayOrder;
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

	@NotBlank(message = "bizOrderNo 不能为空")
	@Length(max = 32, message = "bizOrderNo 最大长度为32")
	private String bizOrderNo;

	@NotNull(message = "amount 不能为空")
	private BigDecimal amount;

	@NotBlank(message = "accountType 不能为空")
	@Length(max = 1, message = "accountType 最大长度为1")
	private String accountType;

	@NotBlank(message = "accountName 不能为空")
	@Length(max = 45, message = "accountName 最大长度为45")
	private String accountName;

	@NotBlank(message = "accountNo 不能为空")
	@Length(max = 255, message = "accountNo 最大长度为255")
	private String accountNo;

	@NotBlank(message = "bankName 不能为空")
	@Length(max = 100, message = "bankName 最大长度为100")
	private String bankName;

	@Length(max = 255, message = "branchName 最大长度为255")
	private String branchName;

	@Length(max = 32, message = "bankCode 最大长度为32")
	private String bankCode;

	@Length(max = 32, message = "ip 最大长度为32")
	private String ip;

	@Length(max = 300, message = "callbackUrl 最大长度为300")
	private String callbackUrl;

	@Length(max = 255, message = "remark 最大长度为255")
	private String remark;


	public PayOrder toPayOrder(SysUser u) {
		check();

		PayOrder o = new PayOrder();
		BeanUtil.copyProperties(this, o);
		o.setChannel(productCode);
		o.setOuterOrderId(bizOrderNo);
//		o.setAmount(amount);
//		o.setAccountType(accountType);
//		o.setAccountName(accountName);
		o.setCardNumber(accountNo);
//		o.setBankCode(bankCode);
//		o.setBankName(bankName);
//		o.setBranchName(branchName);
//		o.setIp(ip);
//		o.setCallbackUrl(callbackUrl);
//		o.setRemark(remark);

		o.setUserId(u.getId());
		o.setUserName(u.getUsername());
		o.setUserRealname(u.getRealname());
		o.setAgentId(u.getAgentId());
		o.setAgentUsername(u.getAgentUsername());
		o.setAgentRealname(u.getAgentRealname());
		o.setSalesmanId(u.getSalesmanId());
		o.setSalesmanUsername(u.getSalesmanUsername());
		o.setSalesmanRealname(u.getSalesmanRealname());
		return o;
	}
}
