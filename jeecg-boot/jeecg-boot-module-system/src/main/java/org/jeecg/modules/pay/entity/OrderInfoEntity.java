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

import java.math.BigDecimal;

/**
 * @Description: 订单信息
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
@Data
@TableName("pay_order_info")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="pay_order_info对象", description="订单信息")
public class OrderInfoEntity {
    
	/**主键id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "主键id")
	private java.lang.String id;
	/**四方系统订单号*/
	@Excel(name = "四方系统订单号", width = 15)
    @ApiModelProperty(value = "四方系统订单号")
	private java.lang.String orderId;
	/**外部订单号*/
	@Excel(name = "外部订单号", width = 15)
    @ApiModelProperty(value = "外部订单号")
	private java.lang.String outerOrderId;
	/**用户id*/
	@Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
	private java.lang.String userId;
	/**用户id*/
	@Excel(name = "用户名", width = 15)
	@ApiModelProperty(value = "用户名")
	private java.lang.String userName;
	/**真实姓名*/
	@Excel(name = "真实姓名", width = 15)
	@ApiModelProperty(value = "真实姓名")
	private java.lang.String userRealname;
	/**上级用户id*/
	@Excel(name = "上级用户id", width = 15)
    @ApiModelProperty(value = "上级用户id")
	private java.lang.String parentUser;
	/**商户编号*/
	@Excel(name = "商户编号", width = 15)
    @ApiModelProperty(value = "商户编号")
	private java.lang.String businessCode;
	/**申请金额*/
	@Excel(name = "申请金额", width = 15)
    @ApiModelProperty(value = "申请金额")
	private java.math.BigDecimal submitAmount;
	/**手续费*/
	@Excel(name = "手续费", width = 15)
    @ApiModelProperty(value = "手续费")
	private java.math.BigDecimal poundage;
	/**实际金额*/
	@Excel(name = "实际金额", width = 15)
    @ApiModelProperty(value = "实际金额")
	private java.math.BigDecimal actualAmount;
	/**状态：-1:无效  0:未支付 1:成功，未返回 2:成功，已返回*/
	@Excel(name = "状态：-1:无效  0:未支付 1:成功，未返回 2:成功，已返回", width = 15)
    @ApiModelProperty(value = "状态：-1:无效  0:未支付 1:成功，未返回 2:成功，已返回")
	private java.lang.Integer status;
	/**支付通道*/
	@Excel(name = "支付通道", width = 15)
    @ApiModelProperty(value = "支付通道")
	private java.lang.String payType;
	/**成功回调地址*/
	@Excel(name = "成功回调地址", width = 15)
    @ApiModelProperty(value = "成功回调地址")
	private java.lang.String successCallbackUrl;
	/**失败回调地址*/
	@Excel(name = "失败回调地址", width = 15)
    @ApiModelProperty(value = "失败回调地址")
	private java.lang.String errCallbackUrl;
	/**可用金额，即可提现额度*/
	@Excel(name = "可用金额，即可提现额度", width = 15)
    @ApiModelProperty(value = "可用金额，即可提现额度")
	private java.math.BigDecimal availableAmount;
	/**
	 * 成功时间
	 */
	@Excel(name = "成功时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
	/**
	 * 是否补单 1:是 2：否
	 */
	/**介绍人姓名*/
	@Excel(name = "是否补单", width = 15)
	@ApiModelProperty(value = "是否补单")
	private String replacementOrder;

	/**
	 * 支付金额
	 */
	@Excel(name = "支付金额", width = 15)
	@ApiModelProperty(value = "支付金额")
	private BigDecimal paymentAmount;


	@Excel(name = "ip", width = 15)
	@ApiModelProperty(value = "ip")
	private String ip;

	private String remark;

	private String productCode;
}
