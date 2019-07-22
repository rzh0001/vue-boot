package org.jeecg.modules.pay.entity;

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
 * @Description: 订单信息
 * @Author: jeecg-boot
 * @Date:   2019-07-22
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
	/**商户编号*/
	@Excel(name = "商户编号", width = 15)
    @ApiModelProperty(value = "商户编号")
	private java.lang.String businessCode;
	/**商户名称*/
	@Excel(name = "商户名称", width = 15)
    @ApiModelProperty(value = "商户名称")
	private java.lang.String businessName;
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
	/**回调地址*/
	@Excel(name = "回调地址", width = 15)
    @ApiModelProperty(value = "回调地址")
	private java.lang.String callbackUrl;
	/**可用金额，即可提现额度*/
	@Excel(name = "可用金额，即可提现额度", width = 15)
    @ApiModelProperty(value = "可用金额，即可提现额度")
	private java.math.BigDecimal availableAmount;
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
