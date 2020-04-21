package org.jeecg.modules.pay.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
import lombok.experimental.FieldNameConstants;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 用户关联商户
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
@Data
@TableName("sys_user_business")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_user_business对象", description="用户关联商户")
public class UserBusinessEntity {
    
	/**id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "id")
	private java.lang.String id;
	/**用户id*/
	@Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
	private java.lang.String userId;
	/**用户名*/
	@Excel(name = "用户名", width = 15)
    @ApiModelProperty(value = "用户名")
	private java.lang.String userName;
	/**商户code*/
	@Excel(name = "商户code", width = 15)
    @ApiModelProperty(value = "商户code")
	private java.lang.String businessCode;
	/**通道*/
	@Excel(name = "通道", width = 15)
	@ApiModelProperty(value = "通道")
	private java.lang.String channelCode;
	/**秘钥*/
	@Excel(name = "秘钥", width = 15)
	@ApiModelProperty(value = "秘钥")
	private java.lang.String apiKey;
	/**删除状态，1删除状态*/
	@Excel(name = "删除状态，1删除状态", width = 15)
    @ApiModelProperty(value = "删除状态，1删除状态")
	private java.lang.Integer delFlag;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private java.lang.String createUser;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date createTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
    @ApiModelProperty(value = "更新人")
	private java.lang.String updateUser;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	private java.util.Date updateTime;
	/**
	 * 前端展示用
	 */
	@TableField(exist = false)
	private boolean editable = false;
	/**激活状态
	 *
	 * 0：未激活，1：激活
	 * */
	@Excel(name = "激活状态", width = 2)
	@ApiModelProperty(value = "激活状态")
	private String active;

	@Excel(name = "充值金额", width = 15)
	@ApiModelProperty(value = "充值金额")
	private BigDecimal rechargeAmount;

	@Excel(name = "收入金额", width = 15)
	@ApiModelProperty(value = "收入金额")
	private BigDecimal incomeAmount;

	private String productCode;
}
