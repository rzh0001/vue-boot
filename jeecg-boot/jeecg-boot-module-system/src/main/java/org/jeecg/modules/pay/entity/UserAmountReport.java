package org.jeecg.modules.pay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 用户余额报表-期初余额 每天0点更新
 * @Author: jeecg-boot
 * @Date:   2019-09-15
 * @Version: V1.0
 */
@Data
@TableName("sys_user_amount_report")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_user_amount_report对象", description="用户余额报表-期初余额 每天0点更新")
public class UserAmountReport {
    
	/**id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "id")
	private java.lang.String id;
	/**用户id*/
    @ApiModelProperty(value = "用户id")
	private java.lang.String userId;
	/**用户名*/
	@Excel(name = "用户名", width = 15)
    @ApiModelProperty(value = "用户名")
	private java.lang.String userName;
	/**报表日期*/
	@Excel(name = "报表日期", width = 15)
    @ApiModelProperty(value = "报表日期")
	private java.lang.String reportDate;
	/**期初金额*/
	@Excel(name = "期初金额", width = 15)
    @ApiModelProperty(value = "期初金额")
	private java.math.BigDecimal originalamount;
	/**支付类型*/
    @ApiModelProperty(value = "支付类型")
	private java.lang.String payType;
	/**今日入金*/
	@Excel(name = "今日入金", width = 15)
    @ApiModelProperty(value = "今日入金")
	private java.math.BigDecimal paidAmount;
	/**今日下发*/
	@Excel(name = "今日下发", width = 15)
    @ApiModelProperty(value = "今日下发")
	private java.math.BigDecimal cashOutAmount;
	/**剩余可提现金额*/
	@Excel(name = "剩余可提现金额", width = 15)
    @ApiModelProperty(value = "剩余可提现金额")
	private java.math.BigDecimal availableAmount;
	/**通道手续费*/
	@Excel(name = "通道手续费", width = 15)
    @ApiModelProperty(value = "通道手续费")
	private java.math.BigDecimal payFee;
	/**代理ID*/
    @ApiModelProperty(value = "代理ID")
	private java.lang.String agentId;
	/**代理账户*/
    @ApiModelProperty(value = "代理账户")
	private java.lang.String agentUsername;
	/**代理姓名*/
    @ApiModelProperty(value = "代理姓名")
	private java.lang.String agentRealname;
	/**介绍人id*/
    @ApiModelProperty(value = "介绍人id")
	private java.lang.String salesmanId;
	/**介绍人账户*/
    @ApiModelProperty(value = "介绍人账户")
	private java.lang.String salesmanUsername;
	/**介绍人姓名*/
    @ApiModelProperty(value = "介绍人姓名")
	private java.lang.String salesmanRealname;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
	private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
	private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	private java.util.Date updateTime;
}
