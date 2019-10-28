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
 * @Description: 挂马账号收入详情
 * @Author: jeecg-boot
 * @Date:   2019-10-27
 * @Version: V1.0
 */
@Data
@TableName("business_income_log")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="business_income_log对象", description="挂马账号收入详情")
public class BusinessIncomeLog {
    
	/**id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "id")
	private java.lang.String id;
	/**订单号*/
	@Excel(name = "订单号", width = 15)
    @ApiModelProperty(value = "订单号")
	private java.lang.String orderId;
	/**挂马账号*/
	@Excel(name = "挂马账号", width = 15)
    @ApiModelProperty(value = "挂马账号")
	private java.lang.String businessCode;
	/**订单金额*/
	@Excel(name = "订单金额", width = 15)
    @ApiModelProperty(value = "订单金额")
	private java.math.BigDecimal submitamount;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date createTime;
	/**通道*/
	@Excel(name = "通道", width = 15)
    @ApiModelProperty(value = "通道")
	private java.lang.String channelCode;
	/**1:充值；2：收入*/
	@Excel(name = "1:充值；2：收入", width = 15)
    @ApiModelProperty(value = "1:充值；2：收入")
	private java.lang.String type;
}
