package org.jeecg.modules.v2.entity;

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

import javax.validation.constraints.NotEmpty;

/**
 * @Description: channel
 * @Author: jeecg-boot
 * @Date:   2020-05-28
 * @Version: V1.0
 */
@Data
@TableName("pay_v2_channel")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="pay_v2_channel对象", description="channel")
public class PayChannel {
	/**id*/
	@TableId(type = IdType.UUID)
	@ApiModelProperty(value = "id")
	private java.lang.String id;
	/**通道代码*/
	@Excel(name = "通道代码", width = 15)
    @ApiModelProperty(value = "通道代码")
	@NotEmpty(message = "code不能为空")
	private java.lang.String channelCode;
	/**通道网关*/
	@Excel(name = "通道网关", width = 15)
    @ApiModelProperty(value = "通道网关")
	private java.lang.String channelGateway;
	/**通道IP白名单，使用,分割*/
	@Excel(name = "通道IP白名单，使用,分割", width = 15)
    @ApiModelProperty(value = "通道IP白名单，使用,分割")
	private java.lang.String channelIp;
	/**通道名称*/
	@Excel(name = "通道名称", width = 15)
    @ApiModelProperty(value = "通道名称")
	@NotEmpty(message = "name不能为空")
	private java.lang.String channelName;
	/**通道默认费率*/
	@Excel(name = "通道默认费率", width = 15)
    @ApiModelProperty(value = "通道默认费率")
	@NotEmpty(message = "rate不能为空")
	private java.lang.String channelRate;
	/**删除状态，0:未删除，1删除状态*/
	@Excel(name = "删除状态，0:未删除，1删除状态", width = 15)
    @ApiModelProperty(value = "删除状态，0:未删除，1删除状态")
	private java.lang.Integer delFlag;

	/**状态 0：关闭；1：开启*/
	@Excel(name = "状态 0：关闭；1：开启", width = 15)
    @ApiModelProperty(value = "状态 0：关闭；1：开启")
	private java.lang.Integer status;
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
