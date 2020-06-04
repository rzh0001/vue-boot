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

/**
 * @Description: 子账号信息
 * @Author: jeecg-boot
 * @Date:   2020-06-01
 * @Version: V1.0
 */
@Data
@TableName("pay_v2_business")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="pay_v2_business对象", description="子账号信息")
public class PayBusiness {
	@TableField(exist = false)
	private Integer key;
	@TableField(exist = false)
	private boolean editable = false;
	/**最近一次使用时间*/
	@Excel(name = "最近一次使用时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "最近一次使用时间")
	private java.util.Date lastUsedTime;
	/**挂马账号是否激活 0:未激活；1：激活*/
	@Excel(name = "挂马账号是否激活 0:未激活；1：激活", width = 15)
    @ApiModelProperty(value = "挂马账号是否激活 0:未激活；1：激活")
	private java.lang.String businessActiveStatus;
	/**挂马账号apikey*/
	@Excel(name = "挂马账号apikey", width = 15)
    @ApiModelProperty(value = "挂马账号apikey")
	private java.lang.String businessApiKey;
	/**挂码账号*/
	@Excel(name = "挂码账号", width = 15)
    @ApiModelProperty(value = "挂码账号")
	private java.lang.String businessCode;
	/**挂马账号收入*/
	@Excel(name = "挂马账号收入", width = 15)
    @ApiModelProperty(value = "挂马账号收入")
	private java.math.BigDecimal businessIncomeAmount;
	/**挂马账号充值金额*/
	@Excel(name = "挂马账号充值金额", width = 15)
    @ApiModelProperty(value = "挂马账号充值金额")
	private java.math.BigDecimal businessRechargeAmount;
	/**通道code*/
	@Excel(name = "通道code", width = 15)
    @ApiModelProperty(value = "通道code")
	private java.lang.String channelCode;
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
	/**删除状态，0：未删除，1删除状态*/
	@Excel(name = "删除状态，0：未删除，1删除状态", width = 15)
    @ApiModelProperty(value = "删除状态，0：未删除，1删除状态")
	private java.lang.Integer delFlag;
	/**id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "id")
	private java.lang.String id;
	/**产品code*/
	@Excel(name = "产品code", width = 15)
    @ApiModelProperty(value = "产品code")
	private java.lang.String productCode;
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
	/**用户名*/
	@Excel(name = "用户名", width = 15)
    @ApiModelProperty(value = "用户名")
	private java.lang.String userName;
}
