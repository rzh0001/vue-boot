package org.jeecg.modules.df.entity;

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
 * @Description: 代付充值订单
 * @Author: jeecg-boot
 * @Date:   2019-10-26
 * @Version: V1.0
 */
@Data
@TableName("df_recharge_order")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="df_recharge_order对象", description="代付充值订单")
public class RechargeOrder {
    
	/**主键id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "主键id")
	private java.lang.String id;
	/**订单号*/
	@Excel(name = "订单号", width = 15)
    @ApiModelProperty(value = "订单号")
	private java.lang.String orderId;
	/**外部订单号*/
	@Excel(name = "外部订单号", width = 15)
    @ApiModelProperty(value = "外部订单号")
	private java.lang.String outerOrderId;
	/**用户id*/
	@Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
	private java.lang.String userId;
	/**用户*/
	@Excel(name = "用户", width = 15)
    @ApiModelProperty(value = "用户")
	private java.lang.String userName;
	/**用户昵称*/
	@Excel(name = "用户昵称", width = 15)
    @ApiModelProperty(value = "用户昵称")
	private java.lang.String userRealname;
	/**商户编号*/
	@Excel(name = "商户编号", width = 15)
    @ApiModelProperty(value = "商户编号")
	private java.lang.String merchantId;
	/**订单金额*/
	@Excel(name = "订单金额", width = 15)
    @ApiModelProperty(value = "订单金额")
	private java.math.BigDecimal amount;
	/**订单状态：0-已保存;1-已打款,待审核;2-已确认;3-审核拒绝*/
	@Excel(name = "订单状态：0-已保存;1-已打款,待审核;2-已确认;3-审核拒绝", width = 15)
    @ApiModelProperty(value = "订单状态：0-已保存;1-已打款,待审核;2-已确认;3-审核拒绝")
	private java.lang.String status;
	/**银行卡ID*/
	@Excel(name = "银行卡ID", width = 15)
    @ApiModelProperty(value = "银行卡ID")
	private java.lang.String bankcardId;
	/**账户类型(1-对私;2-对公)*/
	@Excel(name = "账户类型(1-对私;2-对公)", width = 15)
    @ApiModelProperty(value = "账户类型(1-对私;2-对公)")
	private java.lang.String accountType;
	/**账户名*/
	@Excel(name = "账户名", width = 15)
    @ApiModelProperty(value = "账户名")
	private java.lang.String accountName;
	/**卡号*/
	@Excel(name = "卡号", width = 15)
    @ApiModelProperty(value = "卡号")
	private java.lang.String cardNumber;
	/**银行名称*/
	@Excel(name = "银行名称", width = 15)
    @ApiModelProperty(value = "银行名称")
	private java.lang.String bankName;
	/**开户行全称*/
	@Excel(name = "开户行全称", width = 15)
    @ApiModelProperty(value = "开户行全称")
	private java.lang.String branchName;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
	private java.lang.String remark;
	/**成功时间*/
	@Excel(name = "成功时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "成功时间")
	private java.util.Date successTime;
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
	/**操作IP*/
	@Excel(name = "操作IP", width = 15)
    @ApiModelProperty(value = "操作IP")
	private java.lang.String ip;
	/**
	 * 付款人
	 */
	@Excel(name = "付款人", width = 15)
	@ApiModelProperty(value = "付款人")
	private java.lang.String payer;
}
