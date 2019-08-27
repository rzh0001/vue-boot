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
 * @Description: 用户收入流水详情
 * @Author: jeecg-boot
 * @Date:   2019-08-26
 * @Version: V1.0
 */
@Data
@TableName("sys_user_amount_detail")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_user_amount_detail对象", description="用户收入流水详情")
public class UserAmountDetail {
    
	/**id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "id")
	private java.lang.String id;
	/**userId*/
	@Excel(name = "userId", width = 15)
    @ApiModelProperty(value = "userId")
	private java.lang.String userId;
	/**用户名*/
	@Excel(name = "用户名", width = 15)
    @ApiModelProperty(value = "用户名")
	private java.lang.String userName;
	/**流水类型 1-手续费收入 2-提现 3-冲正*/
	@Excel(name = "流水类型 1-手续费收入 2-提现 3-冲正", width = 15)
    @ApiModelProperty(value = "流水类型 1-手续费收入 2-提现 3-冲正")
	private java.lang.String type;
	/**金额*/
	@Excel(name = "金额", width = 15)
    @ApiModelProperty(value = "金额")
	private java.math.BigDecimal amount;
	/**订单号*/
	@Excel(name = "订单号", width = 15)
    @ApiModelProperty(value = "订单号")
	private java.lang.String orderId;
	/**订单号*/
	@Excel(name = "通道类型", width = 15)
	@ApiModelProperty(value = "通道类型")
	private java.lang.String payType;
	/**费率*/
	@Excel(name = "费率", width = 15)
    @ApiModelProperty(value = "费率")
	private java.lang.String userRate;
	/**代理ID*/
	@Excel(name = "代理ID", width = 15)
    @ApiModelProperty(value = "代理ID")
	private java.lang.String agentId;
	/**代理用户名*/
	@Excel(name = "代理用户名", width = 15)
    @ApiModelProperty(value = "代理用户名")
	private java.lang.String agentUsername;
	/**代理姓名*/
	@Excel(name = "代理姓名", width = 15)
    @ApiModelProperty(value = "代理姓名")
	private java.lang.String agentRealname;
	/**介绍人id*/
	@Excel(name = "介绍人id", width = 15)
    @ApiModelProperty(value = "介绍人id")
	private java.lang.String salesmanId;
	/**介绍人用户名*/
	@Excel(name = "介绍人用户名", width = 15)
    @ApiModelProperty(value = "介绍人用户名")
	private java.lang.String salesmanUsername;
	/**介绍人姓名*/
	@Excel(name = "介绍人姓名", width = 15)
    @ApiModelProperty(value = "介绍人姓名")
	private java.lang.String salesmanRealname;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date createTime;
}
