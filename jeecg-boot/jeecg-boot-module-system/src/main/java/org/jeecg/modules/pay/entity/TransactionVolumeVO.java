package org.jeecg.modules.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: d
 * @Author: jeecg-boot
 * @Date: 2020-04-30
 * @Version: V1.0
 */
@Data
@TableName("TransactionVolume")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "TransactionVolume对象", description = "d")
public class TransactionVolumeVO {

	/**
	 * 代理ID
	 */
	@Excel(name = "代理ID", width = 15)
	@ApiModelProperty(value = "代理ID")
	private java.lang.String agentId;
	/**
	 * 代理姓名
	 */
	@Excel(name = "代理姓名", width = 15)
	@ApiModelProperty(value = "代理姓名")
	private java.lang.String agentRealname;
	/**
	 * 代理帐号
	 */
	@Excel(name = "代理帐号", width = 15)
	@ApiModelProperty(value = "代理帐号")
	private java.lang.String agentUsername;
	/**
	 * amount
	 */
	@Excel(name = "amount", width = 15)
	@ApiModelProperty(value = "amount")
	private java.math.BigDecimal amount;
}
