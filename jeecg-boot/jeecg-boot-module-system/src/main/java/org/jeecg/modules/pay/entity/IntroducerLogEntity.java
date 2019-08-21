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
 * @Description: 介绍人收入详情日志
 * @Author: jeecg-boot
 * @Date:   2019-08-19
 * @Version: V1.0
 */
@Data
@TableName("sys_introducer_log")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_introducer_log对象", description="介绍人收入详情日志")
public class IntroducerLogEntity {
    
	/**id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "id")
	private java.lang.String id;
	/**订单号*/
	@Excel(name = "订单号", width = 15)
    @ApiModelProperty(value = "订单号")
	private java.lang.String orderId;
	/**介绍人名称*/
	@Excel(name = "介绍人名称", width = 15)
    @ApiModelProperty(value = "介绍人名称")
	private java.lang.String introducerName;
	/**高级代理*/
	@Excel(name = "高级代理", width = 15)
    @ApiModelProperty(value = "高级代理")
	private java.lang.String agentName;
	@Excel(name = "通道", width = 15)
	@ApiModelProperty(value = "通道")
	private java.lang.String channelCode;
	/**费率*/
	@Excel(name = "费率", width = 15)
    @ApiModelProperty(value = "费率")
	private java.lang.String introducerRate;
	/**高级代理的收入*/
	@Excel(name = "高级代理的收入", width = 15)
    @ApiModelProperty(value = "高级代理的收入")
	private java.math.BigDecimal agentSubmitamount;
	/**介绍人获取的利润*/
	@Excel(name = "介绍人获取的利润", width = 15)
    @ApiModelProperty(value = "介绍人获取的利润")
	private java.math.BigDecimal poundage;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date createTime;
}
