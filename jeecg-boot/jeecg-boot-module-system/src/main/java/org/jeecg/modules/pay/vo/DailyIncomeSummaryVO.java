package org.jeecg.modules.pay.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 今日交易统计
 * @Author: jeecg-boot
 * @Date:   2019-09-02
 * @Version: V1.0
 */
@Data
public class DailyIncomeSummaryVO {
	
	/**用户id*/
	@Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
	private java.lang.String userId;
	/**用户名*/
	@Excel(name = "用户名", width = 15)
    @ApiModelProperty(value = "用户名")
	private java.lang.String username;
	/**用户名称*/
	@Excel(name = "用户名称", width = 15)
    @ApiModelProperty(value = "用户名称")
	private java.lang.String userRealname;
	/**
	 * 会员类型(1：代理  2：介绍人 3：商户）
	 */
	private String memberType;
	private String payType;
	/**总订单数*/
	@Excel(name = "总订单数", width = 15)
    @ApiModelProperty(value = "总订单数")
	private java.lang.Integer totalOrderCount;
	/**总订单金额*/
	@Excel(name = "总订单金额", width = 15)
    @ApiModelProperty(value = "总订单金额")
	private java.math.BigDecimal totalOrderAmount;
	/**已付订单数*/
	@Excel(name = "已付订单数", width = 15)
    @ApiModelProperty(value = "已付订单数")
	private java.lang.Integer paidOrderCount;
	/**已付订单金额*/
	@Excel(name = "已付订单金额", width = 15)
    @ApiModelProperty(value = "已付订单金额")
	private java.math.BigDecimal paidOrderAmount;
	/**未付订单数*/
	@Excel(name = "未付订单数", width = 15)
    @ApiModelProperty(value = "未付订单数")
	private java.lang.Integer unpaidOrderCount;
	/**未付订单金额*/
	@Excel(name = "未付订单金额", width = 15)
    @ApiModelProperty(value = "未付订单金额")
	private java.math.BigDecimal unpaidOrderAmount;
	/**收入*/
	@Excel(name = "手续费", width = 15)
	@ApiModelProperty(value = "手续费")
	private java.math.BigDecimal payFee;
	/**代理ID*/
	@Excel(name = "代理ID", width = 15)
    @ApiModelProperty(value = "代理ID")
	private java.lang.String agentId;
	/**代理帐号*/
	@Excel(name = "代理帐号", width = 15)
    @ApiModelProperty(value = "代理帐号")
	private java.lang.String agentUsername;
	/**代理姓名*/
	@Excel(name = "代理姓名", width = 15)
    @ApiModelProperty(value = "代理姓名")
	private java.lang.String agentRealname;
	/**介绍人ID*/
	@Excel(name = "介绍人ID", width = 15)
    @ApiModelProperty(value = "介绍人ID")
	private java.lang.String salesmanId;
	/**介绍人帐号*/
	@Excel(name = "介绍人帐号", width = 15)
    @ApiModelProperty(value = "介绍人帐号")
	private java.lang.String salesmanUsername;
	/**介绍人姓名*/
	@Excel(name = "介绍人姓名", width = 15)
    @ApiModelProperty(value = "介绍人姓名")
	private java.lang.String salesmanRealname;
	
	//	@Excel(name = "日期", width = 20, format = "YYYY-MM-dd")
//	@JsonFormat(timezone = "GMT+8", pattern = "YYYY-MM-dd")
//	@DateTimeFormat(pattern = "YYYY-MM-dd")
	@ApiModelProperty(value = "日期")
	private String transTime;
}
