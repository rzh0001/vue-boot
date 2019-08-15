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
 * @Description: 提现申请
 * @Author: jeecg-boot
 * @Date: 2019-08-15
 * @Version: V1.0
 */
@Data
@TableName("pay_cash_out_apply")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "pay_cash_out_apply对象", description = "提现申请")
public class CashOutApply {
    
	/**主键id*/
	@TableId(type = IdType.UUID)
	@ApiModelProperty(value = "主键id")
	private java.lang.String id;
	/**用户ID*/
	@Excel(name = "用户ID", width = 15)
    @ApiModelProperty(value = "用户ID")
	private java.lang.String userId;
	/**登录账号*/
	@Excel(name = "登录账号", width = 15)
    @ApiModelProperty(value = "登录账号")
	private java.lang.String username;
	/**提现金额*/
	@Excel(name = "提现金额", width = 15)
    @ApiModelProperty(value = "提现金额")
	private java.math.BigDecimal amount;
	/**
	 * md5密码盐
	 */
	@Excel(name = "md5密码盐", width = 15)
	@ApiModelProperty(value = "md5密码盐")
	private java.lang.String bankCardId;
	/**银行名称*/
	@Excel(name = "银行名称", width = 15)
    @ApiModelProperty(value = "银行名称")
	private java.lang.String bankName;
	/**分支行*/
	@Excel(name = "分支行", width = 15)
    @ApiModelProperty(value = "分支行")
	private java.lang.String branchName;
	/**账户名*/
	@Excel(name = "账户名", width = 15)
    @ApiModelProperty(value = "账户名")
	private java.lang.String accountName;
	/**卡号*/
	@Excel(name = "卡号", width = 15)
    @ApiModelProperty(value = "卡号")
	private java.lang.String cardNumber;
	/**发起时间*/
	@Excel(name = "发起时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "发起时间")
	private java.util.Date applyTime;
	/**审批时间*/
	@Excel(name = "审批时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "审批时间")
	private java.util.Date approvalTime;
	/**
	 * 代理ID
	 */
	@Excel(name = "代理ID", width = 15)
	@ApiModelProperty(value = "代理ID")
	private java.lang.String agentId;
	/**
	 * 代理帐号
	 */
	@Excel(name = "代理帐号", width = 15)
	@ApiModelProperty(value = "代理帐号")
	private java.lang.String agentUsername;
	/**
	 * 代理姓名
	 */
	@Excel(name = "代理姓名", width = 15)
	@ApiModelProperty(value = "代理姓名")
	private java.lang.String agentRealname;
	/**状态(1-待审核;2-通过;3-拒绝)*/
	@Excel(name = "状态(1-待审核;2-通过;3-拒绝)", width = 15)
    @ApiModelProperty(value = "状态(1-待审核;2-通过;3-拒绝)")
	private java.lang.String status;
	/**删除状态（0，正常，1已删除）*/
	@Excel(name = "删除状态（0，正常，1已删除）", width = 15)
    @ApiModelProperty(value = "删除状态（0，正常，1已删除）")
	private java.lang.String delFlag;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private java.lang.String createBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date createTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
    @ApiModelProperty(value = "更新人")
	private java.lang.String updateBy;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	private java.util.Date updateTime;
}
