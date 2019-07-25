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
 * @Description: 用户关联通道信息
 * @Author: jeecg-boot
 * @Date:   2019-07-25
 * @Version: V1.0
 */
@Data
@TableName("sys_channel_user")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_channel_user对象", description="用户关联通道信息")
public class ChannelUserEntity {
    
	/**id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "id")
	private java.lang.String id;
	/**通道用户id*/
	@Excel(name = "通道用户id", width = 15)
    @ApiModelProperty(value = "通道用户id")
	private java.lang.String userId;
	/**通道用户名*/
	@Excel(name = "通道用户名", width = 15)
    @ApiModelProperty(value = "通道用户名")
	private java.lang.String userName;
	/**通道code*/
	@Excel(name = "通道code", width = 15)
    @ApiModelProperty(value = "通道code")
	private java.lang.String channelCode;
	/**状态 0：关闭；1：开启*/
	@Excel(name = "状态 0：关闭；1：开启", width = 15)
    @ApiModelProperty(value = "状态 0：关闭；1：开启")
	private java.lang.Integer status;
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
}
