package org.jeecg.modules.wallet.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 钱包订单信息
 * @Author: jeecg-boot
 * @Date:   2020-10-12
 * @Version: V1.0
 */
@Data
@TableName("pay_wallet_order_info")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="pay_wallet_order_info对象", description="钱包订单信息")
public class payWalletOrderInfo {
    
	/**主键id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "主键id")
	private java.lang.String id;
	/**四方系统订单号*/
	@Excel(name = "四方系统订单号", width = 15)
    @ApiModelProperty(value = "四方系统订单号")
	private java.lang.String orderId;
	/**钱包地址*/
	@Excel(name = "钱包地址", width = 15)
    @ApiModelProperty(value = "钱包地址")
	private java.lang.String walletUrl;
	/**userName*/
	@Excel(name = "userName", width = 15)
    @ApiModelProperty(value = "userName")
	private java.lang.String userName;
	/**代理*/
	@Excel(name = "代理", width = 15)
    @ApiModelProperty(value = "代理")
	private java.lang.String agentName;
	/**介绍人帐号*/
	@Excel(name = "介绍人帐号", width = 15)
    @ApiModelProperty(value = "介绍人帐号")
	private java.lang.String salesmanUsername;
	/**申请金额，单位为分*/
	@Excel(name = "申请金额，单位为分", width = 15)
    @ApiModelProperty(value = "申请金额，单位为分")
	private java.lang.Integer amount;
	/**币种类型*/
	@Excel(name = "币种类型", width = 15)
    @ApiModelProperty(value = "币种类型")
	private java.lang.String coinType;
	/**币种数量*/
	@Excel(name = "币种数量", width = 15)
    @ApiModelProperty(value = "币种数量")
	private java.math.BigDecimal coinQuantity;
	/**回调地址*/
	@Excel(name = "回调地址", width = 15)
    @ApiModelProperty(value = "回调地址")
	private java.lang.String callbackUrl;
	/**endTime*/
	@Excel(name = "endTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "endTime")
	private java.util.Date endTime;
	/**状态：-1:无效  0:未支付 1:成功，未返回 2:成功，已返回*/
	@Excel(name = "状态：-1:无效  0:未支付 1:成功，未返回 2:成功，已返回", width = 15)
    @ApiModelProperty(value = "状态：-1:无效  0:未支付 1:成功，未返回 2:成功，已返回")
	private java.lang.Integer status;
	/**是否补单 1:是 2：否*/
	@Excel(name = "是否补单 1:是 2：否", width = 15)
    @ApiModelProperty(value = "是否补单 1:是 2：否")
	private java.lang.String reissue;
	/**ip*/
	@Excel(name = "ip", width = 15)
    @ApiModelProperty(value = "ip")
	private java.lang.String ip;
	/**订单备注*/
	@Excel(name = "订单备注", width = 15)
    @ApiModelProperty(value = "订单备注")
	private java.lang.String remark;
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
