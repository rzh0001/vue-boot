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
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 用户关联通道
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
@Data
@TableName("sys_user_channel")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_user_channel对象", description="用户关联通道")
public class UserChannelEntity {
    
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
	/**会员类型(1：代理  2：介绍人 3：商户）*/
	@Excel(name = "会员类型(1：代理  2：介绍人 3：商户）", width = 15)
	@ApiModelProperty(value = "会员类型(1：代理  2：介绍人 3：商户）")
	private java.lang.String memberType;
	/**通道id*/
	@Excel(name = "通道id", width = 15)
    @ApiModelProperty(value = "通道id")
	private java.lang.String channelId;
	/**通道code*/
	@Excel(name = "通道code", width = 15)
    @ApiModelProperty(value = "通道code")
	private java.lang.String channelCode;
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

	//高级代理才有的字段
	/**商户code*/
	@Excel(name = "商户code", width = 15)
	@ApiModelProperty(value = "商户code")
	@TableField(exist = false)
	private java.lang.String businessCode;
	/**秘钥*/
	@Excel(name = "秘钥", width = 15)
	@ApiModelProperty(value = "秘钥")
	@TableField(exist = false)
	private java.lang.String apiKey;

	/**单笔金额上限*/
	@Excel(name = "单笔金额上限", width = 15)
	@ApiModelProperty(value = "单笔金额上限")
	private BigDecimal upperLimit;
	/**单笔金额下限*/
	@Excel(name = "单笔金额下限", width = 15)
	@ApiModelProperty(value = "单笔金额下限")
	private BigDecimal lowerLimit;

	/**当天交易金额上限*/
	@TableField(exist = false)
	@Excel(name = "当天交易金额上限", width = 15)
	@ApiModelProperty(value = "当天交易金额上限")
	private BigDecimal todayMaxAmount;
}
